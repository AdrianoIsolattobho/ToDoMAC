package dao;

import model.Condivisione;
import model.ToDo;

import java.util.ArrayList;


/**
 * Interfaccia per la gestione della condivisione dei ToDo tra utenti.
 * Consente di aggiungere, rimuovere e recuperare informazioni relative
 * ai ToDo condivisi tra utenti.
 */
public interface CondivisioneDAO {

    /**
     * Aggiunge una condivisione del ToDo di un utente autore con un altro utente.
     *
     * @param emailAutore email dell'utente creatore della condivisione
     * @param titoloToDo titolo del todo condiviso
     * @param emailUtenteCondiviso email dell'utente con cui è stato condiviso il todo
     */
    void aggiungiCondivisione(String emailAutore, String titoloToDo, String emailUtenteCondiviso);

    /**
     * Elimina una condivisione esistente tra l'autore del ToDo e l'utente con cui è stato condiviso.
     *
     * @param emailAutore email dell'utente creatore della condivisione
     * @param titoloToDo titolo del todo condiviso
     * @param emailUtenteCondiviso email dell'utente con cui è stato condiviso il todo
     * @return true se la condivisione è stata rimossa con successo, false altrimenti
     */
    Boolean eliminaCondivisione(String emailAutore, String titoloToDo, String emailUtenteCondiviso);

    /**
     * Restituisce la lista di utenti con cui un determinato ToDo è stato condiviso.
     *
     * @param emailAutore email dell'utente creatore della condivisione
     * @param titoloToDo titolo del todo condiviso
     * @return una lista di email degli utenti con cui il ToDo è condiviso
     */
    ArrayList<String> getUtentiCondivisiPerToDo(String emailAutore, String titoloToDo);

    /**
     * Restituisce tutti i ToDo condivisi con un determinato utente.
     *
     * @param emailUtente email dell'utente con cui sono stati condivisi dei todo specifici
     * @return una lista di oggetti ToDo condivisi con l'utente
     */
    ArrayList<ToDo> getToDoCondivisiPerUtente(String emailUtente);

    /**
     * Restituisce le informazioni di condivisione (inclusi autore e titolo del todo) dei ToDo condivisi con l'utente.
     * Utile per accedere ai dati aggiuntivi sulla condivisione.
     *
     * @param emailUtente email utente con cui sono stati condivisi i todo
     * @return una lista di oggetti Condivisione contententi i dettagli della condivisione.
     */
    ArrayList<Condivisione> getToDoPerUtenteCondiviso(String emailUtente);
}
