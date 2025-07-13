package model;

import java.util.ArrayList;
import java.util.List;

public class Bacheca {
    private Titolo titolo;
    private String descrizione;
    private Ordinamento ordinamento;
    private ArrayList<ToDo> toDoList = new ArrayList<>(); // ArrayList per gestire dinamicamente la lista di ToDo

    // costruttore
    public Bacheca(Titolo titolo, String descrizione, Ordinamento ordinamento, List<ToDo> toDoList) {
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.ordinamento = ordinamento;
        this.toDoList = new ArrayList<>();
        this.toDoList.addAll(toDoList);
    }

    public Bacheca() {
    }

    // getter e setter
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

    public void setToDoList(List<ToDo> toDoList) {
        this.toDoList = new ArrayList<>(toDoList);
    }

}
