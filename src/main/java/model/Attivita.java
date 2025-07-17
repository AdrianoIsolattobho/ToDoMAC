package model;

/**
 * Rappresenta una singola attività della checklist.
 * Ogni attività ha un nome e un booleano che indica se è stata completata o meno.
 */
public class Attivita {
    private String nome;
    private boolean completata;


    /**
     * Costruttore della classe Attivita.
     * @param nome Nome dell'attività
     * @param completata Stato iniziale dell'attività (true se è completata)
     */
    public Attivita(String nome,Boolean completata) {
        this.nome = nome;
        this.completata = completata;
    }

    public void setCompletata(boolean completata) {
        this.completata = completata;
    }

    public String getNome() {
        return nome;
    }

    public boolean isCompletata() {
        return completata;
    }
}
