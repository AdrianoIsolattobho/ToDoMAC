package model;
import java.util.ArrayList;

public class Checklist {
    private String nomeChecklist;
    private Boolean completata = false;
    private ArrayList<Attività> attività = new ArrayList<>();

    // costruttore
    public Checklist(String nome,ArrayList<Attività> attività) {
        this.nomeChecklist = nome;
        this.attività = new ArrayList<>();
        this.attività.addAll(attività);
    }


    public String getNomeChecklist() {
        return nomeChecklist;
    }

    // getters e setters
    public ArrayList<Attività> getAttività() {
        return attività;
    }
    public void setAttività(ArrayList<Attività> attività) {
        this.attività = attività;
    }  
}
