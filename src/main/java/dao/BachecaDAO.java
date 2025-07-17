package dao;

import model.Ordinamento;
import model.Titolo;

public interface BachecaDAO {
    String getDescrizioneBacheca(String emailUtente, Titolo titolo);
    Ordinamento getOrdinamentoBacheca(String emailUtente, Titolo titolo);
    void salvaOrdinamentoBacheca (String emailUtente, Titolo titolo, Ordinamento ordinamento);
    void salvaDescrizioneBacheca (String emailUtente, Titolo titolo, String descrizione);
}
