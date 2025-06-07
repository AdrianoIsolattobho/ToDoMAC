package implementazioniPostgresDAO;

import database.DBConnessione;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class UtenteImplementazionePostgresDAO implements dao.UtenteDAO {

    private Connection connection;

    public UtenteImplementazionePostgresDAO() {
        try {
        connection = DBConnessione.getInstance().connection;
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

}
