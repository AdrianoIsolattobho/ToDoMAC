package implementazioni_postgres_dao;

import database.DBConnessione;
import model.Utente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


/**
 * Implementazione concreta dell'interfaccia {@link dao.UtenteDAO} per PostgreSQL.
 * Gestisce tutte le operazioni di accesso ai dati relativi agli utenti.
 */
public class UtenteImplementazionePostgresDAO implements dao.UtenteDAO {

    private Connection connection;

    /**
     * Costruttore che ottiene una connessione al database tramite {@link DBConnessione}.
     */
    public UtenteImplementazionePostgresDAO() {
        try {
        connection = DBConnessione.getInstance().getConnection();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Registra un nuovo utente nel database.
     *
     * @param email   l'email dell'utente
     * @param password la password dell'utente
     */
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


    /**
     * Verifica la validità delle credenziali d'accesso.
     *
     * @param email
     * @param password
     * @return true se esiste un utente con queste credenziali, false altrimenti.
     */
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


    /**
     * Recupera un utente dal database a partire dalla sua email.
     *
     * @param email
     * @return true se esiste un utente con questa email nel database, false altrimenti.
     */
    @Override
    public Utente trovaUtenteDaMail (String email){
        try{
            PreparedStatement trovaUtentePS = connection.prepareStatement(
                    "SELECT \"email\",\"password\" FROM \"Utente\" WHERE \"email\" = ?");

            trovaUtentePS.setString(1,email);

            ResultSet rs = trovaUtentePS.executeQuery();

            if(rs.next()){
                Utente u = new Utente();
                u.setEmail(rs.getString("email"));
                return u;
            }

        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Aggiorna la password di un utente esistente nel database.
     *
     * @param email
     * @param nuovaPassword
     * @return true se l'update è andato a buon fine, false altrimenti.
     */
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

    @Override
    public ArrayList<Utente> getUtentiAll(){
        ArrayList<Utente> utenti = new ArrayList<>();
        try{
            PreparedStatement utentiPS = connection.prepareStatement(
                    "SELECT \"email\" FROM \"Utente\"" );

            ResultSet rs = utentiPS.executeQuery();
            while(rs.next()){
                Utente u = new Utente();
                u.setEmail(rs.getString("email"));
                utenti.add(u);
            }
            rs.close();
            utentiPS.close();

        } catch (Exception e){
            e.printStackTrace();
        }
        return utenti;
    }
}
