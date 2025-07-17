package model;
import java.util.List;

/**
 * La classe Condivisione rappresenta la condivisione di un oggetto ToDo
 * da parte di un utente (creatore) con altri utenti.
 */
public class Condivisione {
    private String creatore;
    private List<Utente> condivisiCon;
    private ToDo toDoCondiviso;

    /**
     * Costruttore principale.
     * @param creatore Utente che ha creato la condivisione
     * @param toDoCondiviso Oggetto ToDo condiviso
     * @param condivisiCon Lista di utenti destinatari della condivisione
     */
    public Condivisione(String creatore, ToDo toDoCondiviso, List<Utente> condivisiCon) {
        this.creatore = creatore;
        this.toDoCondiviso = toDoCondiviso;
        this.condivisiCon = condivisiCon;
    }

    /* ------------ Getter e Setter per accedere ai componenti dall'esterno ------------ */

    public String getCreatore() {
        return creatore;
    }

    public List<Utente> getCondivisiCon() {
        return condivisiCon;
    }

    public ToDo getToDoCondiviso() {
        return toDoCondiviso;
    }


}
