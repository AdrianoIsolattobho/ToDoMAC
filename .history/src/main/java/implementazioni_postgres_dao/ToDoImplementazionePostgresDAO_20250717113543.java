package implementazioni_postgres_dao;

import database.DBConnessione;
import model.*;

import java.net.URI;
import java.net.URL;
import java.util.Calendar;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import java.sql.*;

public class ToDoImplementazionePostgresDAO implements dao.ToDoDAO {

    private Connection connection;

    public ToDoImplementazionePostgresDAO() {
        try {
            connection = DBConnessione.getInstance().getConnection();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void creaToDo(String emailUtente, ToDo toDo, Bacheca bacheca) {
        try {
            // Calcola il nuovo IdToDo come MAX(IdToDo) + 1
            int nuovoIdToDo = 1;
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COALESCE(MAX(\"IdToDo\"), 0) + 1 AS nuovoId FROM \"ToDo\"");
            if (rs.next()) {
                nuovoIdToDo = rs.getInt("nuovoId");
            }
            rs.close();
            stmt.close();

            // Inserisci il nuovo ToDo con il nuovo IdToDo
            PreparedStatement creaPS = connection.prepareStatement(
                    "INSERT INTO \"ToDo\" (\"emailUtente\", \"titolo\", \"descrizione\", \"link\", \"scadenza\", " +
                            "\"completato\", \"scaduto\", \"sfondo\", \"immagine\", \"IdToDo\") " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            creaPS.setString(1, emailUtente);
            creaPS.setString(2, toDo.getTitolo());
            creaPS.setString(3, toDo.getDescrizione());
            creaPS.setString(4, toDo.getLink() != null ? toDo.getLink().toString() : null);
            creaPS.setTimestamp(5,
                    toDo.getScadenza() != null ? new Timestamp(toDo.getScadenza().getTimeInMillis()) : null);
            creaPS.setBoolean(6, toDo.isCompletato());
            creaPS.setBoolean(7, toDo.isScaduto());
            creaPS.setString(8,
                    toDo.getSfondo() != null ? "#" + Integer.toHexString(toDo.getSfondo().getRGB()).substring(2)
                            : null);
            creaPS.setString(9, toDo.getImmagine() != null ? toDo.getImmagine().toString() : null);
            creaPS.setInt(10, nuovoIdToDo);
            creaPS.executeUpdate();
            creaPS.close();

            System.out.println("TitoloBacheca: " + bacheca.getTitolo() + " descrizione: " + bacheca.getDescrizione());

            // INSERISCI SEMPRE UN RECORD PER ASSOCIARE IL TODO ALLA BACHECA
            System.out.println("Associando ToDo alla bacheca: " + bacheca.getTitolo());
            PreparedStatement creaBachecaPS = connection.prepareStatement(
                    "INSERT INTO \"Bacheca\" (\"titolo\",\"emailUtente\", \"descrizione\", \"IdToDo\", \"ordinamento\") " +
                            "VALUES (CAST(? AS titolo), ?, ?, ?, CAST(? AS ordinamento))");
            creaBachecaPS.setString(1, bacheca.getTitolo().toString());
            creaBachecaPS.setString(2, emailUtente);
            creaBachecaPS.setString(3, bacheca.getDescrizione() != null ? bacheca.getDescrizione() : "");
            creaBachecaPS.setInt(4, nuovoIdToDo);
            Ordinamento ordinamento = bacheca.getOrdinamento() != null ? bacheca.getOrdinamento() : Ordinamento.AZ;
            creaBachecaPS.setString(5, ordinamento.name());
            creaBachecaPS.executeUpdate();
            creaBachecaPS.close();

            // Gestione checklist
            if (toDo.getChecklist() != null) {
                for (Attivita attivita : toDo.getChecklist().getAttivita()) {
                    PreparedStatement creaCheckListPS = connection.prepareStatement(
                            "INSERT INTO \"Attivita\" (\"emailUtente\", \"titoloToDo\", \"nome\", \"completata\") " +
                                    "VALUES (?, ?, ?, ?)");
                    creaCheckListPS.setString(1, emailUtente);
                    creaCheckListPS.setString(2, toDo.getTitolo());
                    creaCheckListPS.setString(3, attivita.getNome());
                    creaCheckListPS.setBoolean(4, attivita.isCompletata());
                    creaCheckListPS.executeUpdate();
                    creaCheckListPS.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
public Bacheca caricaBacheca(String emailUtente, Titolo titolo) {
    System.out.println("=== METODO caricaBacheca CHIAMATO ===");
    System.out.println("Email utente: " + emailUtente);
    System.out.println("Titolo bacheca: " + titolo);
    
    Bacheca bacheca = new Bacheca();
    bacheca.setTitolo(titolo);
    
    try {
        // Carica informazioni base della bacheca
        PreparedStatement bachecaInfoPS = connection.prepareStatement(
                "SELECT DISTINCT \"descrizione\", \"ordinamento\" FROM \"Bacheca\" " +
                "WHERE \"emailUtente\" = ? AND \"titolo\" = CAST(? AS titolo)");
        bachecaInfoPS.setString(1, emailUtente);
        bachecaInfoPS.setString(2, titolo.name());
        
        ResultSet bachecaRs = bachecaInfoPS.executeQuery();
        if (bachecaRs.next()) {
            bacheca.setTitolo(titolo);
            bacheca.setDescrizione(bachecaRs.getString("descrizione"));
            String ordinamentoStr = bachecaRs.getString("ordinamento");
            if (ordinamentoStr != null) {
                bacheca.setOrdinamento(Ordinamento.valueOf(ordinamentoStr));
            }
        }
        bachecaRs.close();
        bachecaInfoPS.close();
        
        // Carica i ToDo associati alla bacheca
        List<ToDo> toDoList = caricaToDoPerBacheca(emailUtente, titolo.name());
        bacheca.setToDoList(new ArrayList<>(toDoList));
        
        System.out.println("Bacheca caricata con " + toDoList.size() + " ToDo");
        
    } catch (Exception e) {
        System.out.println("ERRORE nel caricaBacheca:");
        e.printStackTrace();
        return null;
    }
    
    return bacheca;
}

    @Override
    public void modificaToDo(String emailUtente, ToDo toDo, Bacheca bacheca, String oldTitolo) {

        try {
            String sql;
                    sql="SELECT \"IdToDo\", immagine FROM \"ToDo\" WHERE \"emailUtente\" = ? AND \"titolo\" = ?";
            // Recupera IdToDo
            PreparedStatement getIdToDoPS = connection.prepareStatement(sql);
            getIdToDoPS.setString(1, emailUtente);
            getIdToDoPS.setString(2, oldTitolo);

            ResultSet rs = getIdToDoPS.executeQuery();
            int idToDo = -1;
            String immagineAttuale="";
            if (rs.next()) {
                idToDo = rs.getInt("IdToDo");
                immagineAttuale=rs.getString("immagine");
            } else {
                throw new SQLException("ToDo non trovato per email e titolo specificati.");
            }
            rs.close();
            getIdToDoPS.close();
             sql ="UPDATE \"ToDo\" SET " +
                    "\"descrizione\" = ?, \"link\" = ?, \"scadenza\" = ?,  " +
                    "\"completato\" = ?, \"scaduto\" = ?, \"sfondo\" = ?, immagine = COALESCE(?,immagine) ,\"titolo\" = ?" +
                    " WHERE \"emailUtente\" = ? AND \"IdToDo\" = ?";
            // Aggiorna ToDo
            PreparedStatement aggiornaPS = connection.prepareStatement(sql);

            aggiornaPS.setString(1, toDo.getDescrizione());
            aggiornaPS.setString(2, toDo.getLink() != null ? toDo.getLink().toString() : null);
            aggiornaPS.setTimestamp(3, toDo.getScadenza() != null ? new Timestamp(toDo.getScadenza().getTimeInMillis()) : null);
            aggiornaPS.setBoolean(4, toDo.isCompletato());
            aggiornaPS.setBoolean(5, toDo.isScaduto());
            aggiornaPS.setString(6, toDo.getSfondo() != null ? "#" + Integer.toHexString(toDo.getSfondo().getRGB()).substring(2) : null);

            aggiornaPS.setString(7, toDo.getImmagine() != null ? toDo.getImmagine().toString() : immagineAttuale); // <-- Passa null se null
            aggiornaPS.setString(8, toDo.getTitolo());
            aggiornaPS.setString(9, emailUtente);
            aggiornaPS.setInt(10, idToDo);

            aggiornaPS.executeUpdate();
            aggiornaPS.close();

 sql ="DELETE FROM \"Bacheca\" WHERE \"IdToDo\" = ?";
            // Elimina la Bacheca precedente
            PreparedStatement deleteBachecaPS = connection.prepareStatement(sql);
            deleteBachecaPS.setInt(1, idToDo);
            deleteBachecaPS.executeUpdate();
            deleteBachecaPS.close();

            // Inserisce nuova Bacheca
            PreparedStatement insertBachecaPS = connection.prepareStatement(
                    "INSERT INTO \"Bacheca\" " +
                            "(\"IdToDo\", \"titolo\", \"emailUtente\", \"descrizione\", \"ordinamento\") " +
                            "VALUES (?, CAST(? AS titolo), ?, ?, CAST(? AS ordinamento))");
            insertBachecaPS.setInt(1, idToDo);
            insertBachecaPS.setString(2, bacheca.getTitolo().toString());
            insertBachecaPS.setString(3, emailUtente);
            insertBachecaPS.setString(4, bacheca.getDescrizione() != null ? bacheca.getDescrizione() : "");
            insertBachecaPS.setString(5, bacheca.getOrdinamento().name());
            insertBachecaPS.executeUpdate();
            insertBachecaPS.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void eliminaToDo(String emailUtente, String titolo) {
        try {

            PreparedStatement eliminabPS = connection.prepareStatement(
                    "DELETE FROM \"ToDo\" WHERE \"emailUtente\" = ? AND \"titolo\" =?;");

            eliminabPS.setString(1, emailUtente);
            eliminabPS.setString(2, titolo);

            eliminabPS.executeUpdate();
            eliminabPS.close();

            PreparedStatement eliminaPS = connection.prepareStatement(
                    "DELETE FROM \"Attivita\" WHERE \"emailUtente\" = ? AND \"titolo\" =?;");

            eliminaPS.setString(1, emailUtente);
            eliminaPS.setString(2, titolo);

            eliminaPS.executeUpdate();
            eliminaPS.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<ToDo> caricaToDoPerBacheca(String emailUtente, String nomeBacheca) {
        List<ToDo> toDoList = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT T.\"IdToDo\",T.\"titolo\", T.\"descrizione\", T.\"link\", T.\"scadenza\", " +
                    "T.\"completato\", T.\"scaduto\", T.\"sfondo\", T.\"immagine\" " +
                    "FROM \"ToDo\" T JOIN \"Bacheca\" B ON T.\"IdToDo\" = B.\"IdToDo\" " +
                    "WHERE T.\"emailUtente\" = ? AND B.\"titolo\" = CAST(? AS titolo)");
            ps.setString(1, emailUtente);
            ps.setString(2, nomeBacheca);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                String titolo = rs.getString("titolo");
                String descrizione = rs.getString("descrizione");
                URI link = rs.getString("link") != null ? new URI(rs.getString("link")) : null;
                Calendar scadenza = null;
                if (rs.getTimestamp("scadenza") != null) {
                    scadenza = Calendar.getInstance();
                    scadenza.setTime(rs.getTimestamp("scadenza"));
                }
                Color sfondo = rs.getString("sfondo") != null ? Color.decode(rs.getString("sfondo")) : null;
                URL immagine = rs.getString("immagine") != null ? URI.create(rs.getString("immagine")).toURL() : null;

                Checklist checklist = CaricaAttivitaPerToDo(rs.getInt("IdToDo"));

                ToDo t = new ToDo(
                        titolo,
                        descrizione,
                        link,
                        scadenza,
                        sfondo,
                        immagine,
                        checklist.getAttivita() == null || checklist.getAttivita().isEmpty() ? null : checklist);

                t.setCompletato(rs.getBoolean("completato"));
                t.setScaduto(rs.getBoolean("scaduto"));
                toDoList.add(t);
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return toDoList;
    }

    @Override
    public Boolean completaToDo(String emailUtente, String titolo, boolean isCompletato) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "UPDATE \"ToDo\" SET \"completato\" = ? WHERE \"emailUtente\" = ? AND \"titolo\" = ?");
            ps.setBoolean(1, isCompletato);
            ps.setString(2, emailUtente);
            ps.setString(3, titolo);
            int rowsAffected = ps.executeUpdate();
            ps.close();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public Boolean completaAtt(String emailUtente, String titolo, boolean isCompletato, String nome) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "UPDATE \"Attivita\" SET \"completata\" = ? WHERE \"emailUtente\" = ? AND \"titoloToDo\" = ? AND nome = ?");
            ps.setBoolean(1, isCompletato);
            ps.setString(2, emailUtente);
            ps.setString(3, titolo);
            ps.setString(4,nome);
            int rowsAffected = ps.executeUpdate();
            ps.close();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public Checklist CaricaAttivitaPerToDo(int idToDo) {
        Checklist checklist = new Checklist();
        try {
            // Prima recupera il titolo del ToDo usando l'idToDo
            String titoloToDo = null;
            PreparedStatement getTitoloPS = connection.prepareStatement(
                    "SELECT \"titolo\" FROM \"ToDo\" WHERE \"IdToDo\" = ?");
            getTitoloPS.setInt(1, idToDo);
            ResultSet titoloRs = getTitoloPS.executeQuery();
            if (titoloRs.next()) {
                titoloToDo = titoloRs.getString("titolo");
            }
            titoloRs.close();
            getTitoloPS.close();
            
            if (titoloToDo != null) {
                // Ora carica le attività usando il titolo
                PreparedStatement ps = connection.prepareStatement(
                        "SELECT * FROM \"Attivita\" WHERE \"titoloToDo\" = ?");
                ps.setString(1, titoloToDo);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    String nome = rs.getString("nome");
                    boolean completata = rs.getBoolean("completata");
                    Attivita attivita = new Attivita(nome, completata);

                    List<Attivita> attivitaList = checklist.getAttivita();
                    if (attivitaList == null) {
                        attivitaList = new ArrayList<>();
                        checklist.setAttivita(attivitaList);
                    }
                    attivitaList.add(attivita);
                }
                rs.close();
                ps.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return checklist;
    }

}
