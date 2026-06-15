package projeto.data;

public class Config {
    static final String USERNAME = "user";                        // Atualizar
    static final String PASSWORD = "mypass";                    // Atualizar
    private static final String DATABASE = "dssrestaurantes";          // Atualizar
    private static final String DRIVER = "jdbc:mariadb";        // Usar para MariaDB
    static final String URL = DRIVER+"://localhost:3306/"+DATABASE;
}
