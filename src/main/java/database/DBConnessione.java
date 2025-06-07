package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DBConnessione {
    private static DBConnessione instance;
    public Connection connection = null;
    private String nome = "postgres";
    private String password = "password";
    private String url = "jdbc:postgresql://localhost:5432/todomac";
    private String driver = "org.postgresql.Driver";

    //costruttore
    private DBConnessione () throws SQLException{
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url,nome,password);
        } catch (ClassNotFoundException ex){
            System.out.println("Database Connection Creation Failed: "+ ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static DBConnessione getInstance() throws SQLException {
        //crea una nuova connessione se non esiste/è chiusa
        if (instance == null) {
            instance = new DBConnessione();
        } else if (instance.connection.isClosed()) {
            //riferimento a quella esistente
            instance = new DBConnessione();
        }
        return instance;
    }

}
