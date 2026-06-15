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

import projeto.business.SSAdministracao.User;

public class UserDAO implements Map<String, User> {


    private static UserDAO singleton = null;

    private UserDAO() {
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
            Statement stm = conn.createStatement()) {
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
    }

    public static UserDAO getInstance() {
        if (UserDAO.singleton == null) {
            UserDAO.singleton = new UserDAO();
        }
        return UserDAO.singleton;
    }

    @Override
    public int size() {
        int i = 0;
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT count(*) FROM users")) {
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
             PreparedStatement pstm = conn.prepareStatement("SELECT email FROM users WHERE email=?")) {
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
        User t = (User) value;
        return this.containsKey(t.getEmail());
    }

    @Override
    public User get(Object key) {
        User r = null;
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             PreparedStatement pstm = conn.prepareStatement("SELECT * FROM users WHERE email=?")) {
            pstm.setString(1, key.toString());
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {  // A chave existe na tabela
                    String email = rs.getString("email");
                    String password = rs.getString("password");
                    String nome = rs.getString("nome");
                    String cargo = rs.getString("cargo");
                    List<String> restaurantesAcessiveis = this.getRestaurantesAcessiveisUser(email, conn);
                    r = new User(email, password, nome,cargo, restaurantesAcessiveis);
                }
            }
        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return r;
    }

    private List<String> getRestaurantesAcessiveisUser(String email, Connection conn) throws SQLException {
        List<String> restaurantesAcessiveis = new ArrayList<>();
        try (PreparedStatement pstm = conn.prepareStatement(
                "SELECT idRestaurante FROM userRestaurante WHERE email=?")) {
            pstm.setString(1, email);
            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    String idRestaurante = rs.getString("idRestaurante");
                    restaurantesAcessiveis.add(idRestaurante);
                }
                return restaurantesAcessiveis;
            }
        }
    }


    @Override
    public User put(String key, User u) {
        User res = null;
        List<String> restaurantesAcessiveis = u.getRestaurantesAcessiveis();
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             Statement stm = conn.createStatement()) {
            res = this.get(key); // obter o valor anterior, caso exista
            // Actualizar Indicadores
                String insertUserRestSql = "INSERT IGNORE INTO userRestaurante (email, idRestaurante) VALUES (?, ?)";
                try (PreparedStatement psUR = conn.prepareStatement(insertUserRestSql)) {
                    for (String restauranteId : restaurantesAcessiveis) {
                        psUR.setString(1, u.getEmail());
                        psUR.setString(2, restauranteId);
                        psUR.addBatch();
                    }
                    psUR.executeBatch();
                }
                String insertUserSql = "INSERT INTO users (email, password, nome, cargo) VALUES (?, ?) ON DUPLICATE KEY UPDATE password = VALUES(password), nome=VALUES(nome), cargo=VALUES(cargo)";
                try (PreparedStatement psUser = conn.prepareStatement(insertUserSql)) {
                    psUser.setString(1, u.getEmail());
                    psUser.setString(2, u.getPassword());
                    psUser.setString(3, u.getNome());
                    psUser.setString(4, u.getCargo());
                    psUser.executeUpdate();
                }
            
        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return res;
    }

    @Override
    public User remove(Object key) {
        User t = this.get(key);
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             PreparedStatement Users_pstm = conn.prepareStatement("DELETE FROM Users WHERE email=?");
             PreparedStatement alunos_pstm = conn.prepareStatement("UPDATE usersRestaurante SET User=? WHERE email=?")) {

            Users_pstm.setString(1, key.toString());
            Users_pstm.executeUpdate();
        } catch (Exception e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return t;
    }

    @Override
    public void putAll(Map<? extends String, ? extends User> Users) {
        for(User u : Users.values()) {
            this.put(u.getEmail(), u);
        }
    }

    @Override
    public void clear() {
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             Statement stm = conn.createStatement()) {
            stm.executeUpdate("TRUNCATE userRestaurante");
            stm.executeUpdate("TRUNCATE users");
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
             ResultSet rs = stm.executeQuery("SELECT email FROM users")) { 
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
    public Collection<User> values() {
        Collection<User> res = new HashSet<>();
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT email FROM users")) { // ResultSet com os ids de todas as Users
            while (rs.next()) {
                String idr = rs.getString(1); 
                User t = this.get(idr); 
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
    public Set<Entry<String, User>> entrySet() {
        Set<Entry<String, User>> res = new TreeSet<>();
        try (Connection conn = DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT email FROM users")) { // ResultSet com os ids de todas as Users
            while (rs.next()) {
                String email = rs.getString(1); 
                User r = this.get(email); 
                Entry<String, User> entry = new SimpleEntry<>(email, r);
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
