package dao;

import java.util.List;
import model.ToDo;

public interface ToDoDAO {

    void creaToDo(String emailUtente, ToDo toDo);
    void modificaToDo(String emailUtente, ToDo toDo);
    void eliminaToDo(String emailUtente, String titolo);
    List<ToDo> getToDoPerUtente(String emailUtente);

}
