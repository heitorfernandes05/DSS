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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import projeto.business.SSStock.Ingrediente;
import projeto.business.SSStock.SolicitacaoReposicao;

public class SolicitacaoReposicaoDAO implements Map<String, SolicitacaoReposicao> {

    private static SolicitacaoReposicaoDAO singleton = null;

    private SolicitacaoReposicaoDAO() {
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
            Statement stm = conn.createStatement()) {
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
    }

    public static SolicitacaoReposicaoDAO getInstance() {
        if (SolicitacaoReposicaoDAO.singleton == null) {
            SolicitacaoReposicaoDAO.singleton = new SolicitacaoReposicaoDAO();
        }
        return SolicitacaoReposicaoDAO.singleton;
    }

    @Override
    public int size() {
        int i = 0;
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT count(*) FROM solicitacoes")) {
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
             PreparedStatement pstm = conn.prepareStatement("SELECT id FROM solicitacoes WHERE id=?")) {
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
        SolicitacaoReposicao t = (SolicitacaoReposicao) value;
        return this.containsKey(t.getIdSolicitacao());
    }

    @Override
    public SolicitacaoReposicao get(Object key) {
        SolicitacaoReposicao r = null;
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             PreparedStatement pstm = conn.prepareStatement("SELECT * FROM solicitacoes WHERE id=?")) {
            pstm.setString(1, key.toString());
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {  // A chave existe na tabela
                    String id = rs.getString("id");
                    int quantidade = rs.getInt("quantidade");
                    LocalDateTime dataSolicitacao = rs.getTimestamp("data_solicitacao").toLocalDateTime();
                    Timestamp ts = rs.getTimestamp("data_reposicao");
                    Timestamp tsPrev = rs.getTimestamp("data_reposicao_prevista");
                    LocalDateTime dataReposicaoPrevista = null;
                    if (tsPrev != null) {
                        dataReposicaoPrevista = tsPrev.toLocalDateTime();
                    }
                    LocalDateTime dataReposicao = null;
                    if (ts != null) {
                        dataReposicao = ts.toLocalDateTime();
                    }
                    String idStock = rs.getString("idStock");
                    String idPosto = rs.getString("idPosto");
                    Ingrediente ing = getIngrediente(rs.getString("idIngrediente"));
                    r = new SolicitacaoReposicao(id, quantidade, dataSolicitacao, dataReposicaoPrevista, dataReposicao, ing, idStock, idPosto);
                }
            }
        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return r;
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

    @Override
    public SolicitacaoReposicao put(String key, SolicitacaoReposicao s) {
        SolicitacaoReposicao res = null;
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
            Statement stm = conn.createStatement()) {
            
            res = this.get(key);
            Timestamp dataSol = Timestamp.valueOf(s.getDataSolicitacao());
            
            boolean temDataPrevista = s.getDataReposicaoPrevista() != null;
            boolean temDataReposicao = s.getDataReposicao() != null;
            
            String sql;
            
            if (temDataPrevista && temDataReposicao) {
                Timestamp dataPrev = Timestamp.valueOf(s.getDataReposicaoPrevista());
                Timestamp dataRep = Timestamp.valueOf(s.getDataReposicao());
                
                sql = "INSERT INTO solicitacoes VALUES ('" + s.getIdSolicitacao() + "', " + 
                    s.getQuantidade() + ", '" + dataSol + "', '" + dataPrev + "', '" + dataRep + 
                    "', '" + s.getIngrediente().getIdIngrediente() + "', '" + s.getIdStock() + 
                    "', '" + s.getIdPosto() + "') " +
                    "ON DUPLICATE KEY UPDATE quantidade=VALUES(quantidade), " +
                    "data_solicitacao=VALUES(data_solicitacao), " +
                    "data_reposicao_prevista=VALUES(data_reposicao_prevista), " +
                    "data_reposicao=VALUES(data_reposicao), " +
                    "idIngrediente=VALUES(idIngrediente), idStock=VALUES(idStock), " +
                    "idPosto=VALUES(idPosto)";
                    
            } else if (temDataPrevista) {
                Timestamp dataPrev = Timestamp.valueOf(s.getDataReposicaoPrevista());
                sql = "INSERT INTO solicitacoes VALUES ('" + s.getIdSolicitacao() + "', " + 
                    s.getQuantidade() + ", '" + dataSol + "', '" + dataPrev + "', NULL, '" + 
                    s.getIngrediente().getIdIngrediente() + "', '" + s.getIdStock() + "', '" + 
                    s.getIdPosto() + "') " +
                    "ON DUPLICATE KEY UPDATE quantidade=VALUES(quantidade), " +
                    "data_solicitacao=VALUES(data_solicitacao), " +
                    "data_reposicao_prevista=VALUES(data_reposicao_prevista), " +
                    "data_reposicao=VALUES(data_reposicao), " +
                    "idIngrediente=VALUES(idIngrediente), idStock=VALUES(idStock), " +
                    "idPosto=VALUES(idPosto)";
                    
            } else {
                sql = "INSERT INTO solicitacoes VALUES ('" + s.getIdSolicitacao() + "', " + 
                    s.getQuantidade() + ", '" + dataSol + "', NULL, NULL, '" + 
                    s.getIngrediente().getIdIngrediente() + "', '" + s.getIdStock() + "', '" + 
                    s.getIdPosto() + "') " +
                    "ON DUPLICATE KEY UPDATE quantidade=VALUES(quantidade), " +
                    "data_solicitacao=VALUES(data_solicitacao), " +
                    "data_reposicao_prevista=VALUES(data_reposicao_prevista), " +
                    "data_reposicao=VALUES(data_reposicao), " +
                    "idIngrediente=VALUES(idIngrediente), idStock=VALUES(idStock), " +
                    "idPosto=VALUES(idPosto)";
            }
            stm.executeUpdate(sql);
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao guardar solicitação: " + e.getMessage(), e);
        }
        return res;
    }

    @Override
    public SolicitacaoReposicao remove(Object key) {
        SolicitacaoReposicao t = this.get(key);
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             PreparedStatement solicitacoes_pstm = conn.prepareStatement("DELETE FROM solicitacoes WHERE id=?");){
            solicitacoes_pstm.setString(1, key.toString());
            solicitacoes_pstm.executeUpdate();
        } catch (Exception e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return t;
    }

    @Override
    public void putAll(Map<? extends String, ? extends SolicitacaoReposicao> solicitacoes) {
        for(SolicitacaoReposicao r : solicitacoes.values()) {
            this.put(r.getIdSolicitacao(), r);
        }
    }

    @Override
    public void clear() {
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             Statement stm = conn.createStatement()) {
            stm.executeUpdate("TRUNCATE solicitacoes");
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
             ResultSet rs = stm.executeQuery("SELECT id FROM solicitacoes")) { 
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
    public Collection<SolicitacaoReposicao> values() {
        Collection<SolicitacaoReposicao> res = new HashSet<>();
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT id FROM solicitacoes")) { // ResultSet com os ids de todas as solicitacoes
            while (rs.next()) {
                String idr = rs.getString(1); 
                SolicitacaoReposicao t = this.get(idr); 
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
    public Set<Entry<String, SolicitacaoReposicao>> entrySet() {
        Set<Entry<String, SolicitacaoReposicao>> res = new TreeSet<>();
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT id FROM solicitacoes")) { // ResultSet com os ids de todas as solicitacoes
            while (rs.next()) {
                String idr = rs.getString(1); 
                SolicitacaoReposicao r = this.get(idr); 
                Entry<String, SolicitacaoReposicao> entry = new SimpleEntry<>(idr, r);
                res.add(entry);                         
            }
        } catch (Exception e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return res;
    }

    public Collection<SolicitacaoReposicao> getSolicitacoesPendentes(String idStock) {
        Collection<SolicitacaoReposicao> res = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT id FROM solicitacoes WHERE idStock='" + idStock + "'")) {
            while (rs.next()) {
                String idr = rs.getString(1); 
                SolicitacaoReposicao t = this.get(idr); 
                if (t.getDataReposicao() == null)
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
