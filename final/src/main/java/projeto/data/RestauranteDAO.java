package projeto.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.Duration;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import projeto.business.SSAdministracao.Indicadores;
import projeto.business.SSAdministracao.Restaurante;

public class RestauranteDAO implements Map<String, Restaurante> {

    private static RestauranteDAO singleton = null;

    private RestauranteDAO() {
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
            Statement stm = conn.createStatement()) {
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
    }

    public static RestauranteDAO getInstance() {
        if (RestauranteDAO.singleton == null) {
            RestauranteDAO.singleton = new RestauranteDAO();
        }
        return RestauranteDAO.singleton;
    }

    @Override
    public int size() {
        int i = 0;
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT count(*) FROM restaurantes")) {
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
             PreparedStatement pstm = conn.prepareStatement("SELECT id FROM restaurantes WHERE id=?")) {
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
        Restaurante t = (Restaurante) value;
        return this.containsKey(t.getIdRestaurante());
    }

    @Override
    public Restaurante get(Object key) {
        Restaurante r = null;
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             PreparedStatement pstm = conn.prepareStatement("SELECT * FROM restaurantes WHERE id=?")) {
            pstm.setString(1, key.toString());
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {  // A chave existe na tabela
                    String id = rs.getString("id");
                    String morada = rs.getString("morada");
                    Indicadores indicadores = getIndicadoresRestaurante(key.toString(), conn);
                    r = new Restaurante(id, morada, indicadores);
                }
            }
        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return r;
    }

    private Indicadores getIndicadoresRestaurante(String restId, Connection conn) throws SQLException {
        try (PreparedStatement pstm = conn.prepareStatement(
                "SELECT * FROM indicadores WHERE id=?")) {
            pstm.setString(1, restId);
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    double faturacao = rs.getDouble("faturacaoTotal");
                    Time tempo= rs.getTime("tempoMedioAtendimento");
                    Duration tempoMedioAtendimento = Duration.ofSeconds(tempo.getTime()/1000);
                    int numPedidos = rs.getInt("numPedidos");
                    return new Indicadores(faturacao, tempoMedioAtendimento, numPedidos);
                } else {
                    return new Indicadores();
                }
            }
        }
    }


    @Override
    public Restaurante put(String key, Restaurante r) {
        Restaurante res = null;
        Indicadores i = r.getIndicadores();
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             Statement stm = conn.createStatement()) {
            res = this.get(key); // obter o valor anterior, caso exista
            // Atualizar Indicadores
            Time tempoMedio = new Time(i.getTempoMedioAtendimento().toMillis());
            stm.executeUpdate(
                    "INSERT INTO indicadores " +
                                "VALUES ('"+ r.getIdRestaurante()+ "', '"+ i.getFaturacao()+"', '"+tempoMedio+"', '"+i.getNumPedidos()+"') " +
                                "ON DUPLICATE KEY UPDATE faturacaoTotal=VALUES(faturacaoTotal), tempoMedioAtendimento=VALUES(tempoMedioAtendimento), numPedidos=VALUES(numPedidos)");

            // Atualizar Restaurante
            stm.executeUpdate(
                    "INSERT INTO restaurantes VALUES ('"+r.getIdRestaurante()+"', '"+r.getLocal()+"') " +
                                "ON DUPLICATE KEY UPDATE morada=VALUES(morada)");
            
        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return res;
    }

    @Override
    public Restaurante remove(Object key) {
        Restaurante t = this.get(key);
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             PreparedStatement restaurantes_pstm = conn.prepareStatement("DELETE FROM restaurantes WHERE id=?");
             PreparedStatement indicadores_pstm = conn.prepareStatement("DELETE FROM indicadores WHERE Restaurante=?");
            PreparedStatement mensagens_pstm = conn.prepareStatement("UPDATE mensagens SET idRestaurante=NULL WHERE idRestaurante=?");) {
            restaurantes_pstm.setString(1, key.toString());
            restaurantes_pstm.executeUpdate();
        } catch (Exception e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return t;
    }

    @Override
    public void putAll(Map<? extends String, ? extends Restaurante> restaurantes) {
        for(Restaurante r : restaurantes.values()) {
            this.put(r.getIdRestaurante(), r);
        }
    }

    @Override
    public void clear() {
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             Statement stm = conn.createStatement()) {
            stm.executeUpdate("UPDATE mensagens SET idRestaurante=NULL");
            stm.executeUpdate("UPDATE userRestaurante SET idRestaurante=NULL");
            stm.executeUpdate("TRUNCATE indicadores");
            stm.executeUpdate("TRUNCATE restaurantes");
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
             ResultSet rs = stm.executeQuery("SELECT id FROM restaurantes")) { 
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
    public Collection<Restaurante> values() {
        Collection<Restaurante> res = new HashSet<>();
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT id FROM restaurantes")) { // ResultSet com os ids de todas as restaurantes
            while (rs.next()) {
                String idr = rs.getString(1); 
                Restaurante t = this.get(idr); 
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
    public Set<Entry<String, Restaurante>> entrySet() {
        Set<Entry<String, Restaurante>> res = new TreeSet<>();
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT id FROM restaurantes")) { // ResultSet com os ids de todas as restaurantes
            while (rs.next()) {
                String idr = rs.getString(1); 
                Restaurante r = this.get(idr); 
                Entry<String, Restaurante> entry = new SimpleEntry<>(idr, r);
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
