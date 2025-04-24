package model;
public class Utente {
    private String email;
    private String password;
    private Bacheca bacheche[] = new Bacheca[3]; // array di bacheche
    

    // costruttore

    public Utente(String email, String password, Bacheca[] bacheche) {
        this.email = email;
        this.password = password;
        this.bacheche = bacheche;

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

    public Bacheca[] getBacheche() {
        return bacheche;
    }
    public void setBacheche(Bacheca[] bacheche) {
        this.bacheche = bacheche;
    }
}
