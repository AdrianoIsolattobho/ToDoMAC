package dao;

import java.util.List;

import model.*;

/**
 * Interfaccia per la gestione dei ToDo e delle relative bacheche del sistema.
 * Fornisce operazioni per i ToDo, Attività (Checklist) e bacheche utente.
 */
public interface ToDoDAO {

    /**
     * Crea un nuovo ToDo all'interno della bacheca di un utente.
     *
     * @param emailUtente email utente attuale
     * @param toDo todo attuale
     * @param bacheca bacheca attuale
     */
    void creaToDo(String emailUtente, ToDo toDo, Bacheca bacheca);

    /**
     * Carica una bacheca specifica per un utente.
     *
     * @param emailUtente email utente attuale
     * @param titolo titolo bacheca attuale
     * @return la bacheca corrispondente se trovata
     */
    Bacheca caricaBacheca(String emailUtente, Titolo titolo);

    /**
     * Carica tutti i ToDo associati a una determinata bacheca dell'utente.
     *
     * @param emailUtente email utente attuale
     * @param nomeBacheca titolo bacheca attuale
     * @return una lista di ToDo contenuti nella bacheca
     */
    List<ToDo> caricaToDoPerBacheca(String emailUtente, String nomeBacheca);

    /**
     * Modifica un ToDo esistente, eventualmente cambiando anche il titolo.
     *
     * @param emailUtente email utente attuale
     * @param toDo todo da modificare
     * @param bacheca bacheca attuale
     * @param oldTitolo il titolo precedente del ToDO (prima della modifica)
     */
    void modificaToDo(String emailUtente, ToDo toDo, Bacheca bacheca, String oldTitolo);

    /**
     * Carica la checklist (con le attività) associata a un ToDo specifico.
     *
     * @param idToDo id del todo da cui viene presa la checklist
     * @return la checklist associata al ToDo
     */
    Checklist CaricaAttivitaPerToDo(int idToDo);

    /**
     * Imposta lo stato di completamento di un ToDo.
     *
     * @param emailUtente email utente attuale
     * @param titolo titolo del todo da modificare
     * @param isCompletato stato del todo attuale
     * @return true se l'operazione ha avuto successo, false altrimenti
     */
    Boolean completaToDo(String emailUtente, String titolo, boolean isCompletato);

    /**
     * Imposta lo stato di completamento di una singola attività (elemento della checklist).
     *
     * @param emailUtente email utente attuale
     * @param titolo titolo del todo attuale
     * @param isCompletato stato del todo
     * @param nome nome dell'attività
     * @return true se l'operazione ha avuto successo, false altrimenti
     */
    Boolean completaAtt(String emailUtente, String titolo, boolean isCompletato, String nome);

    /**
     * Elimina un ToDo specificato per un utente.
     *
     * @param emailUtente email utente attuale
     * @param titolo titolo del todo da eliminare
     */
    void eliminaToDo(String emailUtente, String titolo);

}
