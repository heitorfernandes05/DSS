package projeto.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import projeto.business.SSPedido.Fatura;
import projeto.business.SSPedido.Pagamento;
import projeto.business.SSPedido.Pedido;
import projeto.business.SSPedido.Produto;

public class PedidoDAO implements Map<Integer, Pedido> {

    private static PedidoDAO singleton = null;

    private PedidoDAO() {
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             Statement stm = conn.createStatement()) {
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
    }

    public static PedidoDAO getInstance() {
        if (PedidoDAO.singleton == null) {
            PedidoDAO.singleton = new PedidoDAO();
        }
        return PedidoDAO.singleton;
    }

    private Integer parseNumero(Object key) {
        if (key == null) {
            return null;
        }
        try {
            return Integer.parseInt(key.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public int size() {
        int i = 0;
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT count(*) FROM pedidos")) {
            if (rs.next()) {
                i = rs.getInt(1);
            }
        } catch (Exception e) {
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
        Integer numero = parseNumero(key);
        if (numero == null) {
            return false;
        }
        boolean r;
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             PreparedStatement pstm = conn.prepareStatement("SELECT numero FROM pedidos WHERE numero=?")) {
            pstm.setInt(1, numero);
            try (ResultSet rs = pstm.executeQuery()) {
                r = rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return r;
    }

    @Override
    public boolean containsValue(Object value) {
        Pedido p = (Pedido) value;
        return this.containsKey(p.getNumero());
    }

    @Override
    public Pedido get(Object key) {
        Integer numero = parseNumero(key);
        if (numero == null) {
            return null;
        }
        Pedido p = null;
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             PreparedStatement pstm = conn.prepareStatement("SELECT * FROM pedidos WHERE numero=?")) {
            pstm.setInt(1, numero);
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    int numeroPedido = rs.getInt("numero");
                    int precoTotal = rs.getInt("precoTotal");
                    int tempoPreparacao = rs.getInt("tempoPreparacao");
                    Timestamp ts = rs.getTimestamp("timestamp");
                    LocalDateTime timestamp = ts == null ? null : ts.toLocalDateTime();
                    String localEntrega = rs.getString("localEntrega");
                    String estado = rs.getString("estado");
                    String nota = rs.getString("nota");
                    int balcao = rs.getInt("balcao");
                    String idRestaurante = rs.getString("idRestaurante");
                    p = new Pedido(numeroPedido, idRestaurante);
                    p.setPrecoTotal(precoTotal);
                    p.setTempoPreparacao(tempoPreparacao);
                    p.setTimestamp(timestamp);
                    p.setBalcao(balcao);
                    if (localEntrega != null) {
                        p.setLocalEntrega(localEntrega);
                    }
                    if (estado != null) {
                        p.setEstado(estado);
                    }
                    if (nota != null) {
                        p.addNota(nota);
                    }

                    Pagamento pagamento = this.getPagamento(numeroPedido, conn);
                    if (pagamento != null) {
                        p.setPagamento(pagamento);
                    }
                    
                    List<Produto> produtos = this.getProdutosPedido(numeroPedido, conn);
                    for (Produto produto : produtos) {
                        p.addProduto(produto);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return p;
    }

    public Pagamento getPagamento(Object key, Connection conn) {
        Integer numero = parseNumero(key);
        if (numero == null) {
            return null;
        }
        Pagamento pagamento = null;
        try (PreparedStatement pstm = conn.prepareStatement("SELECT * FROM pagamentos WHERE numeroPedido=?")) {
            pstm.setInt(1, numero);
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    float precoTotal = rs.getFloat("precoTotal");
                    float valor = rs.getFloat("valor");
                    String metodo = rs.getString("metodo");
                    float troco = rs.getFloat("troco");
                    Timestamp ts = rs.getTimestamp("timestamp");
                    LocalDateTime timestamp = ts == null ? null : ts.toLocalDateTime();
                    String faturaDetalhes = rs.getString("fatura");
                    Fatura fatura = new Fatura(faturaDetalhes);
                    pagamento = new Pagamento(precoTotal, valor, metodo);
                    pagamento.setTroco(troco);
                    pagamento.setTimestamp(timestamp);
                    pagamento.setFatura(fatura);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return pagamento;
    }

    private List<Produto> getProdutosPedido(int numeroPedido, Connection conn) {
        List<Produto> produtos = new ArrayList<>();
        
        String sql = "SELECT pp.idProduto, pp.quantidade " + "FROM pedidoproduto pp " + "WHERE pp.numeroPedido = ?";
        
        try (PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setInt(1, numeroPedido);
            
            try (ResultSet rs = pstm.executeQuery()) {
                ProdutoDAO produtoDAO = ProdutoDAO.getInstance();
                while (rs.next()) {
                    String idProduto = rs.getString("idProduto");
                    int quantidade = rs.getInt("quantidade");
                    Produto produto = produtoDAO.get(idProduto);
                    if (produto != null) {
                        for (int i = 0; i < quantidade; i++) {
                            produtos.add(produto);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        
        return produtos;
    }

    @Override
    public Pedido put(Integer key, Pedido pedido) {
        if (pedido == null) {
            return null;
        }
        
        Pedido res = this.get(key != null ? key : Integer.toString(pedido.getNumero()));
        
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD)) {
            
            try (PreparedStatement pstm = conn.prepareStatement(
                    "INSERT INTO pedidos (numero, precoTotal, tempoPreparacao, timestamp, localEntrega, estado, nota, balcao, idRestaurante) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE precoTotal=VALUES(precoTotal), tempoPreparacao=VALUES(tempoPreparacao), " +
                    "timestamp=VALUES(timestamp), localEntrega=VALUES(localEntrega), estado=VALUES(estado), " +
                    "nota=VALUES(nota), balcao=VALUES(balcao), idRestaurante=VALUES(idRestaurante)")) {
                
                int numero = pedido.getNumero();
                pstm.setInt(1, numero);
                pstm.setFloat(2, pedido.getPrecoTotal());
                pstm.setInt(3, pedido.getTempoPreparacao());
                
                LocalDateTime timestamp = pedido.getTimestamp();
                if (timestamp == null) {
                    pstm.setTimestamp(4, null);
                } else {
                    pstm.setTimestamp(4, Timestamp.valueOf(timestamp));
                }
                
                pstm.setString(5, pedido.getLocalEntrega());
                pstm.setString(6, pedido.getEstado());
                pstm.setString(7, pedido.getNota());
                pstm.setInt(8, pedido.getBalcao());
                pstm.setString(9, pedido.getRestaurante());
                pstm.executeUpdate();
            }
            
            try (PreparedStatement pstm = conn.prepareStatement(
                    "DELETE FROM pedidoproduto WHERE numeroPedido = ?")) {
                pstm.setInt(1, pedido.getNumero());
                pstm.executeUpdate();
            }
            
            List<Produto> produtos = pedido.getProdutosSelecionados();
            if (produtos != null && !produtos.isEmpty()) {
                
                String sqlProduto = 
                    "INSERT INTO pedidoproduto (numeroPedido, idProduto, quantidade) " +
                    "VALUES (?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE quantidade=VALUES(quantidade)";
                
                Map<String, Integer> quantidades = new HashMap<>();
                for (Produto produto : produtos) {
                    String id = produto.getId();
                    quantidades.put(id, quantidades.getOrDefault(id, 0) + 1);
                }
                
                try (PreparedStatement pstmProduto = conn.prepareStatement(sqlProduto)) {
                    for (Map.Entry<String, Integer> entry : quantidades.entrySet()) {
                        pstmProduto.setInt(1, pedido.getNumero());
                        pstmProduto.setString(2, entry.getKey());
                        pstmProduto.setInt(3, entry.getValue());
                        pstmProduto.addBatch();
                    }
                    pstmProduto.executeBatch();
                }
            }
            
            Pagamento pagamento = pedido.getPagamento();
            if (pagamento != null) {
                try (PreparedStatement pstmPagamento = conn.prepareStatement(
                        "INSERT INTO pagamentos (numeroPedido, metodo, valor, troco, timestamp, fatura, precoTotal) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE metodo=VALUES(metodo), valor=VALUES(valor), troco=VALUES(troco), " +
                        "timestamp=VALUES(timestamp), fatura=VALUES(fatura), precoTotal=VALUES(precoTotal)")) {
                    
                    pstmPagamento.setInt(1, pedido.getNumero());
                    pstmPagamento.setString(2, pagamento.getMetodoPagamento());
                    pstmPagamento.setFloat(3, pagamento.getValorPago());
                    pstmPagamento.setFloat(4, pagamento.getTroco());
                    
                    LocalDateTime pagamentoTimestamp = pagamento.getTimestamp();
                    if (pagamentoTimestamp == null) {
                        pstmPagamento.setTimestamp(5, null);
                    } else {
                        pstmPagamento.setTimestamp(5, Timestamp.valueOf(pagamentoTimestamp));
                    }
                    
                    Fatura fatura = pagamento.getFatura();
                    if (fatura != null) {
                        pstmPagamento.setString(6, fatura.getDetalhes());
                    } else {
                        pstmPagamento.setNull(6, Types.VARCHAR);
                    }
                    
                    pstmPagamento.setFloat(7, pedido.getPrecoTotal());
                    pstmPagamento.executeUpdate();
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao guardar pedido: " + e.getMessage(), e);
        }
        
        return res;
    }

    @Override
    public Pedido remove(Object key) {
        Pedido p = this.get(key);
        Integer numero = parseNumero(key);
        if (numero == null) {
            return p;
        }
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             PreparedStatement pstm = conn.prepareStatement("DELETE FROM pedidos WHERE numero=?")) {
            pstm.setInt(1, numero);
            pstm.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return p;
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends Pedido> pedidos) {
        for (Pedido p : pedidos.values()) {
            this.put(p.getNumero(), p);
        }
    }

    @Override
    public void clear() {
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             Statement stm = conn.createStatement()) {
            stm.executeUpdate("TRUNCATE pedidos");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
    }

    @Override
    public Collection<Pedido> values() {
        Collection<Pedido> res = new HashSet<>();
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT numero FROM pedidos")) {
            while (rs.next()) {
                String id = Integer.toString(rs.getInt(1));
                Pedido p = this.get(id);
                res.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return res;
    }

    @Override
    public Set<Entry<Integer, Pedido>> entrySet() {
        Set<Entry<Integer, Pedido>> res = new TreeSet<>();
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT numero FROM pedidos")) {
            while (rs.next()) {
                int id = rs.getInt(1);
                Pedido p = this.get(id);
                Entry<Integer, Pedido> entry = new SimpleEntry<>(id, p);
                res.add(entry);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return res;
    }

    @Override
    public Set<Integer> keySet() {
        Set<Integer> res = new TreeSet<>();
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT numero FROM pedidos")) {
            while (rs.next()) {
                int id = rs.getInt(1);
                res.add(id);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return res;
    }
}
