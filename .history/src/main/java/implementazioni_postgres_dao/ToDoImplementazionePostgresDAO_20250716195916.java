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
            // Inserisci la Bacheca con lo stesso IdToDo
            PreparedStatement creaBachecaPS = connection.prepareStatement(
                    "INSERT INTO \"Bacheca\" (\"titoloBacheca\", \"descrizioneBacheca\", \"IdToDo\", \"ordinamento\") VALUES (?, ?, ?,?)");
            creaBachecaPS.setString(1, bacheca.getTitolo().toString());
            creaBachecaPS.setString(2, bacheca.getDescrizione() != null ? bacheca.getDescrizione() : "");
            creaBachecaPS.setInt(3, nuovoIdToDo);
            Ordinamento ordinamento = bacheca.getOrdinamento() != null ? bacheca.getOrdinamento() : Ordinamento.AZ;
            creaBachecaPS.setString(4,
                    ordinamento != null ? ordinamento.name() : Ordinamento.AZ.name());

            creaBachecaPS.executeUpdate();
            creaBachecaPS.close();

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
    Bacheca b = null;
    try {
        PreparedStatement caricaPS = connection.prepareStatement(
                "SELECT * FROM \"Bacheca\" NATURAL JOIN \"ToDo\" WHERE \"emailUtente\" = ? AND \"titolo\" = CAST(? AS titolo)");
        caricaPS.setString(1, emailUtente);
        caricaPS.setString(2, titolo.name());

        // ...existing code...
    } catch (Exception e) {
        e.printStackTrace();
    }
    return b;
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
                            "(\"IdToDo\", \"titoloBacheca\", \"descrizioneBacheca\", \"ordinamento\") "
                            +
                            "VALUES (?, ?, ?, ?)");
            insertBachecaPS.setInt(1, idToDo);
            insertBachecaPS.setString(2, bacheca.getTitolo().toString());
            insertBachecaPS.setString(3, bacheca.getDescrizione() != null ? bacheca.getDescrizione() : "");
            insertBachecaPS.setString(4, bacheca.getOrdinamento().name());
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

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<ToDo> caricaToDoPerBacheca(String emailUtente, String nomeBacheca) {
        List<ToDo> toDoList = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM \"ToDo\" NATURAL JOIN \"Bacheca\" WHERE \"emailUtente\" = ? AND \"titoloBacheca\" = ?");
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
                ToDo t = new ToDo(
                        titolo,
                        descrizione,
                        link,
                        scadenza,
                        sfondo,
                        immagine,
                        null // oppure la checklist se la gestisci
                );
                t.setCompletato(rs.getBoolean("completato"));
                t.setScaduto(rs.getBoolean("scaduto"));
                // Imposta altri campi se necessario
                toDoList.add(t);
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return toDoList;
    }

}
