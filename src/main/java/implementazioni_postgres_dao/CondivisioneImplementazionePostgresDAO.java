package implementazioni_postgres_dao;

import database.DBConnessione;
import model.*;

import java.awt.*;
import java.io.File;
import java.net.URI;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Logger;

/**
 * Implementazione concreta dell'interfaccia {@link dao.CondivisioneDAO} per
 * PostgreSQL.
 * Gestisce tutte le operazioni di accesso ai dati relativi alle condivisioni.
 **/

public class CondivisioneImplementazionePostgresDAO implements dao.CondivisioneDAO {

    private static final Logger logger = Logger.getLogger(CondivisioneImplementazionePostgresDAO.class.getName());
    private final Connection connection;

    public CondivisioneImplementazionePostgresDAO() {
        try {
            connection = DBConnessione.getInstance().getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Errore di connessione al database", e);
        }
    }

    /**
     * Aggiunge una nuova condivisione di un ToDo tra un autore e un altro utente.
     * @param emailAutore email dell'utente creatore della condivisione
     * @param titoloToDo titolo del todo condiviso
     * @param emailUtenteCondiviso email dell'utente con cui è stato condiviso il todo
     */
    @Override
    public void aggiungiCondivisione(String emailAutore, String titoloToDo, String emailUtenteCondiviso) {
        String sql = """
                INSERT INTO "Condivisione" ("emailUtente", "titoloToDo", "emailAutore")
                VALUES (?, ?, ?)
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, emailUtenteCondiviso);
            ps.setString(2, titoloToDo);
            ps.setString(3, emailAutore);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.severe("Errore durante l'aggiunta della condivisione: " + e.getMessage());
        }
    }

    /**
     * Elimina una condivisione esistente tra un autore e un altro utente.
     * @param emailAutore email dell'utente creatore della condivisione
     * @param titoloToDo titolo del todo condiviso
     * @param emailUtenteCondiviso email dell'utente con cui è stato condiviso il todo
     * @return true se almeno una riga è stata eliminata, false altrimenti.
     */
    @Override
    public Boolean eliminaCondivisione(String emailAutore, String titoloToDo, String emailUtenteCondiviso) {
        String sql = """
                DELETE FROM "Condivisione"
                WHERE "emailUtente" = ? AND "titoloToDo" = ? AND "emailAutore" = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, emailUtenteCondiviso);
            ps.setString(2, titoloToDo);
            ps.setString(3, emailAutore);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            logger.severe("Errore durante l'eliminazione della condivisione: " + e.getMessage());
            return false;
        }
    }

    /**
     *  Restituisce un arraylist contenente tutti gli utenti condivisi con un utente.
     * @param emailAutore email dell'utente creatore della condivisione
     * @param titoloToDo titolo del todo condiviso
     * @return lista di email degli utenti condivisi con l'utente. Se non ci sono utenti condivisi.
     */
    @Override
    public ArrayList<String> getUtentiCondivisiPerToDo(String emailAutore, String titoloToDo) {
        ArrayList<String> utenti = new ArrayList<>();
        String sql = """
                SELECT "emailUtente"
                FROM "Condivisione"
                WHERE "emailAutore" = ? AND "titoloToDo" = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, emailAutore);
            ps.setString(2, titoloToDo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    utenti.add(rs.getString("emailUtente"));
                }
            }
        } catch (SQLException e) {
            logger.severe("Errore nel recupero degli utenti condivisi: " + e.getMessage());
        }
        return utenti;
    }

    /**
     * Restituisce tutti i ToDo che un autore ha condiviso con un altro utente.
     * @param emailAutore email dell'utente con cui sono stati condivisi dei todo specifici
     * @return lista di oggetti todo condivisi dall'autore
     */
    @Override
    public ArrayList<ToDo> getToDoCondivisiPerUtente(String emailAutore) {
        ArrayList<ToDo> todos = new ArrayList<>();
        String sql = """
                SELECT T.*
                FROM "ToDo" T
                JOIN "Condivisione" C ON T."titolo" = C."titoloToDo" AND T."emailUtente" = C."emailAutore"
                WHERE C."emailAutore" = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, emailAutore);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ToDo todo = buildToDoFromResultSet(rs);
                    aggiungiChecklistToDo(todo, emailAutore, todo.getTitolo());
                    todos.add(todo);
                }
            }
        } catch (SQLException e) {
            logger.severe("Errore nel recupero dei ToDo condivisi per l'utente: " + e.getMessage());
        }
        return todos;
    }

    /**
     * Restituisce tutti i ToDo che sono stati condivisi con un determinato utente.
     * Ogni Condivisione contiene il ToDo condiviso e l'autore.
     * @param emailUtente email utente con cui sono stati condivisi i todo
     * @return lista di oggetti condivisione che rappresentano i todo ricevuti in condivisione
     */
    @Override
    public ArrayList<Condivisione> getToDoPerUtenteCondiviso(String emailUtente) {
        ArrayList<Condivisione> condivisioni = new ArrayList<>();
        String sql = """
                SELECT "emailAutore", "titoloToDo"
                FROM "Condivisione"
                WHERE "emailUtente" = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, emailUtente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String emailAutore = rs.getString("emailAutore");
                    String titoloToDo = rs.getString("titoloToDo");

                    ToDo todo = recuperaToDo(emailAutore, titoloToDo);
                    if (todo != null) {
                        aggiungiChecklistToDo(todo, emailAutore, titoloToDo);
                        ArrayList<Utente> utentiCondivisi = new ArrayList<>();
                        Utente u = new Utente();
                        u.setEmail(emailUtente);
                        utentiCondivisi.add(u);
                        condivisioni.add(new Condivisione(emailAutore, todo, utentiCondivisi));
                    }
                }
            }
        } catch (SQLException e) {
            logger.severe("Errore nel recupero dei ToDo condivisi con l'utente: " + e.getMessage());
        }
        return condivisioni;
    }

    /**
     * Metodo helper per recuperare ToDo di un utente preciso
     * @param emailAutore
     * @param titoloToDo
     * @return todo cercato
     */
    private ToDo recuperaToDo(String emailAutore, String titoloToDo) {
        String sql = """
                SELECT "titolo", "emailUtente", "descrizione", link, scadenza, completato, scaduto, immagine, sfondo
                FROM "ToDo"
                WHERE "titolo" = ? AND "emailUtente" = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, titoloToDo);
            ps.setString(2, emailAutore);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return buildToDoFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            logger.warning("Errore nel recupero del ToDo '" + titoloToDo + "' per '" + emailAutore + "': " + e.getMessage());
        }
        return null;
    }

    /**
     * Metodo helper che costruisce un oggetto ToDo a partire dal ResultSet proveniente da una query SQL.
     * Utilizza metodi ausiliari per gestire campi potenzialmente nulli o malformati.
     * @param rs
     * @return un oggetto todo popolato con i dati estratti
     * @throws SQLException se si verifica un problema nella lettura dei dati dal ResultSet.
     */
    private ToDo buildToDoFromResultSet(ResultSet rs) throws SQLException {
        ToDo todo = new ToDo();
        todo.setTitolo(rs.getString("titolo"));
        todo.setDescrizione(rs.getString("descrizione"));
        setSafeLink(rs.getString("link"), todo);
        setSafeScadenza(rs.getTimestamp("scadenza"), todo);
        todo.setCompletato(rs.getBoolean("completato"));
        todo.setScaduto(rs.getBoolean("scaduto"));
        setSafeSfondo(rs.getString("sfondo"), todo);
        verificaPresenzaImmagine(rs.getString("immagine"));
        return todo;
    }

    /**
     * Aggiunge una checklist al ToDo condiviso
     * @param todo
     * @param emailAutore
     * @param titoloToDo
     */
    private void aggiungiChecklistToDo(ToDo todo, String emailAutore, String titoloToDo) {
        String sql = """
                SELECT nome
                FROM "Attivita"
                WHERE "emailUtente" = ? AND "titoloToDo" = ?
                """;
        Checklist checklist = new Checklist();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, emailAutore);
            ps.setString(2, titoloToDo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    checklist.getAttivita().add(new Attivita(rs.getString("nome"), false));
                }
            }
        } catch (SQLException e) {
            logger.warning("Errore durante il caricamento della checklist per '" + titoloToDo + "': " + e.getMessage());
        }
        todo.setChecklist(checklist);
    }

    /**
     * Metodo helper per controllare che la stringa sia un link valido.
     * @param link
     * @param todo
     */
    private void setSafeLink(String link, ToDo todo) {
        if (link == null || link.isEmpty()) {
            todo.setLink(null);
            return;
        }
        try {
            URI uri = new URI(link);
            if (!uri.isAbsolute()) {
                uri = new URI("http://" + link);
            }
            todo.setLink(uri);
        } catch (Exception _) {
            logger.warning("Link non valido ignorato: " + link);
            todo.setLink(null);
        }
    }

    /**
     * Metodo helper per controllare che la scadenza sia valida.
     * @param ts
     * @param todo
     */
    private void setSafeScadenza(Timestamp ts, ToDo todo) {
        if (ts != null) {
            Calendar scadenza = Calendar.getInstance();
            scadenza.setTimeInMillis(ts.getTime());
            todo.setScadenza(scadenza);
        }
    }

    /**
     * Metodo helper per controllare che il colore sia valido.
     * @param sfondoHex
     * @param todo
     */
    private void setSafeSfondo(String sfondoHex, ToDo todo) {
        if (sfondoHex != null && sfondoHex.startsWith("#")) {
            try {
                todo.setSfondo(Color.decode(sfondoHex));
            } catch (NumberFormatException _) {
                logger.warning("Sfondo non valido ignorato: " + sfondoHex);
            }
        }
    }

    /**
     * Metodo helper per controllare che l'immagine sia presente nel database.
     * @param img
     */
    private void verificaPresenzaImmagine(String img) {
        if (img != null && !img.isEmpty()) {
            File imgFile = new File("immagini_todo/" + img);
            if (!imgFile.exists()) {
                logger.warning("Immagine non trovata: " + imgFile.getAbsolutePath());
            }
        }
    }
}
