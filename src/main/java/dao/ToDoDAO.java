package dao;

import java.util.List;

import model.Bacheca;
import model.ToDo;

public interface ToDoDAO {

    void creaToDo(String emailUtente, ToDo toDo, Bacheca bacheca);

    Bacheca caricaBacheca(String emailUtente, String titolo);

    List<ToDo> caricaToDoPerBacheca(String emailUtente, String nomeBacheca);

    void modificaToDo(String emailUtente, ToDo toDo);

    void eliminaToDo(String emailUtente, String titolo);



}
