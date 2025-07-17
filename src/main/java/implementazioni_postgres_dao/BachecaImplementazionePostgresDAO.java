package implementazioni_postgres_dao;

import database.DBConnessione;
import model.Ordinamento;
import model.Titolo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Implementazione concreta dell'interfaccia {@link dao.BachecaDAO} per
 * PostgreSQL.
 * Gestisce tutte le operazioni di accesso ai dati relativi alle bacheche.
 */
public class BachecaImplementazionePostgresDAO implements dao.BachecaDAO{

    private Connection connection;

    /**
     * Costruttore che ottiene una connessione al database tramite {@link DBConnessione}.
     * Stampa lo stack in caso di errore nella connessione.
     */
    public BachecaImplementazionePostgresDAO(){
        try{
            connection = DBConnessione.getInstance().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Recupera la descrizione della bacheca di un utente.
     * @param emailUtente email dell'utente attuale
     * @param titolo titolo della bacheca
     * @return la descrizione della bacheca, oppure null se non esiste
     */
    @Override
    public String getDescrizioneBacheca(String emailUtente, Titolo titolo) {
        String sql = "SELECT \"descrizione\" FROM \"Bacheca\" WHERE \"emailUtente\" = ? AND \"titolo\" = ?::titolo";

        try (PreparedStatement descrizionePS = connection.prepareStatement(sql)) {
            descrizionePS.setString(1, emailUtente);
            descrizionePS.setString(2, titolo.name());

            try (ResultSet rs = descrizionePS.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("descrizione");
                } else {
                    return null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Recupera l'ordinamento della bacheca di un utente.
     * @param emailUtente email dell'utente attuale
     * @param titolo bacheca
     * @return l'ordinamento attuale della bacheca, oppure null se non esiste'
     */
    @Override
    public Ordinamento getOrdinamentoBacheca (String emailUtente, Titolo titolo){

            String sql = "SELECT ordinamento FROM \"Bacheca\" WHERE \"emailUtente\" = ? AND \"titolo\" = ?::titolo";
        try(PreparedStatement ordinamentoPS = connection.prepareStatement(sql)){

            ordinamentoPS.setString(1, emailUtente);
            ordinamentoPS.setString(2, titolo.name());

            ResultSet rs = ordinamentoPS.executeQuery();
            if(rs.next()){
                String ordinamento = rs.getString("ordinamento");
                return Ordinamento.valueOf(ordinamento);
            } else {
                return null;
            }
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Salva o aggiorna la descrizione della bacheca per l'utente specificato.
     * @param emailUtente email dell'utente attuale
     * @param titolo titolo della bacheca
     * @param descrizione descrizione selezionata
     */
    @Override
    public void salvaDescrizioneBacheca (String emailUtente, Titolo titolo, String descrizione){

            String sql = "UPDATE \"Bacheca\" SET \"descrizione\" = ? WHERE \"emailUtente\" = ? AND \"titolo\" = ?::titolo";

        try(PreparedStatement salvabPS = connection.prepareStatement(sql)){

            salvabPS.setString(1, descrizione);
            salvabPS.setString(2, emailUtente);
            salvabPS.setString(3, titolo.name());

            salvabPS.executeUpdate();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Salva o aggiorna l'ordinamento della bacheca per l'utente specificato.
     * @param emailUtente email dell'utente attuale
     * @param titolo titolo della bacheca
     * @param ordinamento ordinamento selezionato
     */
    @Override
    public void salvaOrdinamentoBacheca (String emailUtente, Titolo titolo, Ordinamento ordinamento){

            String sql = "UPDATE \"Bacheca\" SET ordinamento = ?::ordinamento WHERE \"emailUtente\" = ? AND \"titolo\" = ?::titolo";

        try(PreparedStatement salvaoPS = connection.prepareStatement(sql)){

            salvaoPS.setString(1, ordinamento.name());
            salvaoPS.setString(2, emailUtente);
            salvaoPS.setString(3, titolo.name());

            salvaoPS.executeUpdate();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}