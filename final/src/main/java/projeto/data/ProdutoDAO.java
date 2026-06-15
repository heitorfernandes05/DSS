package projeto.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import projeto.business.SSPedido.Item;
import projeto.business.SSPedido.Menu;
import projeto.business.SSPedido.Produto;
import projeto.business.SSStock.Ingrediente;

public class ProdutoDAO implements Map<String, Produto> {

    private static ProdutoDAO singleton = null;

    private ProdutoDAO() {
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             Statement stm = conn.createStatement()) {
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
    }

    public static ProdutoDAO getInstance() {
        if (ProdutoDAO.singleton == null) {
            ProdutoDAO.singleton = new ProdutoDAO();
        }
        return ProdutoDAO.singleton;
    }

    @Override
    public Produto get(Object key) {
        Produto p = null;
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             PreparedStatement pstm = conn.prepareStatement("SELECT * FROM produtos WHERE id=?")) {
            pstm.setString(1, key.toString());
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    String idProduto = rs.getString("id");
                    String nome = rs.getString("nome");
                    float precoBase = rs.getFloat("precoBase");
                    int tempoPreparacao = rs.getInt("tempoPreparacao");
                    p = this.getItem(idProduto, conn, nome, precoBase, tempoPreparacao);
                    if (p == null) {
                        p = this.getMenu(idProduto, conn, nome, tempoPreparacao);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return p;
    }

    public Item getItem(Object key, Connection conn, String nome, float precoBase, int tempoPreparacao) {
        Item p = null;
        try (PreparedStatement pstm = conn.prepareStatement("SELECT * FROM itens WHERE idProduto=?")) {
            pstm.setString(1, key.toString());
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    List<Ingrediente> ingredientes = getIngredientesItem(key, conn);
                    p = new Item((String)key, nome, precoBase, tempoPreparacao, ingredientes);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return p;
    }

    public List<Ingrediente> getIngredientesItem(Object key, Connection conn) {
        List<Ingrediente> ingredientes = new ArrayList<>();
        try (PreparedStatement pstm = conn.prepareStatement("SELECT * FROM itemIngrediente WHERE idItem=?")) {
            pstm.setString(1, key.toString());
            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    Ingrediente ingrediente = getIngrediente(rs.getString("idIngrediente"));
                    if (ingrediente != null)
                        ingredientes.add(ingrediente);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return ingredientes;
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

    public Menu getMenu(Object key, Connection conn, String nome,  int tempoPreparacao) {
        Menu p = null;
        try (PreparedStatement pstm = conn.prepareStatement("SELECT * FROM menus WHERE idProduto=?")) {
            pstm.setString(1, key.toString());
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    float desconto = rs.getFloat("desconto");
                    List<Item> itensMenu = getItensMenu(key, conn);
                    p = new Menu((String) key, nome, desconto, itensMenu);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return p;
    }

    public List<Item> getItensMenu(Object key, Connection conn) {
        List<Item> itens = new ArrayList<>();
        try (PreparedStatement pstm = conn.prepareStatement("SELECT * FROM menuItem WHERE idMenu=?")) {
            pstm.setString(1, key.toString());
            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    String idItem = rs.getString("idItem");
                    Produto p = this.get(idItem);
                    if (p != null && p instanceof Item) {
                        itens.add((Item) p);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return itens;
    }

    private void limparRelacoesProduto(Connection conn, String idProduto) throws SQLException {
        try (PreparedStatement pstmMenuItem = conn.prepareStatement("DELETE FROM menuItem WHERE idMenu=?");
             PreparedStatement pstmMenu = conn.prepareStatement("DELETE FROM menus WHERE idProduto=?");
             PreparedStatement pstmItemIng = conn.prepareStatement("DELETE FROM itemIngrediente WHERE idItem=?");
             PreparedStatement pstmItem = conn.prepareStatement("DELETE FROM itens WHERE idProduto=?")) {
            pstmMenuItem.setString(1, idProduto);
            pstmMenuItem.executeUpdate();
            pstmMenu.setString(1, idProduto);
            pstmMenu.executeUpdate();
            pstmItemIng.setString(1, idProduto);
            pstmItemIng.executeUpdate();
            pstmItem.setString(1, idProduto);
            pstmItem.executeUpdate();
        }
    }

    private void guardarProdutoBase(Connection conn, Produto produto) throws SQLException {
        try (PreparedStatement pstm = conn.prepareStatement(
                "INSERT INTO produtos (id, nome, precoBase, tempoPreparacao) " +
                "VALUES (?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE nome=VALUES(nome), " +
                "precoBase=VALUES(precoBase), tempoPreparacao=VALUES(tempoPreparacao)")) {
            
            pstm.setString(1, produto.getId());
            pstm.setString(2, produto.getNome());
            pstm.setFloat(3, produto.getPrecoBase()); 
            pstm.setInt(4, produto.getTempoPreparacao());
            
            pstm.executeUpdate();
        }
    }

    private void guardarItem(Connection conn, Item item) throws SQLException {
        limparRelacoesProduto(conn, item.getId());
        guardarProdutoBase(conn, item);
        try (PreparedStatement pstmItem = conn.prepareStatement(
                "INSERT INTO itens (idProduto) VALUES (?) ON DUPLICATE KEY UPDATE idProduto=VALUES(idProduto)")) {
            pstmItem.setString(1, item.getId());
            pstmItem.executeUpdate();
        }
        try (PreparedStatement pstmItemIng = conn.prepareStatement(
                "INSERT INTO itemIngrediente (idItem, idIngrediente) VALUES (?, ?)")) {
            for (Ingrediente ingrediente : item.getIngredientes()) {
                pstmItemIng.setString(1, item.getId());
                pstmItemIng.setString(2, ingrediente.getIdIngrediente());
                pstmItemIng.executeUpdate();
            }
        }
    }

    private void guardarMenu(Connection conn, Menu menu) throws SQLException {
        List<Item> itens = menu.cloneItensMenu();
        guardarProdutoBase(conn, menu);
        try (PreparedStatement pstmMenu = conn.prepareStatement(
                "INSERT INTO menus (idProduto, desconto) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE desconto=VALUES(desconto)")) {
            pstmMenu.setString(1, menu.getId());
            pstmMenu.setFloat(2, menu.getDesconto());
            pstmMenu.executeUpdate();
        }
        try (PreparedStatement pstmMenuItem = conn.prepareStatement(
                "INSERT INTO menuItem (idMenu, idItem) VALUES (?, ?)")) {
            for (Item item : itens) {
                guardarItem(conn, item);
                pstmMenuItem.setString(1, menu.getId());
                pstmMenuItem.setString(2, item.getId());
                pstmMenuItem.executeUpdate();
            }
        }
    }

    @Override
    public int size() {
        int i = 0;
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT count(*) FROM produtos")) {
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
        boolean r;
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             PreparedStatement pstm = conn.prepareStatement("SELECT id FROM produtos WHERE id=?")) {
            pstm.setString(1, key.toString());
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
        Produto p = (Produto) value;
        return this.containsKey(p.getId());
    }

    @Override
    public Produto put(String key, Produto produto) {
        if (produto == null) {
            return null;
        }
        Produto res = this.get(key != null ? key : produto.getId());
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD)) {
            limparRelacoesProduto(conn, produto.getId());
            if (produto instanceof Menu menu) {
                guardarMenu(conn, menu);
            } else if (produto instanceof Item item) {
                guardarItem(conn, item);
            } else {
                guardarProdutoBase(conn, produto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return res;
    }

    @Override
    public Produto remove(Object key) {
        Produto p = this.get(key);
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             PreparedStatement pstm = conn.prepareStatement("DELETE FROM produtos WHERE id=?")) {
            limparRelacoesProduto(conn, key.toString());
            pstm.setString(1, key.toString());
            pstm.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return p;
    }

    @Override
    public void putAll(Map<? extends String, ? extends Produto> produtos) {
        for (Produto p : produtos.values()) {
            this.put(p.getId(), p);
        }
    }

    @Override
    public void clear() {
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             Statement stm = conn.createStatement()) {
            stm.executeUpdate("TRUNCATE menuItem");
            stm.executeUpdate("TRUNCATE menus");
            stm.executeUpdate("TRUNCATE itemIngrediente");
            stm.executeUpdate("TRUNCATE itens");
            stm.executeUpdate("TRUNCATE produtos");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
    }

    @Override
    public Set<String> keySet() {
        HashSet<String> ret = new HashSet<String>();
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT id FROM produtos")) {
            while (rs.next()) {
                ret.add(rs.getString(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return ret;
    }

    @Override
    public Collection<Produto> values() {
        Collection<Produto> res = new HashSet<>();
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT id FROM produtos")) {
            while (rs.next()) {
                String id = rs.getString(1);
                Produto p = this.get(id);
                res.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return res;
    }

    @Override
    public Set<Entry<String, Produto>> entrySet() {
        Set<Entry<String, Produto>> res = new TreeSet<>();
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT id FROM produtos")) {
            while (rs.next()) {
                String id = rs.getString(1);
                Produto p = this.get(id);
                Entry<String, Produto> entry = new SimpleEntry<>(id, p);
                res.add(entry);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return res;
    }
}
