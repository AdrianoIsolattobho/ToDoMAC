package dao;

import model.Utente;

public interface UtenteDAO {

    void registraUtente(String email, String password);
    boolean loginValido(String email, String password);
    boolean trovaUtenteDaMail (String email);
    boolean aggiornaPassword (String email, String nuovaPassword);
}
