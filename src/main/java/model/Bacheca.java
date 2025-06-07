package model;
import java.util.ArrayList;

public class Bacheca {
    private Titolo titolo;
    private String descrizione;
    private Ordinamento ordinamento;
    private ArrayList<ToDo> toDoList = new ArrayList<>(); // ArrayList per gestire dinamicamente la lista di ToDo

    //costruttore
    public Bacheca(Titolo titolo, String descrizione, Ordinamento ordinamento, ArrayList<ToDo> toDoList) {
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.ordinamento = ordinamento;
        this.toDoList = new ArrayList<>();
        for (ToDo toDo : toDoList) {
            this.toDoList.add(toDo);
        }
    }

    public Bacheca(){}



    //getter e setter
    public Titolo getTitolo() {
        return titolo;
    }
    public void setTitolo(Titolo titolo) {
        this.titolo = titolo;
    }


    public String getDescrizione() {
        return descrizione;
    }
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }


    public Ordinamento getOrdinamento() {
        return ordinamento;
    }
    public void setOrdinamento(Ordinamento ordinamento) {
        this.ordinamento = ordinamento;
    }


    public ArrayList<ToDo> getToDoList() {
        return toDoList;
    }
    public void setToDoList(ArrayList<ToDo> toDoList) {
        this.toDoList = toDoList;
    }
    
}
