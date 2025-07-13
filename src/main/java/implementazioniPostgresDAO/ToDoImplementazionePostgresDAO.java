package implementazioniPostgresDAO;

import database.DBConnessione;
import model.ToDo;
import model.Attivita;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import java.awt.Color;
import java.net.URI;


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
/**
 * Il metodo permette di inserire i promemoria nel database, prendendo */
    @Override
    public void creaToDo(String emailUtente, ToDo toDo) {
        try {
            PreparedStatement creaPS = connection.prepareStatement(
                    "INSERT INTO \"ToDo\" (\"emailUtente\", \"titolo\", \"descrizione\", \"link\", \"scadenza\", " +
                            "\"completato\", \"scaduto\", \"sfondo\", \"immagine\") " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

            creaPS.setString(1, emailUtente);
            creaPS.setString(2, toDo.getTitolo());
            creaPS.setString(3, toDo.getDescrizione());
            creaPS.setString(4, toDo.getLink() != null ? toDo.getLink().toString() : null);
            creaPS.setTimestamp(5, toDo.getScadenza() != null ? new Timestamp(toDo.getScadenza().getTimeInMillis()) : null);
            creaPS.setBoolean(6, toDo.isCompletato());
            creaPS.setBoolean(7, toDo.isScaduto());
            creaPS.setString(8, toDo.getSfondo() != null ? "#" + Integer.toHexString(toDo.getSfondo().getRGB()).substring(2) : null);
            creaPS.setString(9, toDo.getImmagine() != null ? toDo.getImmagine().toString() : null);

            creaPS.executeUpdate();

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
    public void modificaToDo(String emailUtente, ToDo toDo) {
        try{
            PreparedStatement aggiornaPS = connection.prepareStatement(
                    "UPDATE \"ToDo\" SET " +
                            "\"descrizione\" = ?, \"link\" = ?, \"scadenza\" = ?,  " +
                            "\"completato\" = ?, \"scaduto\" = ?, \"sfondo\" = ?, \"immagine\" = ? " +
                            "WHERE \"emailUtente\" = ? AND \"titolo\" = ?");

            aggiornaPS.setString(1, toDo.getDescrizione());
            aggiornaPS.setString(2, toDo.getLink()!= null ? toDo.getLink().toString() : null);
            aggiornaPS.setTimestamp(3,toDo.getScadenza() != null ? new Timestamp(toDo.getScadenza().getTimeInMillis()) : null);
            aggiornaPS.setBoolean(4,toDo.isCompletato());
            aggiornaPS.setBoolean(5,toDo.isScaduto());
            aggiornaPS.setString(6,toDo.getSfondo() != null ? "#" + Integer.toHexString(toDo.getSfondo().getRGB()).substring(2) : null);
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
    public void eliminaToDo(String emailUtente, String titolo){
        try{
            PreparedStatement eliminaPS = connection.prepareStatement(
                    "DELETE FROM \"ToDo\" WHERE \"emailUtente\" = ? AND \"titolo\" =?;");

            eliminaPS.setString(1,emailUtente);
            eliminaPS.setString(2, titolo);

            eliminaPS.executeUpdate();
            eliminaPS.close();

        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public List<ToDo> getToDoPerUtente(String emailUtente) {
        List<ToDo> toDoList = new ArrayList<>();
        try{
            PreparedStatement getToDoPerUtentePS = connection.prepareStatement(
                    "SELECT * FROM \"ToDo\" WHERE \"emailUtente\" = ?");

            getToDoPerUtentePS.setString(1, emailUtente);

            ResultSet rs = getToDoPerUtentePS.executeQuery();

            while (rs.next()){
                ToDo toDo = new ToDo();
                toDo.setTitolo(rs.getString("titolo"));
                toDo.setDescrizione(rs.getString("descrizione"));
                toDo.setLink(URI.create(rs.getString("link")));
                Timestamp scadenzats = rs.getTimestamp("scadenza");
                if (scadenzats != null) {
                    Calendar scadenza = Calendar.getInstance();
                    scadenza.setTimeInMillis(scadenzats.getTime());
                    toDo.setScadenza(scadenza);
                }
                toDo.setCompletato(rs.getBoolean("completato"));
                toDo.setScaduto(rs.getBoolean("scaduto"));
                String sfondo = rs.getString("sfondo");
                if(sfondo != null){
                    toDo.setSfondo(Color.decode(sfondo));
                }


                toDoList.add(toDo);
            }
            rs.close();
            getToDoPerUtentePS.close();

        } catch (SQLException e){
            e.printStackTrace();
        }

        return toDoList;
    }
}


