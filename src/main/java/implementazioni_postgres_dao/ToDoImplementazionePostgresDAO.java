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
                    ordinamento != null ? ordinamento.name() : Ordinamento.AZ.name()
            );


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
    public Bacheca caricaBacheca(String emailUtente, String titolo) {
        Bacheca b = null;
        try {
            PreparedStatement caricaPS = connection.prepareStatement(
                    "SELECT * FROM \"Bacheca\" NATURAL JOIN \"ToDo\" WHERE \"emailUtente\" = ? AND \"titoloBacheca\" = ?"
            );
            caricaPS.setString(1, emailUtente);
            caricaPS.setString(2, titolo);

            ResultSet rs = caricaPS.executeQuery();

            if (rs.next()) {
                Titolo titoloBacheca = Titolo.valueOf(rs.getString("titoloBacheca"));
                String descrizioneBacheca = rs.getString("descrizioneBacheca");
                String ordinamentoStr = rs.getString("ordinamento");
                Ordinamento ordinamento = (ordinamentoStr != null)
                        ? Ordinamento.valueOf(ordinamentoStr)
                        : Ordinamento.AZ;

                // Popolazione dei ToDo utilizzando il metodo già esistente
                List<ToDo> toDoList = caricaToDoPerBacheca(emailUtente, titoloBacheca.toString());

                b = new Bacheca(
                        titoloBacheca,
                        descrizioneBacheca,
                        ordinamento,
                        toDoList
                );
            }

            rs.close();
            caricaPS.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }

    @Override
    public void modificaToDo(String emailUtente, ToDo toDo) {
        try {
            PreparedStatement aggiornaPS = connection.prepareStatement(
                    "UPDATE \"ToDo\" SET " +
                            "\"descrizione\" = ?, \"link\" = ?, \"scadenza\" = ?,  " +
                            "\"completato\" = ?, \"scaduto\" = ?, \"sfondo\" = ?, \"immagine\" = ? " +
                            "WHERE \"emailUtente\" = ? AND \"titolo\" = ?");

            aggiornaPS.setString(1, toDo.getDescrizione());
            aggiornaPS.setString(2, toDo.getLink() != null ? toDo.getLink().toString() : null);
            aggiornaPS.setTimestamp(3,
                    toDo.getScadenza() != null ? new Timestamp(toDo.getScadenza().getTimeInMillis()) : null);
            aggiornaPS.setBoolean(4, toDo.isCompletato());
            aggiornaPS.setBoolean(5, toDo.isScaduto());
            aggiornaPS.setString(6,
                    toDo.getSfondo() != null ? "#" + Integer.toHexString(toDo.getSfondo().getRGB()).substring(2)
                            : null);
            aggiornaPS.setString(7, toDo.getImmagine() != null ? toDo.getImmagine().toString() : null);
            aggiornaPS.setString(8, emailUtente);
            aggiornaPS.setString(9, toDo.getTitolo());

            aggiornaPS.executeUpdate();
            aggiornaPS.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void eliminaToDo(String emailUtente, String titolo) {
        try {
            PreparedStatement eliminaPS = connection.prepareStatement(
                    "DELETE FROM \"ToDo\" WHERE \"emailUtente\" = ? AND \"titolo\" =?;");

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
