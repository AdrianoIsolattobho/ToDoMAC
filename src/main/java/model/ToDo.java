package model;
import java.util.Calendar;
public class ToDo {
    private String titolo;
    private String descrizione;
    private String link;
    private Calendar scadenza;
    private Calendar creazione;
    private boolean completato = false;
    private boolean manuale = false;
    private boolean scaduto = false;
    private java.awt.Color sfondo;
    private String immagine;
    private Checklist checklist;


    // costruttore completo
    public ToDo(String titolo, String descrizione, String link, Calendar scadenza, boolean completato,
            boolean manuale, boolean scaduto, java.awt.Color sfondo, String immagine,
            Checklist checklist) {
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.link = link;
        this.scadenza = scadenza;
        this.creazione = Calendar.getInstance(); // data di creazione impostata alla data corrente
        this.completato = completato;
        this.manuale = manuale;
        this.scaduto = scaduto;
        this.sfondo = sfondo;
        this.immagine = immagine;
        this.checklist = checklist;
    }

    // costruttore senza elementi null
    public ToDo(String titolo, String descrizione, Calendar scadenza, boolean completato,
            boolean manuale, boolean scaduto) {
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.scadenza = scadenza;
        this.creazione = Calendar.getInstance(); // data di creazione impostata alla data corrente
        this.completato = completato;
        this.manuale = manuale;
        this.scaduto = scaduto;
    }

    // getters e setters
    public String getTitolo() {
        return titolo;
    }


    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }


    public String getDescrizione() {
        return descrizione;
    }


    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }


    public String getLink() {
        return link;
    }


    public void setLink(String link) {
        this.link = link;
    }


    public Calendar getScadenza() {
        return scadenza;
    }


    public void setScadenza(Calendar scadenza) {
        this.scadenza = scadenza;
    }


    public Calendar getCreazione() {
        return creazione;
    }


    public void setCreazione(Calendar creazione) {
        this.creazione = creazione;
    }


    public boolean isCompletato() {
        return completato;
    }


    public void setCompletato(boolean completato) {
        this.completato = completato;
    }


    public boolean isManuale() {
        return manuale;
    }


    public void setManuale(boolean manuale) {
        this.manuale = manuale;
    }


    public boolean isScaduto() {
        return scaduto;
    }


    public void setScaduto(boolean scaduto) {
        this.scaduto = scaduto;
    }

    public java.awt.Color getSfondo() {
        return sfondo;
    }


    public void setSfondo(java.awt.Color sfondo) {
        this.sfondo = sfondo;
    }


    public String getImmagine() {
        return immagine;
    }


    public void setImmagine(String immagine) {
        this.immagine = immagine;
    }


    public Checklist getChecklist() {
        return checklist;
    }


    public void setChecklist(Checklist checklist) {
        this.checklist = checklist;
    }
    
    
}

