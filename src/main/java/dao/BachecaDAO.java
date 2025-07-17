package dao;

import model.Ordinamento;
import model.Titolo;

/**
 * Interfaccia per l'accesso ai dati relativi alle bacheche associate a un utente.
 * Le bacheche sono identificate da un titolo e associate a un utente tramite l'email.
 * L'interfaccia consente di ottenere o salvare informazioni come descrizione e ordinamento.
 */

public interface BachecaDAO {

    /**
     * Restituisce la descrizione associata a una specifica bacheca di un utente.
     *
     * @param emailUtente email dell'utente attuale
     * @param titolo titolo della bacheca
     * @return la descrizione della bacheca, oppure null se non esiste
     */
    String getDescrizioneBacheca(String emailUtente, Titolo titolo);

    /**
     * Restituisce l'ordinamento applicato a una bacheca dell'utente.
     *
     * @param emailUtente email dell'utente attuale
     * @param titolo bacheca
     * @return l'ordinamento attuale della bacheca
     */
    Ordinamento getOrdinamentoBacheca(String emailUtente, Titolo titolo);

    /**
     * Salva o aggiorna l'ordinamento della bacheca per l'utente specificato
     *
     * @param emailUtente email dell'utente attuale
     * @param titolo titolo della bacheca
     * @param ordinamento ordinamento selezionato
     */
    void salvaOrdinamentoBacheca (String emailUtente, Titolo titolo, Ordinamento ordinamento);

    /**
     * Salva o aggiorna la descrizione della bacheca per l'utente specificato
     *
     * @param emailUtente email dell'utente attuale
     * @param titolo titolo della bacheca
     * @param descrizione descrizione selezionata
     */
    void salvaDescrizioneBacheca (String emailUtente, Titolo titolo, String descrizione);
}
