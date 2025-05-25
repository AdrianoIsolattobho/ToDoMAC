package model;
/*
 * Classe Attività
 * Attività che compone il CheckList
 */
public class Attività {
    private String nome;
    private boolean completata = false;

    public Attività(String nome, boolean completata) {
        this.nome = nome;
        this.completata = completata;
    }



    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public boolean isCompletata() {
        return completata;
    }
    public void setCompletata(boolean completata) {
        this.completata = completata;
    }

    
}
