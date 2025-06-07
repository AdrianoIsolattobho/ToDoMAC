package dao;

import model.Utente;

public interface UtenteDAO {

    void registraUtente(String email, String password);
    boolean loginValido(String email, String password);
}
