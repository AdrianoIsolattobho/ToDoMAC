package model;

import java.awt.*;
import java.util.Calendar;
import java.net.URL;
import java.net.URI;

/**
 * Rappresenta il promemoria (ToDo) all'interno di una bacheca.
 * Include informazioni quali titolo, descrizione, link, scadenza, stato e checklist associata.
 */
public class ToDo {
    private String titolo;
    private String descrizione;
    private URI link;
    private Calendar scadenza;
    private Calendar creazione;
    private boolean completato;
    private boolean scaduto;
    private java.awt.Color sfondo;
    private URL immagine;
    private Checklist checklist;
    private String emailUtente;

    /**
     * Costruttore principale.
     * @param titolo titolo del ToDo (obbligatorio)
     * @param descrizione descrizione del ToDo
     * @param link link associabile al ToDo
     * @param scadenza data di scadenza di un ToDo
     * @param sfondo colore di sfondo del ToDo
     * @param immagine file immagine associabile al ToDo
     * @param checklist Checklist di sub-attività
     */
    public ToDo(String titolo, String descrizione, URI link, Calendar scadenza, java.awt.Color sfondo, URL immagine,
            Checklist checklist) {
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.link = link;
        this.scadenza = scadenza;
        this.creazione = Calendar.getInstance(); // data di creazione impostata alla data corrente
        this.completato = false;
        this.scaduto = false;
        this.sfondo = sfondo;
        this.immagine = immagine;
        this.checklist = checklist;
    }

    /**
     * Costruttore vuoto.
     */
    public ToDo(){}

    /* ------------ Getter e Setter per accedere ai componenti dall'esterno ------------ */

    public String getEmailUtente() {
        return emailUtente;
    }

    public void setEmailUtente(String emailUtente) {
        this.emailUtente = emailUtente;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public void setLink(URI link) {
        this.link = link;
    }

    public void setScadenza(Calendar scadenza) {
        this.scadenza = scadenza;
    }

    public void setSfondo(Color sfondo) {
        this.sfondo = sfondo;
    }

    public void setImmagine(URL immagine) {
        this.immagine = immagine;
    }

    public void setChecklist(Checklist checklist) {
        this.checklist = checklist;
    }

    public void setCompletato(boolean completato) {
        this.completato = completato;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public URI getLink() {
        return link;
    }

    public Calendar getScadenza() {
        return scadenza;
    }

    public boolean isScaduto() {
        return scaduto;
    }

    public void setScaduto(boolean scaduto) {
        this.scaduto = scaduto;
    }

    public Calendar getCreazione() {
        return creazione;
    }

    public boolean isCompletato() {
        return completato;
    }

    public java.awt.Color getSfondo() {
        return sfondo;
    }

    public URL getImmagine() {
        return immagine;
    }

    public Checklist getChecklist() {
        return checklist;
    }

}
