package dao;

import model.Condivisione;
import model.ToDo;

import java.util.ArrayList;

public interface CondivisioneDAO {

    void aggiungiCondivisione(String emailAutore, String titoloToDo, String emailUtenteCondiviso);
    void eliminaCondivisione(String emailAutore, String titoloToDo, String emailUtenteCondiviso);
    ArrayList<String> getUtentiCondivisiPerToDo(String emailAutore, String titoloToDo);
    ArrayList<ToDo> getToDoCondivisiPerUtente(String emailUtente);
    ArrayList<Condivisione> getToDoPerUtenteCondiviso(String emailUtente);
    // Nell'interfaccia CondivisioneDAO
boolean rimuoviCondivisione(String creatore, String titoloToDo, String utenteCondiviso);
}
