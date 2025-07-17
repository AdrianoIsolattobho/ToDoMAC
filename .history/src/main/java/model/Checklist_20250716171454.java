package model;
import java.util.ArrayList;
import java.util.List;

public class Checklist {
    private String nomeChecklist;
    private Boolean completata;
    private List<Attivita> attivita;

    public Boolean getCompletata() {
        return completata;
    }

    public void setNomeChecklist(String nomeChecklist) {
        this.nomeChecklist = nomeChecklist;
    }

    public void setAttivita(List<Attivita> attivita) {
        this.attivita = attivita;
    }

    // costruttore
    public Checklist(String nome, List<Attivita> attivita) {
        this.nomeChecklist = nome;
        this.attivita = new ArrayList<>();
        this.attivita.addAll(attivita);
        this.completata = false;
    }

    public Checklist(){}

    public void setCompletata(Boolean completata) {
        this.completata = completata;
    }

    public String getNomeChecklist() {
        return nomeChecklist;
    }

    // getters e setters
    public List<Attivita> getAttivita() {
    if (this.attivita == null) {
        this.attivita = new ArrayList<>();  // restituisci sempre almeno una lista vuota
    }
    return this.attivita;
}

}
