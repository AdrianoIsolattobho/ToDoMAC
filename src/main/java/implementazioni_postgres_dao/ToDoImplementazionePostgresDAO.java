package implementazioni_postgres_dao;

import database.DBConnessione;
import model.*;
import java.awt.Color;
import java.net.URI;
import java.net.URL;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementazione concreta dell'interfaccia {@link dao.ToDoDAO} per
 * PostgreSQL.
 * Gestisce tutte le operazioni di accesso ai dati relativi ai ToDo.
 */
public class ToDoImplementazionePostgresDAO implements dao.ToDoDAO {
    private Connection connection;
    private static final Logger logger = Logger.getLogger(ToDoImplementazionePostgresDAO.class.getName());

    public ToDoImplementazionePostgresDAO() {
        try {
            connection = DBConnessione.getInstance().getConnection();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * Crea un nuovo ToDo all'interno di una bacheca di un utente
     * @param emailUtente email utente attuale
     * @param toDo todo attuale
     * @param bacheca bacheca attuale
     */
    @Override
    public void creaToDo(String emailUtente, ToDo toDo, Bacheca bacheca) {
        try {
            int nuovoIdToDo = getNextIdToDo();

            inserisciToDo(emailUtente, toDo, nuovoIdToDo);
            inserisciBacheca(emailUtente, bacheca, nuovoIdToDo);

            if (toDo.getChecklist() != null) {
                inserisciChecklist(emailUtente, toDo);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore durante la creazione del ToDo", e);
        }
    }

    /**
     * Inserisce un ToDo all'interno del database.
     * @param emailUtente
     * @param toDo
     * @param idToDo
     * @throws SQLException se c'è un errore con l'inserimento
     */
    private void inserisciToDo(String emailUtente, ToDo toDo, int idToDo) throws SQLException {
        String sql = """
                INSERT INTO "ToDo" ("emailUtente", "titolo", "descrizione", "link", "scadenza",
                "completato", "scaduto", "sfondo", "immagine", "IdToDo")
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, emailUtente);
            ps.setString(2, toDo.getTitolo());
            ps.setString(3, toDo.getDescrizione());
            ps.setString(4, toDo.getLink() != null ? toDo.getLink().toString() : null);
            ps.setTimestamp(5, toDo.getScadenza() != null ? new Timestamp(toDo.getScadenza().getTimeInMillis()) : null);
            ps.setBoolean(6, toDo.isCompletato());
            ps.setBoolean(7, toDo.isScaduto());
            ps.setString(8, colorToHex(toDo.getSfondo()));
            ps.setString(9, toDo.getImmagine() != null ? toDo.getImmagine().toString() : null);
            ps.setInt(10, idToDo);
            ps.executeUpdate();
        }
    }

    /**
     * Inserisce una bacheca all'interno del database.
     * @param emailUtente
     * @param bacheca
     * @param idToDo
     * @throws SQLException se c'è un errore con l'inserimento
     */
    private void inserisciBacheca(String emailUtente, Bacheca bacheca, int idToDo) throws SQLException {
        String sql = """
                INSERT INTO "Bacheca" ("titolo", "emailUtente", "descrizione", "IdToDo", "ordinamento")
                VALUES (CAST(? AS titolo), ?, ?, ?, CAST(? AS ordinamento))
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, bacheca.getTitolo().toString());
            ps.setString(2, emailUtente);
            ps.setString(3, Optional.ofNullable(bacheca.getDescrizione()).orElse(""));
            ps.setInt(4, idToDo);
            ps.setString(5, Optional.ofNullable(bacheca.getOrdinamento()).orElse(Ordinamento.AZ).name());
            ps.executeUpdate();
        }
    }

    /**
     * Metodo helper che prende il valore max di IdToDo + 1 dal database.
     * @return un id di ToDo
     * @throws SQLException
     */
    private int getNextIdToDo() throws SQLException {
        String sql = "SELECT COALESCE(MAX(\"IdToDo\"), 0) + 1 AS nuovoId FROM \"ToDo\"";
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getInt("nuovoId") : 1;
        }
    }

    /**
     * Inserisce una checklist all'interno del database.
     * @param emailUtente
     * @param toDo
     */
    private void inserisciChecklist(String emailUtente, ToDo toDo) {
        String sql = """
                INSERT INTO "Attivita" ("emailUtente", "titoloToDo", "nome", "completata")
                VALUES (?, ?, ?, ?)
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, emailUtente);
            ps.setString(2, toDo.getTitolo());
            for (Attivita attivita : toDo.getChecklist().getAttivita()) {
                ps.setString(3, attivita.getNome());
                ps.setBoolean(4, attivita.isCompletata());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            logger.severe("Errore durante inserimento ToDo: " + e.getMessage());
        }

    }

    /**
     * Carica le informazioni di una bacheca di un utente all'interno del database.
     * @param emailUtente email utente attuale
     * @param titolo titolo bacheca attuale
     * @return
     */
    @Override
    public Bacheca caricaBacheca(String emailUtente, Titolo titolo) {
        Bacheca bacheca = new Bacheca();
        bacheca.setTitolo(titolo);
        String sql = """
                SELECT DISTINCT "descrizione", "ordinamento"
                FROM "Bacheca"
                WHERE "emailUtente" = ? AND "titolo" = CAST(? AS titolo)
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, emailUtente);
            ps.setString(2, titolo.name());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    bacheca.setDescrizione(rs.getString("descrizione"));
                    String ordinamento = rs.getString("ordinamento");
                    if (ordinamento != null) {
                        bacheca.setOrdinamento(Ordinamento.valueOf(ordinamento));
                    }
                }
            }
            bacheca.setToDoList(new ArrayList<>(caricaToDoPerBacheca(emailUtente, titolo.name())));
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore durante il caricamento della Bacheca", e);
            return null;
        }
        return bacheca;
    }

    /**
     * Metodo per modificare un'immagine di un ToDo già creato.
     * @param emailUtente email utente attuale
     * @param toDo todo da modificare
     * @param bacheca bacheca attuale
     * @param oldTitolo il titolo precedente del ToDo (prima della modifica)
     */
    @Override
    public void modificaToDo(String emailUtente, ToDo toDo, Bacheca bacheca, String oldTitolo) {
        String sql = "SELECT \"IdToDo\", \"immagine\" FROM \"ToDo\" WHERE \"emailUtente\" = ? AND \"titolo\" = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, emailUtente);
            ps.setString(2, oldTitolo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int idToDo = rs.getInt("IdToDo");
                    String immagineAttuale = rs.getString("immagine");

                    aggiornaToDo(emailUtente, toDo, idToDo, immagineAttuale);
                    eliminaBacheca(idToDo);
                    inserisciBacheca(emailUtente, bacheca, idToDo);
                } else {
                    logger.warning("ToDo non trovato per modifica con titolo:");
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore durante la modifica del ToDo", e);
        }
    }

    /**
     * Aggiorna le informazioni di un ToDo già creato.
     * @param emailUtente
     * @param toDo
     * @param idToDo
     * @param immagineAttuale
     * @throws SQLException
     */
    private void aggiornaToDo(String emailUtente, ToDo toDo, int idToDo, String immagineAttuale) throws SQLException {
        String sql = """
                UPDATE "ToDo"
                SET "descrizione" = ?, "link" = ?, "scadenza" = ?, "completato" = ?,
                "scaduto" = ?, "sfondo" = ?, "immagine" = COALESCE(?, "immagine"), "titolo" = ?
                WHERE "emailUtente" = ? AND "IdToDo" = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, toDo.getDescrizione());
            ps.setString(2, toDo.getLink() != null ? toDo.getLink().toString() : null);
            ps.setTimestamp(3, toDo.getScadenza() != null ? new Timestamp(toDo.getScadenza().getTimeInMillis()) : null);
            ps.setBoolean(4, toDo.isCompletato());
            ps.setBoolean(5, toDo.isScaduto());
            ps.setString(6, colorToHex(toDo.getSfondo()));
            ps.setString(7, toDo.getImmagine() != null ? toDo.getImmagine().toString() : immagineAttuale);
            ps.setString(8, toDo.getTitolo());
            ps.setString(9, emailUtente);
            ps.setInt(10, idToDo);
            ps.executeUpdate();
        }
    }

    /**
     * Elimina un ToDo da una bacheca.
     * @param idToDo
     * @throws SQLException
     */
    private void eliminaBacheca(int idToDo) throws SQLException {
        String sql = "DELETE FROM \"Bacheca\" WHERE \"IdToDo\" = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idToDo);
            ps.executeUpdate();
        }
    }

    /**
     * Elimina un ToDo dal database.
     * @param emailUtente email utente attuale
     * @param titolo titolo del todo da eliminare
     */
    @Override
    public void eliminaToDo(String emailUtente, String titolo) {
        String sqlToDo = "DELETE FROM \"ToDo\" WHERE \"emailUtente\" = ? AND \"titolo\" = ?";
        String sqlAttivita = "DELETE FROM \"Attivita\" WHERE \"emailUtente\" = ? AND \"titoloToDo\" = ?";
        try (PreparedStatement psToDo = connection.prepareStatement(sqlToDo);
                PreparedStatement psAttivita = connection.prepareStatement(sqlAttivita)) {
            psToDo.setString(1, emailUtente);
            psToDo.setString(2, titolo);
            psToDo.executeUpdate();

            psAttivita.setString(1, emailUtente);
            psAttivita.setString(2, titolo);
            psAttivita.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore durante l'eliminazione del ToDo", e);
        }
    }

    /**
     * Carica i ToDo di un utente all'interno della bacheca specificata.
     * Utilizzato per caricare i ToDo di un utente al login.
     * @param emailUtente email utente attuale
     * @param nomeBacheca titolo bacheca attuale
     * @return
     */
    @Override
    public List<ToDo> caricaToDoPerBacheca(String emailUtente, String nomeBacheca) {
        List<ToDo> toDoList = new ArrayList<>();
        String sql = """
                SELECT T."IdToDo", T."titolo", T."descrizione", T."link", T."scadenza",
                T."completato", T."scaduto", T."sfondo", T."immagine"
                FROM "ToDo" T
                JOIN "Bacheca" B ON T."IdToDo" = B."IdToDo"
                WHERE T."emailUtente" = ? AND B."titolo" = CAST(? AS titolo)
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, emailUtente);
            ps.setString(2, nomeBacheca);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ToDo toDo = parseToDoFromResultSet(rs);
                    toDoList.add(toDo);
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Errore durante il caricamento dei ToDo per la Bacheca", e);
        }
        return toDoList;
    }

    /**
     * Metodo helper per analizzare i ToDo dal ResultSet.
     * @param rs
     * @return un oggetto ToDo con tutte le informazioni del ResultSet.
     * @throws SQLException
     */
    private ToDo parseToDoFromResultSet(ResultSet rs) throws SQLException {
        String titolo = rs.getString("titolo");
        String descrizione = rs.getString("descrizione");

        URI link = null;
        String linkStr = rs.getString("link");
        if (linkStr != null) {
            link = URI.create(linkStr);
        }

        Calendar scadenza = null;
        Timestamp ts = rs.getTimestamp("scadenza");
        if (ts != null) {
            scadenza = Calendar.getInstance();
            scadenza.setTime(ts);
        }

        Color sfondo = null;
        String sfondoStr = rs.getString("sfondo");
        if (sfondoStr != null) {
            sfondo = Color.decode(sfondoStr);
        }

        URL immagine = null;
        String imgStr = rs.getString("immagine");
        if (imgStr != null) {
            try {
                immagine = URI.create(imgStr).toURL();
            } catch (Exception _) {
                logger.warning("Immagine non valida: " + imgStr);
            }

        }

        Checklist checklist = CaricaAttivitaPerToDo(rs.getInt("IdToDo"));
        if (checklist.getAttivita() != null && checklist.getAttivita().isEmpty()) {
            checklist = null;
        }

        ToDo toDo = new ToDo(titolo, descrizione, link, scadenza, sfondo, immagine, checklist);
        toDo.setCompletato(rs.getBoolean("completato"));
        toDo.setScaduto(rs.getBoolean("scaduto"));
        return toDo;
    }

    /**
     * Imposta lo stato di un ToDo in completo.
     * @param emailUtente email utente attuale
     * @param titolo titolo del todo da modificare
     * @param isCompletato stato del todo attuale
     * @return true se il todo è stato completato, false altrimenti.
     */
    @Override
    public Boolean completaToDo(String emailUtente, String titolo, boolean isCompletato) {
        String sql = "UPDATE \"ToDo\" SET \"completato\" = ? WHERE \"emailUtente\" = ? AND \"titolo\" = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setBoolean(1, isCompletato);
            ps.setString(2, emailUtente);
            ps.setString(3, titolo);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore durante il completamento del ToDo", e);
            return false;
        }
    }

    /**
     * Imposta lo stato di un'Attivita di un ToDo in completo.
     * @param emailUtente email utente attuale
     * @param titolo titolo del todo attuale
     * @param isCompletato stato del todo
     * @param nome nome dell'attività
     * @return true se l'attività è stato completata, false altrimenti.
     */
    @Override
    public Boolean completaAtt(String emailUtente, String titolo, boolean isCompletato, String nome) {
        String sql = "UPDATE \"Attivita\" SET \"completata\" = ? WHERE \"emailUtente\" = ? AND \"titoloToDo\" = ? AND nome = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setBoolean(1, isCompletato);
            ps.setString(2, emailUtente);
            ps.setString(3, titolo);
            ps.setString(4, nome);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore durante il completamento dell'Attività", e);
            return false;
        }
    }

    /**
     * Carica le Attivita di un ToDo di un utente.
     * Utile per recuperare tutte le attività al momento del login.
     * @param idToDo id del todo da cui viene presa la checklist
     * @return la checklist con tutte le sue attività
     */
    @Override
    public Checklist CaricaAttivitaPerToDo(int idToDo) {
        Checklist checklist = new Checklist();
        String titoloToDo = null;
        String sqlTitolo = "SELECT \"titolo\" FROM \"ToDo\" WHERE \"IdToDo\" = ?";
        try (PreparedStatement psTitolo = connection.prepareStatement(sqlTitolo)) {
            psTitolo.setInt(1, idToDo);
            try (ResultSet rs = psTitolo.executeQuery()) {
                if (rs.next()) {
                    titoloToDo = rs.getString("titolo");
                }
            }
            if (titoloToDo != null) {
                String sqlAtt = "SELECT nome, completata FROM \"Attivita\" WHERE \"titoloToDo\" = ?";
                try (PreparedStatement psAtt = connection.prepareStatement(sqlAtt)) {
                    psAtt.setString(1, titoloToDo);
                    try (ResultSet rs = psAtt.executeQuery()) {
                        List<Attivita> attivitaList = new ArrayList<>();
                        while (rs.next()) {
                            attivitaList.add(new Attivita(rs.getString("nome"), rs.getBoolean("completata")));
                        }
                        checklist.setAttivita(attivitaList);
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore durante il caricamento delle Attività per il ToDo", e);
        }
        return checklist;
    }

    /**
     * Metodo helper per convertire un colore in un stringa hex.
     * @param color
     * @return una stringa con il codice hex del colore
     */
    private String colorToHex(Color color) {
        return color != null ? "#" + Integer.toHexString(color.getRGB()).substring(2) : null;
    }
}
