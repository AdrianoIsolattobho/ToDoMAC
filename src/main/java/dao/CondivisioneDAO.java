package dao;

import model.ToDo;

import java.util.ArrayList;

public interface CondivisioneDAO {

    void aggiungiCondivisione(String emailAutore, String titoloToDo, String emailUtenteCondiviso);
    void eliminaCondivisione(String emailAutore, String titoloToDo, String emailUtenteCondiviso);
    ArrayList<String> getUtentiCondivisiPerToDo(String emailAutore, String titoloToDo);
    ArrayList<ToDo> getToDoCondivisiPerUtente(String emailUtente);
}
