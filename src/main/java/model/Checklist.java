package model;
import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta una Checklist composta da più attività {@link Attivita},
 * con uno stato di completamento e un nome identificativo.
 */
public class Checklist {
    private String nomeChecklist;
    private Boolean completata;
    private List<Attivita> attivita;

    /**
     * Costruttore principale.
     * Inizializza una checklist con nome e lista di attività.
     * La checklist parte non completata di default.
     *
     * @param nome Nome della checklist.
     * @param attivita Lista di attività associate.
     */
    public Checklist(String nome, List<Attivita> attivita) {
        this.nomeChecklist = nome;
        this.attivita = new ArrayList<>();
        this.attivita.addAll(attivita);
        this.completata = false;
    }

    /**
     * Costruttore vuoto.
     */
    public Checklist(){}

    /* ------------ Getter e Setter per accedere ai componenti dall'esterno ------------ */

    public Boolean getCompletata() {
        return completata;
    }

    public void setNomeChecklist(String nomeChecklist) {
        this.nomeChecklist = nomeChecklist;
    }

    public void setAttivita(List<Attivita> attivita) {
        this.attivita = attivita;
    }

    public void setCompletata(Boolean completata) {
        this.completata = completata;
    }

    public String getNomeChecklist() {
        return nomeChecklist;
    }

    public List<Attivita> getAttivita() {
    if (this.attivita == null) {
        this.attivita = new ArrayList<>();  // restituisci sempre almeno una lista vuota
    }
    return this.attivita;
}

}
