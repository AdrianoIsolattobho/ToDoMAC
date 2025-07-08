package implementazioniPostgresDAO;

import database.DBConnessione;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class UtenteImplementazionePostgresDAO implements dao.UtenteDAO {

    private Connection connection;

    public UtenteImplementazionePostgresDAO() {
        try {
        connection = DBConnessione.getInstance().getConnection();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void registraUtente(String email, String password) {
        try {
            PreparedStatement registratiPS = connection.prepareStatement(
                    "INSERT INTO \"Utente\" " +
                    "(\"email\", \"password\")" +
                   "VALUES (?,?);");

            registratiPS.setString(1, email);
            registratiPS.setString(2, password);
            registratiPS.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean loginValido(String email, String password){
        try {
            PreparedStatement loginValidoPS = connection.prepareStatement(
                    "SELECT * FROM \"Utente\" WHERE \"email\" = ? AND \"password\" = ?;");

            loginValidoPS.setString(1, email);
            loginValidoPS.setString(2, password);

            return loginValidoPS.executeQuery().next();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean trovaUtenteDaMail (String email){
        try{
            PreparedStatement trovaUtentePS = connection.prepareStatement(
                    "SELECT * FROM \"Utente\" WHERE \"email\" = ?");

            trovaUtentePS.setString(1,email);

            ResultSet rs = trovaUtentePS.executeQuery();
            return rs.next();

        } catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }


    @Override
    public boolean aggiornaPassword(String email, String nuovaPassword) {
        try{
            PreparedStatement aggiornaPasswordPS = connection.prepareStatement(
                    "UPDATE \"Utente\" SET \"password\" = ? WHERE \"email\" = ?");

            aggiornaPasswordPS.setString(1, nuovaPassword);
            aggiornaPasswordPS.setString(2, email);

            return aggiornaPasswordPS.executeUpdate() > 0;

        } catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }
}
