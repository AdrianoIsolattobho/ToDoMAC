package model;

/**
 * Rappresenta un utente del sistema con credenziali e tre bacheche associate:
 * Università, Tempo Libero e Lavoro
 */
public class Utente {
    private String email;
    private String password;
    private Bacheca tempoLibero;
    private Bacheca universita;
    private Bacheca lavoro;

    /**
     * Costruttore completo per creare un utente con bacheche già assegnate.
     *
     * @param email email identificativa di un Utente
     * @param password password relativa alla mail dell'Utente
     * @param tempoLibero bacheca Tempo Libero
     * @param universita bacheca Università
     * @param lavoro bacheca Lavoro
     */
    public Utente(String email, String password, Bacheca tempoLibero, Bacheca universita, Bacheca lavoro) {
        this.email = email;
        this.password = password;
        this.tempoLibero = tempoLibero;
        this.universita = universita;
        this.lavoro = lavoro;
    }

    /**
     * Costruttore vuoto utilizzato per il recupero password.
     */
    public Utente(){}

    /* ------------ Getter e Setter per accedere ai componenti dall'esterno ------------ */
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
