package projeto.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import projeto.business.SSStock.PostoTrabalho;

public class PostoTrabalhoDAO implements Map<String, PostoTrabalho> {

    private static PostoTrabalhoDAO singleton = null;

    private PostoTrabalhoDAO() {
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
            Statement stm = conn.createStatement()) {
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
    }

    public static PostoTrabalhoDAO getInstance() {
        if (PostoTrabalhoDAO.singleton == null) {
            PostoTrabalhoDAO.singleton = new PostoTrabalhoDAO();
        }
        return PostoTrabalhoDAO.singleton;
    }

    @Override
    public int size() {
        int i = 0;
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT count(*) FROM postos")) {
            if(rs.next()) {
                i = rs.getInt(1);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return i;
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        boolean r;
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             PreparedStatement pstm = conn.prepareStatement("SELECT id FROM postos WHERE id=?")) {
            pstm.setString(1, key.toString());
            try (ResultSet rs = pstm.executeQuery()) {
                r = rs.next();  // A chave existe na tabela
            }
        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return r;
    }

    @Override
    public boolean containsValue(Object value) {
        PostoTrabalho t = (PostoTrabalho) value;
        return this.containsKey(t.getIdPosto());
    }

    @Override
    public PostoTrabalho get(Object key) {
        PostoTrabalho r = null;
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             PreparedStatement pstm = conn.prepareStatement("SELECT * FROM postos WHERE id=?")) {
            pstm.setString(1, key.toString());
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {  // A chave existe na tabela
                    String id = rs.getString("id");
                    String funcao = rs.getString("funcao");
                    String restId = rs.getString("idRestaurante");
                    r = new PostoTrabalho(id, funcao, restId);
                }
            }
        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return r;
    }


    @Override
    public PostoTrabalho put(String key, PostoTrabalho p) {
        PostoTrabalho posto = null;
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             Statement stm = conn.createStatement()) {
            posto = this.get(key); // obter o valor anterior, caso exista
            stm.executeUpdate(
                    "INSERT INTO postos " +
                                "VALUES ('"+ p.getIdPosto()+ "', '"+ p.getFuncao()+"', '"+p.getIdRestaurante()+"') " +
                                "ON DUPLICATE KEY UPDATE funcao=VALUES(funcao), idRestaurante=VALUES(idRestaurante)");
            
        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return posto;
    }

    @Override
    public PostoTrabalho remove(Object key) {
        PostoTrabalho t = this.get(key);
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             PreparedStatement postos_pstm = conn.prepareStatement("DELETE FROM postos WHERE id=?");){

            postos_pstm.setString(1, key.toString());
            postos_pstm.executeUpdate();
        } catch (Exception e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return t;
    }

    @Override
    public void putAll(Map<? extends String, ? extends PostoTrabalho> postos) {
        for(PostoTrabalho r : postos.values()) {
            this.put(r.getIdPosto(), r);
        }
    }

    @Override
    public void clear() {
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             Statement stm = conn.createStatement()) {
            stm.executeUpdate("UPDATE solicitacoes SET id=NULL");
            stm.executeUpdate("TRUNCATE postos");
        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
    }

    @Override
    public Set<String> keySet() {
        HashSet<String> ret = new HashSet<String>();
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT id FROM postos")) { 
            while (rs.next())
                ret.add(rs.getString(1));
        } catch (Exception e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return ret;
    }

    @Override
    public Collection<PostoTrabalho> values() {
        Collection<PostoTrabalho> res = new HashSet<>();
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT id FROM postos")) { // ResultSet com os ids de todas as postos
            while (rs.next()) {
                String idr = rs.getString(1); 
                PostoTrabalho t = this.get(idr); 
                res.add(t);                         
            }
        } catch (Exception e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return res;
    }


    @Override
    public Set<Entry<String, PostoTrabalho>> entrySet() {
        Set<Entry<String, PostoTrabalho>> res = new TreeSet<>();
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT id FROM postos")) { // ResultSet com os ids de todas as postos
            while (rs.next()) {
                String idr = rs.getString(1); 
                PostoTrabalho r = this.get(idr); 
                Entry<String, PostoTrabalho> entry = new SimpleEntry<>(idr, r);
                res.add(entry);                         
            }
        } catch (Exception e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return res;
    }
}
