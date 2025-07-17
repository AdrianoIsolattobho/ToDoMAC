package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe singleton per la gestione della connessione al database PostgreSQL.
 * Fornisce un'unica istanza condivisa dell'oggetto {@link Connection} per evitare
 * la creazione multipla di connessioni durante l'esecuzione dell'applicazione.
 */
public class DBConnessione {
    private static DBConnessione instance;
    private Connection connection;
    private static final String NOME = "postgres";
    //Possiamo ignorare il problema di sonar qube riguardo alla password
    // perchè la sicurezza non è l'obiettivo in questo contesto didattico
    private static final String PASSWORD = "Angelozampa15."; //NOSONAR
    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";


    /**
     * Costruttore privato per impedire l'istanziazione diretta dall'esterno.
     * Inizializza la connessione al database.
     *
     * @throws SQLException se la connessione non può essere stabilita
     */
    private DBConnessione() throws SQLException {
        connection = DriverManager.getConnection(URL, NOME, PASSWORD);
    }

    /**
     * Restituisce l'istanza singleton della classe, creandola se necessario.
     * È sincronizzato per essere thread-safe.
     *
     * @return l'unica istanza di {@code DBConnessione}
     * @throws SQLException se la connessione non può essere creata
     */
    public static synchronized DBConnessione getInstance() throws SQLException {
        if (instance == null) {
            instance = new DBConnessione();
        }
        return instance;
    }

    /**
     * Restituisce l'oggetto {@code Connection} attivo.
     *
     * @return l'oggetto {@code Connection}
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Chiude la connessione al database se aperta.
     *
     * @throws SQLException se si verifica un errore durante la chiusura
     */
    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}