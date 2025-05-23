package controller;

import gui.*;

import model.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class Controller {

    private Scelta view;
    private HashMap<String, Utente> utentiRegistrati;
    private Utente utenteAttuale;

    // Oggetti di modello per gestire l'applicazione
    private ArrayList<Bacheca> bacheche;
    private ArrayList<ToDo> todos;
    private ArrayList<Condivisione> condivisioni;

    private void mostraScelta() {
        view.setContentPane(view.getScelta());
        view.pack();
        view.setLocationRelativeTo(null);
        view.revalidate();
        view.repaint();
    }

    private void mostraLogin() {
        view.setContentPane(view.getLogInView().getMainLogIn());
        view.pack();
        view.setLocationRelativeTo(null);
        view.revalidate();
        view.repaint();
    }

    private void mostraRegistrazione() {
        view.setContentPane(view.getRegisterView().getMainRegistrazione());
        view.pack();
        view.setLocationRelativeTo(null);
        view.revalidate();
        view.repaint();
    }

    private void mostraMain() {
        // Aggiorna la GUI con i dati dell'utente loggato
        Main mainView = view.getLogInView().getMainView();
        
        // Qui potresti aggiornare elementi della GUI con i dati dell'utente
        // Ad esempio, impostare il nome dell'utente, popolare le bacheche, ecc.
        
        view.setContentPane(mainView.getMain());
        view.pack();
        view.setLocationRelativeTo(null);
        view.revalidate();
        view.repaint();
    }

    /**
     * Costruttore del controller.
     * Inizializza la vista e aggiunge i listener per i pulsanti di login e
     * registrazione.
     *
     * @param view la vista principale
     */
    public Controller(Scelta view) {
        this.view = view;
        this.utentiRegistrati = new HashMap<>();
        this.bacheche = new ArrayList<>();
        this.todos = new ArrayList<>();
        this.condivisioni = new ArrayList<>();

        // Inizializzazione dei dati di esempio
        inizializzaDatiEsempio();

        // Configurazione dei componenti dopo l'inizializzazione
        view.getLogInView().setupComponents();
        view.getRegisterView().setupComponents();

        view.getLogInButton().addActionListener(e -> {
            this.mostraLogin();

            LogIn loginView = view.getLogInView();
            loginView.getBack().addActionListener(ev -> this.mostraScelta());
            // Gestione click su "entra"
            loginView.getEntra().addActionListener(ev -> gestisciLogin(loginView));
        });

        view.getRegistratiButton().addActionListener(e -> {
            this.mostraRegistrazione();

            Register registerView = view.getRegisterView();
            registerView.getBackButton().addActionListener(ev -> this.mostraScelta());
            // Listener per il pulsante "Registrati"
            registerView.getRegistratiButton().addActionListener(ev -> gestisciRegistrazione(registerView));
        });
    }

    /**
     * Inizializza i dati di esempio per l'applicazione.
     * Simile a quanto fatto in Programma.java
     */
    private void inizializzaDatiEsempio() {
        // Creazione attività
        Attività a1 = new Attività("comprare il latte", false);
        Attività a2 = new Attività("comprare il pane", false);
        Attività a3 = new Attività("comprare le uova", false);

        // Creazione checklist
        ArrayList<Attività> serieAttività = new ArrayList<>();
        serieAttività.add(a1);
        serieAttività.add(a2);
        serieAttività.add(a3);
        Checklist c1 = new Checklist(serieAttività);

        // Creazione ToDo
        Calendar scadenza = Calendar.getInstance();
        scadenza.set(2025, Calendar.OCTOBER, 15);

        ToDo t1 = new ToDo("comprare la spesa", "comprare la spesa al supermercato", 
                "www.supermercato.it", scadenza, false, false, false, "sfondo", "immagine", c1);
        ToDo t2 = new ToDo("cucinare", "cucinare le polpette", scadenza, false, false, false);

        todos.add(t1);
        todos.add(t2);

        // Creazione bacheca
        ArrayList<ToDo> listaToDo1 = new ArrayList<>();
        listaToDo1.add(t1);
        listaToDo1.add(t2);
        Bacheca b1 = new Bacheca(Titolo.TempoLibero, "Bacheca per la spesa", Ordinamento.AZ, listaToDo1);
        
        bacheche.add(b1);

        // Creazione utente
        Bacheca bacheche1[] = new Bacheca[3];
        bacheche1[0] = b1;
        Utente u1 = new Utente("giovanni@gmail.com", "3457890", bacheche1);
        
        // Aggiungi utente test per login
        Utente utenteTest = new Utente("test@email.com", "password", bacheche1);
        
        utentiRegistrati.put(u1.getEmail(), u1);
        utentiRegistrati.put(utenteTest.getEmail(), utenteTest);

        // Creazione seconda bacheca per condivisione
        ArrayList<ToDo> listaToDo2 = new ArrayList<>();
        ToDo t3 = new ToDo("comprare la frutta", "comprare la frutta al mercato", scadenza, false, false, false);
        listaToDo2.add(t3);
        Bacheca b2 = new Bacheca(Titolo.TempoLibero, "Bacheca prova condivisione", Ordinamento.AZ, listaToDo2);
        
        todos.add(t3);
        bacheche.add(b2);
        
        // Creazione utente per condivisione
        Bacheca bacheche2[] = new Bacheca[3];
        bacheche2[0] = b2;
        Utente u2 = new Utente("storti@gmail.com", "356780", bacheche2);
        utentiRegistrati.put(u2.getEmail(), u2);

        // Creazione condivisione
        ArrayList<Utente> condivisiCon = new ArrayList<>();
        condivisiCon.add(u2);
        Condivisione co1 = new Condivisione(u1, t1, condivisiCon);
        condivisioni.add(co1);
    }

    /**
     * Gestisce il login dell'utente.
     * Controlla se i campi sono compilati e verifica le credenziali.
     *
     * @param loginView la vista di login
     */
    private void gestisciLogin(LogIn loginView) {
        String email = loginView.getEmailText();
        String password = loginView.getPasswordText();

        if (email.isEmpty() || password.isEmpty() || 
            email.equals("Email") || password.equals("Password")) {
            JOptionPane.showMessageDialog(loginView, "Compila tutti i campi.", "Attenzione",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Verifica le credenziali
        Utente utente = utentiRegistrati.get(email);
        if (utente != null && utente.getPassword().equals(password)) {
            utenteAttuale = utente;
            JOptionPane.showMessageDialog(loginView, "Login effettuato!", "Successo", JOptionPane.INFORMATION_MESSAGE);
            this.mostraMain();
        } else {
            JOptionPane.showMessageDialog(loginView, "Email o password non corretti.", "Errore",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Gestisce la registrazione dell'utente.
     * Controlla se i campi sono compilati correttamente e registra il nuovo utente.
     *
     * @param registerView la vista di registrazione
     */
    private void gestisciRegistrazione(Register registerView) {
        String email = registerView.getEmailText();
        String password = registerView.getPasswordText();
        String confermaPassword = registerView.getConfermaPasswordText();

        if (email.isEmpty() || password.isEmpty() || confermaPassword.isEmpty() ||
            email.equals("Email") || password.equals("Password") || confermaPassword.equals("Conferma password")) {
            JOptionPane.showMessageDialog(registerView, "Compila tutti i campi.", "Attenzione",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!password.equals(confermaPassword)) {
            JOptionPane.showMessageDialog(registerView, "Le password non corrispondono.", "Errore",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (utentiRegistrati.containsKey(email)) {
            JOptionPane.showMessageDialog(registerView, "Email già registrata.", "Errore",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Registrazione del nuovo utente
        Bacheca[] bacheche = new Bacheca[3]; // Array vuoto per il nuovo utente
        Utente nuovoUtente = new Utente(email, password, bacheche);
        utentiRegistrati.put(email, nuovoUtente);

        JOptionPane.showMessageDialog(registerView, "Registrazione avvenuta con successo! Effettua il login.",
                "Successo", JOptionPane.INFORMATION_MESSAGE);
        this.mostraLogin();
    }

    /**
     * Metodo per aggiungere un nuovo ToDo all'utente corrente.
     * 
     * @param titolo titolo del ToDo
     * @param descrizione descrizione del ToDo
     * @param scadenza data di scadenza
     * @param bachecaIndex indice della bacheca in cui aggiungere il ToDo
     */
    public void aggiungiToDo(String titolo, String descrizione, Calendar scadenza, int bachecaIndex) {
        if (utenteAttuale != null && bachecaIndex >= 0 && bachecaIndex < utenteAttuale.getBacheche().length &&
            utenteAttuale.getBacheche()[bachecaIndex] != null) {
            
            ToDo nuovoToDo = new ToDo(titolo, descrizione, scadenza, false, false, false);
            todos.add(nuovoToDo);
            
            Bacheca bacheca = utenteAttuale.getBacheche()[bachecaIndex];
            bacheca.getToDoList().add(nuovoToDo);
            
            // Aggiorna la GUI se necessario
        }
    }

    /**
     * Metodo per aggiungere una nuova bacheca all'utente corrente.
     * 
     * @param titolo titolo della bacheca
     * @param descrizione descrizione della bacheca
     */
    public void aggiungiBacheca(Titolo titolo, String descrizione) {
        if (utenteAttuale != null) {
            Bacheca nuovaBacheca = new Bacheca(titolo, descrizione, Ordinamento.AZ, new ArrayList<>());
            bacheche.add(nuovaBacheca);
            
            // Trova uno slot vuoto nell'array delle bacheche dell'utente
            Bacheca[] bachecheDellUtente = utenteAttuale.getBacheche();
            for (int i = 0; i < bachecheDellUtente.length; i++) {
                if (bachecheDellUtente[i] == null) {
                    bachecheDellUtente[i] = nuovaBacheca;
                    break;
                }
            }
            
            // Aggiorna la GUI se necessario
        }
    }

    /**
     * Metodo per condividere un ToDo con un altro utente.
     * 
     * @param toDoIndex indice del ToDo da condividere
     * @param emailDestinatario email dell'utente con cui condividere
     */
    public void condividiToDo(int toDoIndex, String emailDestinatario) {
        if (utenteAttuale != null && toDoIndex >= 0 && toDoIndex < todos.size()) {
            Utente destinatario = utentiRegistrati.get(emailDestinatario);
            if (destinatario != null) {
                ToDo toDoCondiviso = todos.get(toDoIndex);
                ArrayList<Utente> utentiCondivisi = new ArrayList<>();
                utentiCondivisi.add(destinatario);
                
                Condivisione nuovaCondivisione = new Condivisione(utenteAttuale, toDoCondiviso, utentiCondivisi);
                condivisioni.add(nuovaCondivisione);
                
                JOptionPane.showMessageDialog(view, "ToDo condiviso con successo con " + emailDestinatario, 
                        "Condivisione", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(view, "Utente non trovato.", "Errore", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Ordina i ToDo di una bacheca secondo il criterio specificato.
     * 
     * @param bachecaIndex indice della bacheca nell'array dell'utente
     * @param nuovoOrdinamento nuovo criterio di ordinamento
     */
    public void ordinaToDo(int bachecaIndex, Ordinamento nuovoOrdinamento) {
        if (utenteAttuale != null && bachecaIndex >= 0 && bachecaIndex < utenteAttuale.getBacheche().length &&
            utenteAttuale.getBacheche()[bachecaIndex] != null) {
            
            Bacheca bacheca = utenteAttuale.getBacheche()[bachecaIndex];
            ArrayList<ToDo> toDoList = bacheca.getToDoList();
            
            // Imposta il nuovo ordinamento
            bacheca.setOrdinamento(nuovoOrdinamento);
            
            // Ordina la lista di ToDo in base al criterio specificato
            switch (nuovoOrdinamento) {
                case AZ:
                    Collections.sort(toDoList, Comparator.comparing(ToDo::getTitolo));
                    break;
                case ZA:
                    Collections.sort(toDoList, Comparator.comparing(ToDo::getTitolo).reversed());
                    break;
                case CREAZIONE_ASC:
                    Collections.sort(toDoList, Comparator.comparing(ToDo::getCreazione));
                    break;
                case CREAZIONE_DESC:
                    Collections.sort(toDoList, Comparator.comparing(ToDo::getCreazione).reversed());
                    break;
                case SCADENZA_ASC:
                    Collections.sort(toDoList, Comparator.comparing(ToDo::getScadenza));
                    break;
                case SCADENZA_DESC:
                    Collections.sort(toDoList, Comparator.comparing(ToDo::getScadenza).reversed());
                    break;
            }
            
            // Aggiorna la bacheca con la lista ordinata
            bacheca.setToDoList(toDoList);
            
            // Qui potresti aggiornare la GUI per mostrare la nuova sequenza ordinata
        }
    }
    
    /**
     * Cambia il criterio di ordinamento di una bacheca e riordina i ToDo.
     * 
     * @param bachecaIndex indice della bacheca
     * @param nuovoOrdinamento nuovo criterio di ordinamento
     */
    public void cambiaOrdinamento(int bachecaIndex, Ordinamento nuovoOrdinamento) {
        if (utenteAttuale != null && bachecaIndex >= 0 && bachecaIndex < utenteAttuale.getBacheche().length &&
            utenteAttuale.getBacheche()[bachecaIndex] != null) {
            
            // Aggiorna l'ordinamento nella bacheca e riordina i ToDo
            ordinaToDo(bachecaIndex, nuovoOrdinamento);
            
            // Notifica l'utente del cambiamento
            JOptionPane.showMessageDialog(view, 
                    "L'ordinamento della bacheca è stato cambiato in: " + nuovoOrdinamento.name(), 
                    "Ordinamento cambiato", 
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Ordina tutte le bacheche dell'utente corrente secondo i loro rispettivi criteri di ordinamento.
     * Può essere utile dopo l'aggiunta di nuovi ToDo o la modifica di ToDo esistenti.
     */
    public void ordinaTutteLeBacheche() {
        if (utenteAttuale != null) {
            for (int i = 0; i < utenteAttuale.getBacheche().length; i++) {
                if (utenteAttuale.getBacheche()[i] != null) {
                    Bacheca bacheca = utenteAttuale.getBacheche()[i];
                    ordinaToDo(i, bacheca.getOrdinamento());
                }
            }
        }
    }

    public static void main(String[] args) {
        StileSwing.applicaStile();
        SwingUtilities.invokeLater(() -> {
            Scelta view = new Scelta();
            new Controller(view);
        });
    }
}
