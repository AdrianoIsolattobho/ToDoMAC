package model;

import java.util.ArrayList;
import java.util.List;

/**
 * La classe Bacheca rappresenta una collezione di ToDo,
 * associata ad un titolo, una descrizione e un criterio di ordinamento.
 */
public class Bacheca {
    private Titolo titolo;
    private String descrizione;
    private Ordinamento ordinamento;
    private List<ToDo> toDoList; // List per gestire dinamicamente la lista di ToDo

    /**
     * Costruttore completo
     * @param titolo Titolo della bacheca
     * @param descrizione Descrizione della bacheca
     * @param ordinamento Tipo di ordinamento applicato alla lista di ToDo
     * @param toDoList Lista di ToDo di una bacheca
     */
    public Bacheca(Titolo titolo, String descrizione, Ordinamento ordinamento, List<ToDo> toDoList) {
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.ordinamento = ordinamento;
        this.toDoList = new ArrayList<>();
        this.toDoList.addAll(toDoList);
    }

    /**
     * Costruttore vuoto.
     */
    public Bacheca() {
    }

    /* ------------ Getter e Setter per accedere ai componenti dall'esterno ------------ */
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

    public List<ToDo> getToDoList() {
        return toDoList;
    }

    public void setToDoList(List<ToDo> toDoList) {
        this.toDoList = new ArrayList<>(toDoList);
    }

}
