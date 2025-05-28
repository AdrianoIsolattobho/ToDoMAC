package controller;

import model.*;

import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

public class DatiEsempio {
    public static HashMap<String, Utente> inizializzaDatiEsempio() {
        HashMap<String, Utente> utentiRegistrati;
        ArrayList<ToDo> todos = new ArrayList<>();
        // Creazione attività per la checklist della spesa
        Attivita a1 = new Attivita("comprare il latte");
        Attivita a2 = new Attivita("comprare il pane");
        Attivita a3 = new Attivita("comprare le uova");

        // Creazione checklist
        ArrayList<Attivita> serieAttivita = new ArrayList<>();
        serieAttivita.add(a1);
        serieAttivita.add(a2);
        serieAttivita.add(a3);
        Checklist c1 = new Checklist("Compra", serieAttivita);

        // Creazione data di scadenza
        Calendar scadenza = Calendar.getInstance();
        scadenza.set(2025, Calendar.OCTOBER, 15);

        //Creazione data di scadenza passata
        Calendar scadenzaPassata = Calendar.getInstance();
        scadenzaPassata.set(2015, Calendar.OCTOBER, 15);

        // Creazione ToDo per la bacheca Tempo Libero
        ToDo t1;
        try {
            t1 = new ToDo("Lista della spesa", "Comprare tutto al supermercato",
                    new URI("https", "www.google.it", null, null), scadenza,  new Color(255, 102, 102), null, c1);
        } catch (URISyntaxException e) {
            // Gestione dell'eccezione
            t1 = new ToDo("Lista della spesa", "Comprare tutto al supermercato", null,scadenza,null,null,null);
        }
        ToDo t2 = new ToDo("Preparare la cena", "Cucinare le polpette", null,scadenzaPassata, null,null,null);

        // Creazione ToDo per la bacheca Universita
        ToDo t3 = new ToDo("Studiare Java", "Ripassare le basi di programmazione", null,scadenza, null,null,null);
        ToDo t4 = new ToDo("Preparare esame", "Completare gli esercizi", null,scadenza, null,null,null);

        // Creazione ToDo per la bacheca Lavoro
        ToDo t5 = new ToDo("Meeting settimanale", "Preparare la presentazione", null, scadenza, new Color(13, 200, 3), DatiEsempio.class.getResource
                ("/img/keynote.png"), null);
        ToDo t6 = new ToDo("Email clienti", "Rispondere alle email urgenti", null,scadenza, null,null,null);

        // Aggiungi tutti i ToDo alla lista generale
        todos.addAll(Arrays.asList(t1, t2, t3, t4, t5, t6));

        // Creazione bacheca Tempo Libero
        ArrayList<ToDo> todoTempoLibero = new ArrayList<>();
        todoTempoLibero.add(t1);
        todoTempoLibero.add(t2);
        Bacheca bTempoLibero = new Bacheca(Titolo.TempoLibero, "Bacheca personale", Ordinamento.AZ, todoTempoLibero);

        // Creazione bacheca Università
        ArrayList<ToDo> todoUniversita = new ArrayList<>();
        todoUniversita.add(t3);
        todoUniversita.add(t4);
        Bacheca bUniversita = new Bacheca(Titolo.Universita, "Bacheca università", Ordinamento.SCADENZA_ASC, todoUniversita);

        // Creazione bacheca Lavoro
        ArrayList<ToDo> todoLavoro = new ArrayList<>();
        todoLavoro.add(t5);
        todoLavoro.add(t6);
        Bacheca bLavoro = new Bacheca(Titolo.Lavoro, "Bacheca lavoro", Ordinamento.AZ, todoLavoro);


        // Creazione utente con tutte le bacheche separate
        Utente utente = new Utente("a", "a", bTempoLibero, bUniversita, bLavoro);
        
        // Inizializzazione della HashMap degli utenti registrati
        utentiRegistrati = new HashMap<>();
        utentiRegistrati.put(utente.getEmail(), utente);
        
        return utentiRegistrati;
    }

}
