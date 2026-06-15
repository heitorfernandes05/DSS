package projeto.business.SSAdministracao;

import java.util.List;

public class User {
    private String email;
    private String password;
    private String nome;
    private String cargo;
    private List<String> restaurantesAcessiveis;

    public User(String email, String password, String nome, String cargo, List<String> restaurantesAcessiveis) {
        this.email = email;
        this.password = password;
        this.nome = nome;
        this.cargo = cargo;
        this.restaurantesAcessiveis = restaurantesAcessiveis;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
    }

    public String getNome() {
        return this.nome;
    }

    public String getCargo() {
        return this.cargo;
    }

    public List<String> getRestaurantesAcessiveis() {
        return this.restaurantesAcessiveis;
    }

    public boolean validarPassword(String password) {
        return this.password.equals(password);
    }
    
}
