package model;
import java.util.ArrayList;

public class Condivisione {
    private Utente creatore;
    private ArrayList<Utente> condivisiCon=new ArrayList<>();
    private ToDo toDoCondiviso;

    public Condivisione(Utente creatore, ToDo toDoCondiviso, Utente condivisiCon) {
        this.creatore = creatore;
        this.toDoCondiviso = toDoCondiviso;
        this.condivisiCon.add(condivisiCon);
    }

    public Utente getCreatore() {
        return creatore;
    }

    public void setCreatore(Utente creatore) {
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
