package implementazioni_postgres_dao;

import database.DBConnessione;
import model.Attivita;
import model.Checklist;
import model.ToDo;

import java.awt.*;
import java.io.File;
import java.net.URI;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;

public class CondivisioneImplementazionePostgresDAO implements dao.CondivisioneDAO {

    private Connection connection;

    public CondivisioneImplementazionePostgresDAO(){
        try{
            connection = DBConnessione.getInstance().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void aggiungiCondivisione(String emailAutore, String titoloToDo, String emailUtenteCondiviso) {
        try{
            PreparedStatement aggiungiPS = connection.prepareStatement(
                    "INSERT INTO \"Condivisione\" (\"emailUtente\", \"titoloToDo\", \"emailAutore\") " +
                            "VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

            aggiungiPS.setString(1, emailUtenteCondiviso);
            aggiungiPS.setString(2, titoloToDo);
            aggiungiPS.setString(3, emailAutore);

            aggiungiPS.executeUpdate();

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void eliminaCondivisione(String emailAutore, String titoloToDo, String emailUtenteCondiviso){
        try{
            PreparedStatement eliminaPS = connection.prepareStatement(
                    "DELETE FROM \"Condivisione\" WHERE \"emailUtente\" = ? AND \"titoloToDo\" = ? AND \"emailAutore\" = ?;");

            eliminaPS.setString(1, emailUtenteCondiviso);
            eliminaPS.setString(2, titoloToDo);
            eliminaPS.setString(3, emailAutore);

            eliminaPS.executeUpdate();
            eliminaPS.close();

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<String> getUtentiCondivisiPerToDo (String emailAutore, String titoloToDo){
        ArrayList<String> utenti = new ArrayList<>();
        try{
            PreparedStatement utentiPS = connection.prepareStatement(
                    "SELECT \"emailUtente\" FROM \"Condivisione\" WHERE \"emailAutore\" = ? AND \"titoloToDo\" = ?");

            utentiPS.setString(1, emailAutore);
            utentiPS.setString(2, titoloToDo);

            ResultSet rs = utentiPS.executeQuery();

            while (rs.next()){
                utenti.add(rs.getString("emailUtente"));
            }
            rs.close();
            utentiPS.close();

        } catch (Exception e){
            e.printStackTrace();
        }
        return utenti;
    }

    @Override
    public ArrayList<ToDo> getToDoCondivisiPerUtente(String emailAutore){
        ArrayList<ToDo> todos = new ArrayList<>();
        try{
            PreparedStatement todoPS = connection.prepareStatement(
                    "SELECT T.* FROM \"ToDo\"T JOIN \"Condivisione\" C ON T.titolo = C.\"titoloToDo\" AND T.\"emailUtente\" = C.\"emailAutore\"" +
                            " WHERE C.\"emailAutore\" = ?" );

            todoPS.setString(1, emailAutore);

            ResultSet rs = todoPS.executeQuery();
            while (rs.next()){
                ToDo t = new ToDo();
                String titolo = rs.getString("titolo");
                String emailUtente = rs.getString("emailUtente");

                t.setTitolo(titolo);
                t.setDescrizione(rs.getString("descrizione"));

                String link = rs.getString("link");
                if(link != null && !link.isEmpty()){
                    t.setLink(new URI(link).toURL().toURI());
                }

                Timestamp ts = rs.getTimestamp("scadenza");
                if(ts != null){
                    Calendar scadenza = Calendar.getInstance();
                    scadenza.setTimeInMillis(ts.getTime());
                    t.setScadenza(scadenza);
                }

                t.setCompletato(rs.getBoolean("completato"));
                t.setScaduto(rs.getBoolean("scaduto"));

                String sfondohex = rs.getString("sfondo");
                if(sfondohex != null && sfondohex.startsWith("#")){
                    t.setSfondo(Color.decode(sfondohex));
                }

                String img = rs.getString("immagine");
                if (img != null && !img.isEmpty()) {
                    File fileImg = new File("immagini_todo/"+ img);
                }

                PreparedStatement attivitaPS = connection.prepareStatement(
                        "SELECT * FROM \"Attivita\" WHERE \"emailUtente\" = ? AND \"titoloTodo\" = ?" );

                attivitaPS.setString(1, emailAutore);
                attivitaPS.setString(2, titolo);

                ResultSet ars = attivitaPS.executeQuery();
                Checklist checklist = new Checklist();

                while (ars.next()){
                    String nome = ars.getString("nome");
                    checklist.getAttivita().add(new Attivita(nome));
                }

                t.setChecklist(checklist);

                ars.close();
                attivitaPS.close();

                todos.add(t);
            }
            rs.close();
            todoPS.close();
        } catch (Exception e){
            e.printStackTrace();
        }
        return todos;
    }

}
