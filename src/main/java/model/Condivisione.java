package model;
import java.util.ArrayList;

public class Condivisione {
    private String creatore;
    private ArrayList<Utente> condivisiCon;
    private ToDo toDoCondiviso;

    public Condivisione(String creatore, ToDo toDoCondiviso, ArrayList<Utente> condivisiCon) {
        this.creatore = creatore;
        this.toDoCondiviso = toDoCondiviso;
        this.condivisiCon = condivisiCon;
    }

    public String getCreatore() {
        return creatore;
    }

    public void setCreatore(String creatore) {
        this.creatore = creatore;
    }

    public ArrayList<Utente> getCondivisiCon() {
        return condivisiCon;
    }

    public void setCondivisiCon(ArrayList<Utente> condivisiCon) {
        this.condivisiCon = condivisiCon;
    }

    public ToDo getToDoCondiviso() {
        return toDoCondiviso;
    }

    public void setToDoCondiviso(ToDo toDoCondiviso) {
        this.toDoCondiviso = toDoCondiviso;
    }

    
}
