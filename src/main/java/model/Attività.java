package model;
public class Attività {
    private String nome;
    private boolean completata = false;


    //costruttore
    public Attività(String nome, boolean completata) {
        this.nome = nome;
        this.completata = completata;
    }



    //getters e setters
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
