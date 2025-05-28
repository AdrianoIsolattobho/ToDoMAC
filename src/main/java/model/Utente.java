package model;
public class Utente {
    private String email;
    private String password;
    private Bacheca tempoLibero;
    private Bacheca universita;
    private Bacheca lavoro;

    

    // costruttore


    public Utente(String email, String password, Bacheca tempoLibero, Bacheca universita, Bacheca lavoro) {
        this.email = email;
        this.password = password;
        this.tempoLibero = tempoLibero;
        this.universita = universita;
        this.lavoro = lavoro;
    }

    // getters e setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Bacheca getTempoLibero() {
        return tempoLibero;
    }

    public Bacheca getUniversita() {
        return universita;
    }

    public Bacheca getLavoro() {
        return lavoro;
    }
}
