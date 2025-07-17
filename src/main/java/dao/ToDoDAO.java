package dao;

import java.util.List;

import model.*;

public interface ToDoDAO {

    void creaToDo(String emailUtente, ToDo toDo, Bacheca bacheca);

    Bacheca caricaBacheca(String emailUtente, Titolo titolo);

    List<ToDo> caricaToDoPerBacheca(String emailUtente, String nomeBacheca);

    void modificaToDo(String emailUtente, ToDo toDo, Bacheca bacheca, String oldTitolo);

    Checklist CaricaAttivitaPerToDo(int idToDo);

    Boolean completaToDo(String emailUtente, String titolo, boolean isCompletato);

    Boolean completaAtt(String emailUtente, String titolo, boolean isCompletato, String nome);

    void eliminaToDo(String emailUtente, String titolo);

}
