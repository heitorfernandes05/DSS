package projeto.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import projeto.business.SSAdministracao.Mensagem;
import projeto.business.SSAdministracao.User;

public class MensagemDAO {

    private static MensagemDAO singleton = null;

    private MensagemDAO() {
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
            Statement stm = conn.createStatement()) {
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
    }

    public static MensagemDAO getInstance() {
        if (MensagemDAO.singleton == null) {
            MensagemDAO.singleton = new MensagemDAO();
        }
        return MensagemDAO.singleton;
    }

     
    public int size() {
        int i = 0;
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT count(*) FROM mensagens")) {
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

     
    public boolean isEmpty() {
        return this.size() == 0;
    }

     
    public boolean containsKey(Object key) {
        boolean r;
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
            PreparedStatement pstm = conn.prepareStatement("SELECT id FROM mensagens WHERE id=?")) {
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

     
    public boolean containsValue(Object value) {
        Mensagem t = (Mensagem) value;
        return this.containsKey(t.getIdMensagem());
    }

     
    public Mensagem get(Object key) {
        Mensagem r = null;
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
            PreparedStatement pstm = conn.prepareStatement("SELECT * FROM mensagens WHERE id = ?")) { 
            pstm.setString(1, key.toString());
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    String id = rs.getString("id");
                    String conteudo = rs.getString("conteudo");
                    LocalDateTime timestampEnvio = rs.getTimestamp("timestampEnvio").toLocalDateTime();
                    String autor = rs.getString("autor");
                    User autorUser = UserDAO.getInstance().get(autor);
                    r = new Mensagem(id, conteudo, autorUser, timestampEnvio);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return r;
    }

     
    public Mensagem put(String key, String idRest, Mensagem r) {
        Mensagem res = null;
        
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             Statement stm = conn.createStatement()) {
            res = this.get(key); // obter o valor anterior, caso exista
            // Atualizar Mensagem
            Timestamp timestampEnvioSQL = java.sql.Timestamp.valueOf(r.getTimestampEnvio());
            stm.executeUpdate(
                    "INSERT INTO mensagens VALUES ('"+ r.getIdMensagem()+ "', '"+r.getConteudo()+"', '"+r.getAutor().getEmail()+"', '"+timestampEnvioSQL+"', '" + idRest +  "') " +
                                "ON DUPLICATE KEY UPDATE conteudo=VALUES(conteudo), autor=VALUES(autor), timestampEnvio=VALUES(timestampEnvio), idRestaurante=VALUES(idRestaurante)");
            
        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return res;
    }

     
    public Mensagem remove(Object key) {
        Mensagem t = this.get(key);
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             PreparedStatement mensagens_pstm = conn.prepareStatement("DELETE FROM mensagens WHERE  id=?");) {
            mensagens_pstm.setString(1, key.toString());
            mensagens_pstm.executeUpdate();
        } catch (Exception e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return t;
    }
     
    public void clear() {
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             Statement stm = conn.createStatement()) {
            stm.executeUpdate("TRUNCATE mensagens");
        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
    }

     
    public Set<String> keySet() {
        HashSet<String> ret = new HashSet<String>();
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT id FROM mensagens")) { 
            while (rs.next())
                ret.add(rs.getString(1));
        } catch (Exception e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return ret;
    }

     
    public Collection<Mensagem> values() {
        Collection<Mensagem> res = new HashSet<>();
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT id FROM mensagens")) { // ResultSet com os ids de todas as mensagens
            while (rs.next()) {
                String idr = rs.getString(1); 
                Mensagem t = this.get(idr); 
                res.add(t);                         
            }
        } catch (Exception e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return res;
    }


    public Set<Entry<String, Mensagem>> entrySet() {
        Set<Entry<String, Mensagem>> res = new TreeSet<>();
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT id FROM mensagens")) { // ResultSet com os ids de todas as mensagens
            while (rs.next()) {
                String idr = rs.getString(1); 
                Mensagem r = this.get(idr); 
                Entry<String, Mensagem> entry = new SimpleEntry<>(idr, r);
                res.add(entry);                         
            }
        } catch (Exception e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return res;
    }

    public Collection<Mensagem> getMensagensAtivas(String idRestaurante) {
        Collection<Mensagem> res = new HashSet<>();
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT id, idRestaurante FROM mensagens WHERE idRestaurante='" + idRestaurante + "'")) {
            while (rs.next()) {
                String idr = rs.getString(1); 
                Mensagem t = this.get(idr); 
                res.add(t);                         
            }
        } catch (Exception e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return res;
    }
}

