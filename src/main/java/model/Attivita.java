package model;
/*
 * Classe Attività
 * Attività che compone il CheckList
 */
public class Attivita {
    private String nome;
    private boolean completata;

    public void setCompletata(boolean completata) {
        this.completata = completata;
    }

    public Attivita(String nome) {
        this.nome = nome;
        this.completata = false;
    }

    public String getNome() {
        return nome;
    }
    public boolean isCompletata() {
        return completata;
    }
}
