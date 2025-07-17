package dao;

import model.Utente;

import java.util.ArrayList;

/**
 * Interfaccia che definisce i metodi per l'accesso ai dati (DAO) per l'entità {@link Utente}.
 */
public interface UtenteDAO {

    /**
     * Registra un nuovo utente nel database.
     *
     * @param email   l'email dell'utente
     * @param password la password dell'utente
     */
    void registraUtente(String email, String password);

    /**
     * Verifica se le credenziali fornite appartengano a un utente registrato nel database.
     *
     * @param email email dell'utente
     * @param password password dell'utente
     * @return true se le credenziali sono valide, false altrimenti
     */
    boolean loginValido(String email, String password);

    /**
     * Cerca e restituisce un utente a partire dalla sua email.
     *
     * @param email email dell'utente che si sta cercando
     * @return l'oggetto Utente corrispondente all'email, oppure null se non trovato
     */
    Utente trovaUtenteDaMail (String email);

    /**
     * Aggiorna la password di un utente nel database.
     *
     * @param email email dell'utente da modificare
     * @param nuovaPassword nuova password da associare alla mail
     * @return true se l'aggiornamento è andato a buon fine, false altrimenti
     */
    boolean aggiornaPassword (String email, String nuovaPassword);

    /**
     * Prende tutti gli utenti dal database.
     *
     * @return una lista di tutti gli utenti
     */
    ArrayList<Utente> getUtentiAll();
}
