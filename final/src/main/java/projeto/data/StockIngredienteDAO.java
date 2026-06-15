package projeto.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import projeto.business.SSStock.Ingrediente;
import projeto.business.SSStock.Stock;
import projeto.business.SSStock.StockIngrediente;

public class StockIngredienteDAO {

    private static StockIngredienteDAO singleton = null;

    private StockIngredienteDAO() {
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
            Statement stm = conn.createStatement()) {
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
    }

    public static StockIngredienteDAO getInstance() {
        if (StockIngredienteDAO.singleton == null) {
            StockIngredienteDAO.singleton = new StockIngredienteDAO();
        }
        return StockIngredienteDAO.singleton;
    }

    public int size() {
        int i = 0;
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT count(*) FROM stockIngrediente")) {
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

    public boolean containsKey(Object idStock, Object idIngrediente) {
        if (idStock == null || idIngrediente == null) {
            return false;
        }
        boolean existe = false;
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
            PreparedStatement pstm = conn.prepareStatement(
                "SELECT 1 FROM stockIngrediente WHERE idStock = ? AND idIngrediente = ?")) {
            
            pstm.setString(1, idStock.toString());
            pstm.setString(2, idIngrediente.toString());
            
            try (ResultSet rs = pstm.executeQuery()) {
                existe = rs.next();  //(idStock, idIngrediente)
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return existe;
    }

    public StockIngrediente get(Object idStock, Object idIngrediente) {
        StockIngrediente r = null;
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             PreparedStatement pstm = conn.prepareStatement("SELECT * FROM stockIngrediente WHERE idStock = ? AND idIngrediente = ?")) {
            pstm.setString(1, idStock.toString());
            pstm.setString(2, idIngrediente.toString());
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {  // A chave existe na tabela
                    String idIng = rs.getString("idIngrediente");
                    int quantidadeAtual = rs.getInt("quantidadeAtual");
                    int quantidadeMin = rs.getInt("quantidadeMin");
                    int quantidadeRec = rs.getInt("quantidadeRec");
                    Ingrediente ingrediente = getIngrediente(idIng);
                    r = new StockIngrediente(ingrediente, quantidadeMin, quantidadeRec, quantidadeAtual);
                }
            }
        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return r;
    }

    public Collection<StockIngrediente> getByStock(String idStock) {
        Collection<StockIngrediente> res = new HashSet<>();
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
            PreparedStatement pstm = conn.prepareStatement("SELECT * FROM stockIngrediente WHERE idStock = ?")) {
            
            pstm.setString(1, idStock);
            
            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    String idIngrediente = rs.getString("idIngrediente");
                    int quantidadeAtual = rs.getInt("quantidadeAtual");
                    int quantidadeMin = rs.getInt("quantidadeMin");
                    int quantidadeRec = rs.getInt("quantidadeRec");
                    Ingrediente ingrediente = getIngrediente(idIngrediente);
                    StockIngrediente si = new StockIngrediente(
                        ingrediente, quantidadeMin, quantidadeRec, quantidadeAtual
                    );
                    res.add(si);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao obter StockIngredientes por stock: " + e.getMessage(), e);
        }
        return res;
    }

    public Ingrediente getIngrediente(String idIngrediente) {
        Ingrediente ingrediente = null;
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             PreparedStatement pstm = conn.prepareStatement("SELECT * FROM ingredientes WHERE id=?")) {
            pstm.setString(1, idIngrediente);
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {  // A chave existe na tabela
                    String id = rs.getString("id");
                    String nome = rs.getString("nome");
                    boolean alergenio = rs.getBoolean("alergenio");
                    ingrediente = new Ingrediente(id, nome, alergenio);
                }
            }
        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return ingrediente;
    }


    public StockIngrediente put(String idStock, StockIngrediente p) {
        StockIngrediente posto = null;
        String idIngrediente = p.getIngrediente().getIdIngrediente();
        
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
            Statement stm = conn.createStatement()) {
            
            posto = this.get(idStock, idIngrediente); 
            int isalergenio = p.getIngrediente().isAlergenio() ? 1 : 0;
            
            stm.executeUpdate(
                "INSERT INTO ingredientes VALUES ('" + idIngrediente + "', '" + 
                p.getIngrediente().getNome() + "', " + isalergenio + ") " +
                "ON DUPLICATE KEY UPDATE nome=VALUES(nome), alergenio=VALUES(alergenio)");
            
            stm.executeUpdate(
                "INSERT INTO stockIngrediente VALUES ('" + idStock + "', '" + 
                idIngrediente + "', " + 
                p.getQuantidadeMin() + ", " + 
                p.getQuantidade() + ", " + 
                p.getQuantidadeRec() + ") " +
                "ON DUPLICATE KEY UPDATE " +
                "quantidadeMin=VALUES(quantidadeMin), " +
                "quantidadeAtual=VALUES(quantidadeAtual), " +
                "quantidadeRec=VALUES(quantidadeRec)");
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao guardar StockIngrediente: " + e.getMessage(), e);
        }
        return posto;
    }

    public StockIngrediente remove(Object idStock, Object idIngrediente) {
        StockIngrediente t = this.get(idStock, idIngrediente);
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             PreparedStatement stockIngrediente_pstm = conn.prepareStatement("DELETE FROM stockIngrediente WHERE idStock = ? AND idIngrediente = ?");){

            stockIngrediente_pstm.setString(1, idStock.toString());
            stockIngrediente_pstm.setString(2, idIngrediente.toString());
            stockIngrediente_pstm.executeUpdate();
        } catch (Exception e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return t;
    }

    public void putAll(Map<? extends String, ? extends StockIngrediente> stockIngrediente) {
        for( Map.Entry<? extends String, ? extends StockIngrediente> entry : stockIngrediente.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    public void clear() {
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             Statement stm = conn.createStatement()) {
            stm.executeUpdate("TRUNCATE stockIngrediente");
        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
    }

    public Collection<StockIngrediente> values() {
        Collection<StockIngrediente> res = new HashSet<>();
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT idStock, idIngrediente FROM stockIngrediente")) {
            while (rs.next()) {
                String idr = rs.getString("idIngrediente");
                String ids = rs.getString("idStock");
                StockIngrediente si = this.get(ids, idr);
                res.add(si);                         
            }
        } catch (Exception e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return res;
    }
}
