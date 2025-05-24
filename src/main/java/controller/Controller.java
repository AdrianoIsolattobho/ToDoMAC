package controller;

import gui.*;

import model.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Arrays;

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

        Main mainView = view.getLogInView().getMainView();
        
        aggiornaInterfacciaUtente(mainView);
        
        view.setContentPane(mainView.getMain());
        view.pack();
        view.setLocationRelativeTo(null);
        view.revalidate();
        view.repaint();
        mainView.getAggiungiToDo().addActionListener(e -> {
            CreaToDo creaTodoDialog = new CreaToDo(SwingUtilities.getWindowAncestor(mainView.getMain()));
            creaTodoDialog.getSalvaButton().addActionListener(saveEvent -> {
                // Qui va il codice per salvare il nuovo ToDo
                String titolo = creaTodoDialog.getTitolo();
                String descrizione = creaTodoDialog.getDescrizione();
                String priorita = creaTodoDialog.getPriorita();
                Object dataScadenza = creaTodoDialog.getDataScadenza();

                // Logica per creare un nuovo ToDo
                // ...

                creaTodoDialog.dispose();
                // Aggiorna l'interfaccia
                aggiornaInterfacciaUtente(mainView);
            });
            creaTodoDialog.setVisible(true);
        });

    }

    private void aggiornaInterfacciaUtente(Main mainView) {
        // Verifica se l'utente attuale è valido e ha delle bacheche
        if (utenteAttuale != null && utenteAttuale.getBacheche() != null) {
            mainView.setNomeText( utenteAttuale.getEmail());
            Bacheca[] bachecheUtente = utenteAttuale.getBacheche();

            for (Bacheca bacheca : bachecheUtente) {
                if (bacheca != null && bacheca.getToDoList() != null) {

                    JPanel contenitoreToDo = null;

                    switch (bacheca.getTitolo()) {
                        case TempoLibero: // Bacheca per Tempo Libero
                            contenitoreToDo = mainView.getContenitoreToDoT();
                            break;
                        case Università: // Bacheca per Università
                            contenitoreToDo = mainView.getContenitoreToDoU();
                            break;
                        case Lavoro: // Bacheca per Lavoro
                            contenitoreToDo = mainView.getContenitoreToDoL();
                            break;
                    }

                        // Aggiunge ogni ToDo al contenitore specifico
                        for (ToDo todo : bacheca.getToDoList()) {
                            visualizzaToDo(todo, contenitoreToDo);
                        }

                }
            }
        }
    }

    // Metodo per aggiungere visivamente un ToDo al contenitore
    private void visualizzaToDo(@NotNull ToDo todo, @NotNull JPanel contenitoreToDo) {

        // Crea un nuovo JPanel per il singolo ToDo
        JPanel todoPanel = new JPanel();
        todoPanel.setLayout(new BoxLayout(todoPanel, BoxLayout.Y_AXIS));
        todoPanel.setBorder(BorderFactory.createTitledBorder(todo.getTitolo()));

        //set background del colore scelto
        if (todo.getSfondo()!=null){
            todoPanel.setBackground(todo.getSfondo());
        }else {
            todoPanel.setBackground(new Color(255, 255, 255));
        }

        //panel con label e checkbox
        JPanel descrizionePanel = new JPanel();
        descrizionePanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        // Crea e aggiungi una JLabel con il titolo del ToDo
        JLabel descrizioneLabel = new JLabel(todo.getDescrizione());
        descrizioneLabel.setFont(new Font("Arial", Font.PLAIN , 16));

        //creazione checkbox
        JCheckBox checkboxTodo = new JCheckBox();
        checkboxTodo.setSelected(todo.isCompletato());

        //aggiunta al panel descrizione
        descrizionePanel.add(checkboxTodo);
        descrizionePanel.add(descrizioneLabel);

        //aggiunta al panel todo
        todoPanel.add(descrizionePanel);

        if (todo.getChecklist()!=null){
            JPanel checklistPanel = new JPanel();
            checklistPanel.setLayout(new BoxLayout(checklistPanel, BoxLayout.Y_AXIS));
            checklistPanel.setBorder(BorderFactory.createTitledBorder(todo.getChecklist().getNomeChecklist()));


            //for each delle attività
            for (Attività att : todo.getChecklist().getAttività()) {
                //panel delle attività
                JPanel attivitàPanel = new JPanel();
                attivitàPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

                // Crea una checkbox e imposta lo stato in base allo stato dell'attività
                JCheckBox checkBoxAtt = new JCheckBox();
                checkBoxAtt.setSelected(att.isCompletata());

                //label delle attività
                JLabel attLabel = new JLabel(att.getNome());
                attLabel.setFont(new Font("Arial", Font.BOLD, 16));

                //aggiunta al panel attività
                attivitàPanel.add(checkBoxAtt);
                attivitàPanel.add(attLabel);

                //aggiunta al panel checklist
                checklistPanel.add(attivitàPanel);
            }
            checklistPanel.revalidate();
            checklistPanel.repaint();
            todoPanel.add(checklistPanel);
            todoPanel.revalidate();
            todoPanel.repaint();
        }

        

        contenitoreToDo.add(todoPanel);
        

        contenitoreToDo.revalidate();
        contenitoreToDo.repaint();
    }

    /**
     * Costruttore del controller.
     * Inizializza la vista e aggiunge i listener per i pulsanti di login e
     * registrazione.
     *
     * @param view la vista principale
     */
    public Controller(@NotNull Scelta view) {
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
     */
    private void inizializzaDatiEsempio() {
        // Creazione attività per la checklist della spesa
        Attività a1 = new Attività("comprare il latte", false);
        Attività a2 = new Attività("comprare il pane", false);
        Attività a3 = new Attività("comprare le uova", false);

        // Creazione checklist
        ArrayList<Attività> serieAttività = new ArrayList<>();
        serieAttività.add(a1);
        serieAttività.add(a2);
        serieAttività.add(a3);
        Checklist c1 = new Checklist("Compra",serieAttività);

        // Creazione data di scadenza
        Calendar scadenza = Calendar.getInstance();
        scadenza.set(2025, Calendar.OCTOBER, 15);

        // Creazione ToDo per la bacheca Tempo Libero
        ToDo t1;
        try {
            t1 = new ToDo("Lista della spesa", "Comprare tutto al supermercato",
                    new URI("https", "www.google.it", null, null), scadenza, false, false, false, new Color(255, 102, 102), null, c1);
        } catch (URISyntaxException e) {
            // Gestione dell'eccezione
            t1 = new ToDo("Lista della spesa", "Comprare tutto al supermercato", scadenza, false, false, false);
        }
        ToDo t2 = new ToDo("Preparare la cena", "Cucinare le polpette", scadenza, false, false, false);

        // Creazione ToDo per la bacheca Università
        ToDo t3 = new ToDo("Studiare Java", "Ripassare le basi di programmazione", scadenza, true, false, false);
        ToDo t4 = new ToDo("Preparare esame", "Completare gli esercizi", scadenza, false, true, false);
        
        // Creazione ToDo per la bacheca Lavoro
        ToDo t5 = new ToDo("Meeting settimanale", "Preparare la presentazione", scadenza, false, true, false);
        ToDo t6 = new ToDo("Email clienti", "Rispondere alle email urgenti", scadenza, true, false, false);

        // Aggiungi tutti i ToDo alla lista generale
        todos.addAll(Arrays.asList(t1, t2, t3, t4, t5, t6));

        // Creazione bacheca Tempo Libero
        ArrayList<ToDo> todoTempoLibero = new ArrayList<>();
        todoTempoLibero.add(t1);
        todoTempoLibero.add(t2);
        Bacheca bTempoLibero = new Bacheca(Titolo.TempoLibero, "Bacheca personale", Ordinamento.AZ, todoTempoLibero);

        // Creazione bacheca Università
        ArrayList<ToDo> todoUniversità = new ArrayList<>();
        todoUniversità.add(t3);
        todoUniversità.add(t4);
        Bacheca bUniversità = new Bacheca(Titolo.Università, "Bacheca università", Ordinamento.SCADENZA_ASC, todoUniversità);

        // Creazione bacheca Lavoro
        ArrayList<ToDo> todoLavoro = new ArrayList<>();
        todoLavoro.add(t5);
        todoLavoro.add(t6);
        Bacheca bLavoro = new Bacheca(Titolo.Lavoro, "Bacheca lavoro", Ordinamento.AZ, todoLavoro);

        // Aggiungi tutte le bacheche alla lista generale
        bacheche.addAll(Arrays.asList(bTempoLibero, bUniversità, bLavoro));

        // Creazione array bacheche per l'utente
        Bacheca[] bachecheutente = new Bacheca[3];
        bachecheutente[0] = bTempoLibero;
        bachecheutente[1] = bUniversità;
        bachecheutente[2] = bLavoro;

        // Creazione utente con tutte le bacheche
        Utente utente = new Utente("a", "a", bachecheutente);
        utentiRegistrati.put(utente.getEmail(), utente);
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
    


    public static void main(String[] args) {
        StileSwing.applicaStile();
        SwingUtilities.invokeLater(() -> {
            Scelta view = new Scelta();
            new Controller(view);
        });
    }
}