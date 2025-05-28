package model;
import java.util.ArrayList;

public class Checklist {
    private String nomeChecklist;
    private Boolean completata;
    private ArrayList<Attivita> attivita;

    public Boolean getCompletata() {
        return completata;
    }

    // costruttore
    public Checklist(String nome,ArrayList<Attivita> attivita) {
        this.nomeChecklist = nome;
        this.attivita = new ArrayList<>();
        this.attivita.addAll(attivita);
        this.completata = false;
    }

    public void setCompletata(Boolean completata) {
        this.completata = completata;
    }

    public String getNomeChecklist() {
        return nomeChecklist;
    }

    // getters e setters
    public ArrayList<Attivita> getAttivita() {
        return attivita;
    }

}
