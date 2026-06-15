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

import projeto.business.SSStock.Stock;

public class StockDAO implements Map<String, Stock> {

    private static StockDAO singleton = null;

    private StockDAO() {
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
            Statement stm = conn.createStatement()) {
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
    }

    public static StockDAO getInstance() {
        if (StockDAO.singleton == null) {
            StockDAO.singleton = new StockDAO();
        }
        return StockDAO.singleton;
    }

    @Override
    public int size() {
        int i = 0;
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT count(*) FROM stock")) {
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
             PreparedStatement pstm = conn.prepareStatement("SELECT id FROM stock WHERE id=?")) {
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
        Stock t = (Stock) value;
        return this.containsKey(t.getIdStock());
    }

    @Override
    public Stock get(Object key) {
        Stock r = null;
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             PreparedStatement pstm = conn.prepareStatement("SELECT * FROM stock WHERE id=?")) {
            pstm.setString(1, key.toString());
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {  // A chave existe na tabela
                    String id = rs.getString("id");
                    String restId = rs.getString("idRestaurante");
                    r = new Stock(id, restId);
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
    public Stock put(String key, Stock p) {
        Stock posto = null;
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             Statement stm = conn.createStatement()) {
            posto = this.get(key); // obter o valor anterior, caso exista
            stm.executeUpdate(
                    "INSERT INTO stock " +
                                "VALUES ('"+ p.getIdStock()+ "', '"+p.getIdRestaurante()+"') " +
                                "ON DUPLICATE KEY UPDATE idRestaurante=VALUES(idRestaurante)");
            
        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return posto;
    }

    @Override
    public Stock remove(Object key) {
        Stock t = this.get(key);
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             PreparedStatement stock_pstm = conn.prepareStatement("DELETE FROM stock WHERE id=?");){

            stock_pstm.setString(1, key.toString());
            stock_pstm.executeUpdate();
        } catch (Exception e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return t;
    }

    @Override
    public void putAll(Map<? extends String, ? extends Stock> stock) {
        for(Stock r : stock.values()) {
            this.put(r.getIdStock(), r);
        }
    }

    @Override
    public void clear() {
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             Statement stm = conn.createStatement()) {
            stm.executeUpdate("UPDATE solicitacoes SET idStock=NULL");
            stm.executeUpdate("UPDATE stockIngrediente SET idStock=NULL");
            stm.executeUpdate("TRUNCATE stock");
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
             ResultSet rs = stm.executeQuery("SELECT id FROM stock")) { 
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
    public Collection<Stock> values() {
        Collection<Stock> res = new HashSet<>();
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT id FROM stock")) { // ResultSet com os ids de todas as stock
            while (rs.next()) {
                String idr = rs.getString(1); 
                Stock t = this.get(idr); 
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
    public Set<Entry<String, Stock>> entrySet() {
        Set<Entry<String, Stock>> res = new TreeSet<>();
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT id FROM stock")) { // ResultSet com os ids de todas as stock
            while (rs.next()) {
                String idr = rs.getString(1); 
                Stock r = this.get(idr); 
                Entry<String, Stock> entry = new SimpleEntry<>(idr, r);
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
