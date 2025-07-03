package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnessione {
    private static DBConnessione instance;
    private Connection connection;
    private static final String NOME = "postgres";
    //Possiamo ignorare il problema di sonar qube riguardo alla password dato che non ci interessa la sicurezza in questo contesto
    private static final String PASSWORD = "password";
    private static final String URL = "jdbc:postgresql://localhost:5432/todomac";


    private DBConnessione() throws SQLException {
        connection = DriverManager.getConnection(URL, NOME, PASSWORD);
    }

    public static synchronized DBConnessione getInstance() throws SQLException {
        if (instance == null) {
            instance = new DBConnessione();
        }
        return instance;
    }


    public Connection getConnection() {
        return connection;
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}