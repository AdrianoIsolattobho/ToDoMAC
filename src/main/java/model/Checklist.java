package model;
import java.util.ArrayList;

public class Checklist {
    private ArrayList<Attività> attività = new ArrayList<>();

    // costruttore
    public Checklist(ArrayList<Attività> attività) {
        this.attività = new ArrayList<>();
        for (Attività att : attività) {
            this.attività.add(att);
        }
    }
    // getters e setters
    public ArrayList<Attività> getAttività() {
        return attività;
    }
    public void setAttività(ArrayList<Attività> attività) {
        this.attività = attività;
    }  
}
