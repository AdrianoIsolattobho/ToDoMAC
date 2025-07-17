package controller;

import gui.*;

import implementazioni_postgres_dao.*;
import model.*;
import org.jetbrains.annotations.NotNull;
import dao.*;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;

/**
 * Controller che gestisce tutte le interazioni tra modello e view
 * come nel paradigma Model-View-Controller (MVC)
 * o Boundary-Control-Entity (BCE)
 */
public class Controller {

    private Scelta view;
    private Utente utenteAttuale;
    private boolean mostraCompletati = false;
    private boolean mostraScadenze = false;
    private ToDoDAO toDoDAO = new ToDoImplementazionePostgresDAO();
    private UtenteDAO utenteDAO = new UtenteImplementazionePostgresDAO();
    private CondivisioneDAO condivisioneDAO = new CondivisioneImplementazionePostgresDAO();
    private BachecaDAO bachecaDAO = new BachecaImplementazionePostgresDAO();

    // costanti stringhe per aggraziarsi SonarQube
    private static final String ERRORMESSAGE = "Errore";
    private static final String FORMATODATA = "dd/MM/yyyy";
    private static final String TA = "Titolo attività";
    private static final String TL = "Tempo libero";
    private static final String LAV = "Lavoro";
    private static final String UNI = "Università";
    private static final String ATTENZIONE = "Attenzione";
    private static final Font FONTICONE = new Font("Dialog", Font.BOLD, 18);
    private static final String AGGIUNGI = "aggiungi";
    private static final String RIMUOVI = "rimuovi";

    /**
     * Gestisce la ricerca e visualizza i risultati
     */
    private void gestisciRicerca(String termineRicerca, Main mainView) {
        ArrayList<ToDo> risultati = filtraToDoPerRicerca(termineRicerca);

        // Nascondi tutte le bacheche normali
        mainView.getBaFre().setVisible(false);
        mainView.getBaLav().setVisible(false);
        mainView.getBaUni().setVisible(false);
        mainView.getBaSca().setVisible(false);

        // Mostra pannello risultati ricerca
        JPanel pannelloRisultati = mainView.getContenitoreToDoRIc();
        pannelloRisultati.removeAll();

        if (risultati.isEmpty()) {
            JLabel nessunRisultato = new JLabel("Nessun ToDo trovato per: " + termineRicerca);
            pannelloRisultati.add(nessunRisultato);
        } else {
            for (ToDo todo : risultati) {
                if (mostraCompletati || !todo.isCompletato()) {
                    visualizzaToDo(todo, pannelloRisultati, null);
                }
            }
        }

        // Aggiorna il titolo del pannello
        mainView.getBaRic().setBorder(BorderFactory.createTitledBorder("Risultati ricerca: " + termineRicerca));
        mainView.getBaRic().setVisible(true);

        pannelloRisultati.revalidate();
        pannelloRisultati.repaint();
    }

    /**
     * Azzera la ricerca e mostra tutte le bacheche
     */
    private void azzeraRicerca(Main mainView) {
        // Ripristina la visualizzazione normale
        aggiornaInterfacciaUtente(mainView);
        mainView.getBaRic().setVisible(false);
    }

    /**
     * Metodo per filtrare i ToDo in base al termine di ricerca
     * 
     * @param termineRicerca il termine da cercare
     * @return lista dei ToDo che corrispondono alla ricerca
     */
    private ArrayList<ToDo> filtraToDoPerRicerca(String termineRicerca) {
        ArrayList<ToDo> risultati = new ArrayList<>();

        if (termineRicerca == null || termineRicerca.trim().isEmpty()) {
            // Se non c'è termine di ricerca, ritorna tutti i ToDo
            raccogliTuttiToDo(risultati);
            return risultati;
        }

        String termine = termineRicerca.toLowerCase().trim();

        // Cerca in tutte le bacheche
        cercaInBacheca(utenteAttuale.getTempoLibero(), termine, risultati);
        cercaInBacheca(utenteAttuale.getLavoro(), termine, risultati);
        cercaInBacheca(utenteAttuale.getUniversita(), termine, risultati);

        return risultati;
    }

    /**
     * Cerca ToDo in una specifica bacheca
     */
    private void cercaInBacheca(Bacheca bacheca, String termine, ArrayList<ToDo> risultati) {
        if (bacheca != null && bacheca.getToDoList() != null) {
            for (ToDo todo : bacheca.getToDoList()) {
                if (corrispondeRicerca(todo, termine)) {
                    risultati.add(todo);
                }
            }
        }
    }

    /**
     * Verifica se un ToDo corrisponde al termine di ricerca
     */
    private boolean corrispondeRicerca(ToDo todo, String termine) {
        // Cerca nel titolo e nella descrizione
        return todo.getTitolo() != null && todo.getTitolo().toLowerCase().contains(termine)
                || todo.getDescrizione() != null && todo.getDescrizione().toLowerCase().contains(termine);
    }

    /**
     * Raccoglie tutti i ToDo da tutte le bacheche
     */
    private void raccogliTuttiToDo(ArrayList<ToDo> risultati) {
        if (utenteAttuale.getTempoLibero() != null && utenteAttuale.getTempoLibero().getToDoList() != null) {
            risultati.addAll(utenteAttuale.getTempoLibero().getToDoList());
        }
        if (utenteAttuale.getLavoro() != null && utenteAttuale.getLavoro().getToDoList() != null) {
            risultati.addAll(utenteAttuale.getLavoro().getToDoList());
        }
        if (utenteAttuale.getUniversita() != null && utenteAttuale.getUniversita().getToDoList() != null) {
            risultati.addAll(utenteAttuale.getUniversita().getToDoList());
        }
    }

    /**
     * Metodo helper che esegue delle funzioni di base per mostrare al meglio un
     * pannello
     * 
     * @param scelto il pannello da mostrare
     */
    private void mostraPanel(JPanel scelto) {
        view.setContentPane(scelto);
        view.pack();
        view.setLocationRelativeTo(null);
        view.revalidate();
        view.repaint();
    }

    /**
     * Metodo per mostrare la view principale
     * aggiunge gli action listener per l'aggiunta di nuovi todo
     * per il logout dal programma
     * e per mostrare i todo già completati
     */
    private void mostraMain() {
        Main mainView = view.getLogInView().getMainView();

        aggiornaInterfacciaUtente(mainView);

        mostraPanel(mainView.getMain());

        // action listener della aggiunta di un nuovo todo
        mainView.getAggiungiToDo().addActionListener(e -> aggiuntaTodo());

        // action listener per mostrare i todo già completati
        mainView.getMostraCompletati().addActionListener(
                e -> {
                    this.mostraCompletati = !this.mostraCompletati;
                    mainView.getMostraCompletati()
                            .setText(mostraCompletati ? "Mostra senza completati" : "Mostra tutti");
                    aggiornaInterfacciaUtente(mainView);
                });

        // action listener per uscire dal programma
        mainView.getEsci().addActionListener(e -> mostraPanel(view.getScelta()));

        mainView.getMostraInScadenza().addActionListener(_ -> {
            this.mostraScadenze = !this.mostraScadenze;
            mainView.getBaSca().setVisible(mostraScadenze);
            mainView.getMostraInScadenza().setText(mostraScadenze ? "Mostra senza Scadenze" : "Mostra Scadenze");
            aggiornaInterfacciaUtente(mainView);
        });

        // ActionListener per azzerare la ricerca
        mainView.getButtonAzzera().addActionListener(e -> {
            mainView.getCampoRicerca().setText("");
            azzeraRicerca(mainView);
        });

        SetPlaceHolder.setTP(mainView.getCampoRicerca(), "Ricerca un ToDo", GestioneDarkMode.isDarkMode());
        // Ricerca in tempo reale (opzionale)
        mainView.getCampoRicerca().addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String termineRicerca = mainView.getCampoRicerca().getText();
                if (termineRicerca.length() >= 2) { // Inizia la ricerca dopo 2 caratteri
                    gestisciRicerca(termineRicerca, mainView);
                } else if (termineRicerca.isEmpty()) {
                    azzeraRicerca(mainView);
                }
            }
        });

    }

    /**
     * Metodo estratto da AggiuntaTodo e usato anche in ModificaTodo per salvare
     * l'immagine selezionata
     * ActionListener per la selezione dell'immagine
     * 
     * @param immagineScelta riferimento dell'immagine selezionata
     * @param creaTodoDialog il dialog da cui viene chiamato il metodo
     */
    private ActionListener generaActionListenerSceltaImmagine(AtomicReference<URL> immagineScelta,
            CreaToDo creaTodoDialog) {
        return _ -> {
            // utilizziamo la libreria JFile Chooser che ci permette di scegliere i file
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Seleziona un'immagine");

            // Filtro per mostrare solo file di immagine
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "Immagini (JPG, PNG, GIF)", "jpg", "jpeg", "png", "gif");
            fileChooser.setFileFilter(filter);

            // check della validità del file, se il file è valido chiama caricaImmagine che
            // ne mostra una preview
            int result = fileChooser.showOpenDialog(creaTodoDialog);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                if (!selectedFile.exists()) {
                    JOptionPane.showMessageDialog(creaTodoDialog,
                            "Il file selezionato non esiste.",
                            ERRORMESSAGE, JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!selectedFile.canRead()) {
                    JOptionPane.showMessageDialog(creaTodoDialog,
                            "Impossibile leggere il file selezionato.",
                            ERRORMESSAGE, JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    // legge l'immagine
                    BufferedImage image = ImageIO.read(selectedFile);

                    // crea cartella immagini_todo se non esiste
                    File outputDir = new File("immagini_todo");
                    if (!outputDir.exists()) {
                        outputDir.mkdir();
                    }

                    // genera un nome file univoco
                    String estensione = getEstensione(selectedFile.getName());
                    String nuovoNomeFile = UUID.randomUUID().toString() + "." + estensione;
                    File destinazione = new File(outputDir, nuovoNomeFile);

                    // salva l'immagine nel nuovo file
                    ImageIO.write(image, estensione, destinazione);

                    // mostra l'immagine nella preview
                    caricaImmagine(new ImageIcon(image),
                            creaTodoDialog.getPreviewPanel(), creaTodoDialog);

                    // imposta il nuovo path nell'AtomicReference
                    immagineScelta.set(destinazione.toURI().toURL());
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(creaTodoDialog,
                            "Errore durante il caricamento dell'immagine: " + e.getMessage(),
                            ERRORMESSAGE, JOptionPane.ERROR_MESSAGE);
                }
            }
        };
    }

    /**
     * Metodo per ricavare l'estensione desiderata dal file
     *
     * @param nomeFile nome file immagine
     * @return estensione
     */
    private String getEstensione(String nomeFile) {
        int punto = nomeFile.lastIndexOf('.');
        if (punto > 0 && punto < nomeFile.length() - 1) {
            return nomeFile.substring(punto + 1).toLowerCase();
        }
        return "png"; // fallback per i file con estensione non valida
    }

    /**
     * Metodo estratto da AggiuntaTodo e usato anche in ModificaTodo per salvare il
     * colore
     * ActionListener per la selezione del colore
     * 
     * @param coloreScelto   riferimento al colore selezionato
     * @param creaTodoDialog il dialog da cui viene chiamato il metodo
     */
    private ActionListener generaActionListenerSceltaColore(AtomicReference<Color> coloreScelto,
            CreaToDo creaTodoDialog) {
        return _ -> {
            // usiamo la funzione base JColorChooser che ci permette di scegliere il colore
            coloreScelto.set(JColorChooser.showDialog(
                    creaTodoDialog,
                    "Scegli un colore per il ToDo", coloreScelto.get()));

            if (coloreScelto.get() != null) {
                // Cambia il colore del bottone per mostrare il colore scelto
                creaTodoDialog.getColorButton().setForeground(coloreScelto.get());
                creaTodoDialog.getColorButton().repaint();
                creaTodoDialog.getColorButton().revalidate();
            }
        };
    }

    /**
     * Metodo estratto da AggiuntaTodo e usato anche in ModificaTodo per salvare la
     * data di scadenza
     * ActionListener per la selezione della data di scadenza
     * 
     * @param dataScelto     riferimento alla data selezionata
     * @param creaTodoDialog il dialog da cui viene chiamato il metodo
     */
    private ActionListener generaActionListenerSceltaScadenza(AtomicReference<Calendar> dataScelto,
            CreaToDo creaTodoDialog) {
        return _ -> {
            // Creiamo un JDialog personalizzato per il calendario
            JDialog dateDialog = new JDialog(creaTodoDialog, "Seleziona data di scadenza", true);
            dateDialog.setLayout(new BorderLayout());

            // Utilizziamo JSpinner per la selezione della data
            JPanel calendarPanel = new JPanel();

            // Creiamo un calendario con JSpinner
            SpinnerDateModel dateModel = new SpinnerDateModel();
            JSpinner dateSpinner = new JSpinner(dateModel);
            JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, FORMATODATA);
            dateSpinner.setEditor(dateEditor);

            calendarPanel.add(new JLabel("Seleziona data: "));
            calendarPanel.add(dateSpinner);

            // Pulsanti di conferma e annulla
            JPanel buttonPanel = new JPanel();
            JButton confirmButton = new JButton("Conferma");
            JButton cancelButton = new JButton("Annulla");

            confirmButton.addActionListener(confirmEvent -> {
                // Salva la data selezionata
                Date selectedDate = (Date) dateSpinner.getValue();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(selectedDate);
                dataScelto.set(calendar);

                // Cambia il testo del bottone per mostrare la data selezionata
                SimpleDateFormat dateFormat = new SimpleDateFormat(FORMATODATA);
                creaTodoDialog.getDataScadenzaButton().setText(dateFormat.format(selectedDate));

                dateDialog.dispose();
            });

            cancelButton.addActionListener(_ -> dateDialog.dispose());

            buttonPanel.add(confirmButton);
            buttonPanel.add(cancelButton);

            dateDialog.add(calendarPanel, BorderLayout.CENTER);
            dateDialog.add(buttonPanel, BorderLayout.SOUTH);
            dateDialog.setSize(300, 150);
            dateDialog.setLocationRelativeTo(creaTodoDialog);
            dateDialog.setVisible(true);
        };
    }

    /**
     * Metodo estratto da AggiuntaTodo e usato anche in ModificaTodo per salvare la
     * CheckList
     * ActionListener per la creare il pannello da cui verrà poi ricavata la
     * CheckList
     * 
     * @param attivitaFields riferimento all' array di attività da salvare
     * @param creaTodoDialog il dialog da cui viene chiamato il metodo
     */
    private ActionListener generaActionListenerAggiungiCheckList(ArrayList<JTextField> attivitaFields,
            AtomicReference<JTextField> titoloCheckListRef, CreaToDo creaTodoDialog) {
        return _ -> {
            JPanel checklistPanel = new JPanel();
            checklistPanel.setLayout(new BoxLayout(checklistPanel, BoxLayout.Y_AXIS));

            JPanel titoloChecklist = new JPanel();
            titoloChecklist.setLayout(new BoxLayout(titoloChecklist, BoxLayout.X_AXIS));
            JTextField titoloCheckListField = new JTextField(20);
            SetPlaceHolder.setTP(titoloCheckListField, "Titolo CheckList", GestioneDarkMode.isDarkMode());
            titoloChecklist.add(titoloCheckListField);
            titoloCheckListRef.set(titoloCheckListField);
            JButton aggiungiAttivitaButton = new JButton("+");
            titoloChecklist.add(aggiungiAttivitaButton);
            JButton cancellaCheckListButton = new JButton("X");
            titoloChecklist.add(cancellaCheckListButton);
            titoloChecklist.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));

            checklistPanel.add(titoloChecklist);

            creaTodoDialog.getChecklistButton().setEnabled(false);
            creaTodoDialog.getChecklistButton().setText("V");
            creaTodoDialog.getChecklistButton().setBackground(Color.GRAY);

            cancellaCheckListButton.addActionListener(cancellaCheckListEvent -> {
                checklistPanel.removeAll();
                attivitaFields.clear();
                titoloCheckListRef.set(null);
                creaTodoDialog.getChecklistButton().setEnabled(true);
                creaTodoDialog.getChecklistButton().setText("Inserisci");
                creaTodoDialog.getChecklistButton().setBackground(Color.WHITE);
                creaTodoDialog.repaint();
                creaTodoDialog.pack();
                creaTodoDialog.revalidate();

            });

            aggiungiAttivitaButton.addActionListener(attivitaAddEvent -> {
                JPanel attivitaPanel = new JPanel();
                attivitaPanel.setLayout(new BoxLayout(attivitaPanel, BoxLayout.X_AXIS));
                JTextField titoloAttivitaField = new JTextField(20);
                SetPlaceHolder.setTP(titoloAttivitaField, TA, GestioneDarkMode.isDarkMode());
                attivitaFields.add(titoloAttivitaField);

                attivitaPanel.add(titoloAttivitaField);
                JButton cancellaAttivitaButton = new JButton("-");
                attivitaPanel.add(cancellaAttivitaButton);

                cancellaAttivitaButton.addActionListener(cancellaAttivitaEvent -> {
                    attivitaFields.remove(titoloAttivitaField);
                    attivitaPanel.removeAll();
                    creaTodoDialog.repaint();
                    creaTodoDialog.pack();
                    creaTodoDialog.revalidate();
                });

                checklistPanel.add(attivitaPanel);
                creaTodoDialog.repaint();
                creaTodoDialog.pack();
                creaTodoDialog.revalidate();

            });

            creaTodoDialog.getPannelloAggiungibile().add(checklistPanel);
            creaTodoDialog.repaint();
            creaTodoDialog.pack();
            creaTodoDialog.revalidate();
        };
    }

    /**
     * Metodo Helper per controllare la validità del link inserito nel campo Link
     * 
     * @param linkTesto      il link da controllare
     * @param creaTodoDialog il dialog da cui viene chiamato il metodo
     */
    private URI parseLink(String linkTesto, JDialog creaTodoDialog) {
        if (linkTesto != null && !linkTesto.trim().isEmpty()) {
            try {
                return new URI(linkTesto);
            } catch (URISyntaxException _) {
                JOptionPane.showMessageDialog(creaTodoDialog,
                        "Il link inserito non è valido. Formato corretto: https://esempio.com",
                        ERRORMESSAGE, JOptionPane.ERROR_MESSAGE);
                return null;
            }
        }
        return null;
    }

    /**
     * Genera un action listener per il salavataggio di un nuovo ToDo.
     * Questo listener gestisce la raccolta dei dati dal dialog di creazione,
     * valida i dati inseriti, crea un oggetto ToDo con le informazioni fornite,
     * lo salva nel database, aggiorna le bacheche in memoria e aggiorna l'interfaccia utente.
     *
     * @param mainView
     * @param creaTodoDialog
     * @param attivitaFields
     * @param dataScelto
     * @param coloreScelto
     * @param immagineScelta
     * @param titoloCheckListRef
     * @return un action listener che, una volta attivato, salva il nuovo todo con i dati inseriti
     */
    private ActionListener generaActionListenerSalvataggio(Main mainView, CreaToDo creaTodoDialog,
            ArrayList<JTextField> attivitaFields, AtomicReference<Calendar> dataScelto,
            AtomicReference<Color> coloreScelto, AtomicReference<URL> immagineScelta,
            AtomicReference<JTextField> titoloCheckListRef) {
        return _ -> {
            // Qui va il codice per salvare il nuovo ToDo
            String titoloTodo = creaTodoDialog.getTitolo().getText();
            String linkTesto = creaTodoDialog.getLinkField().getText();
            String descrizioneTodo = creaTodoDialog.getDescrizioneField().getText();
            // Recupera la bacheca selezionata dal ComboBox
            String bachecaSelezionata = (String) creaTodoDialog.getBachecaBox().getSelectedItem();

            // Validazione basilare
            if (titoloTodo == null || titoloTodo.trim().isEmpty()) {
                JOptionPane.showMessageDialog(creaTodoDialog,
                        "Inserisci un titolo valido per il ToDo",
                        ERRORMESSAGE, JOptionPane.ERROR_MESSAGE);
                return; // Non chiude il dialogo se c'è un errore
            }

            // check del link
            try {
                URI link = parseLink(linkTesto, creaTodoDialog);
                if (link == null && linkTesto != null && !linkTesto.trim().isEmpty()) {
                    return; // Esci se il link è invalido
                }

                // Crea il nuovo ToDo con tutti i dati raccolti
                ToDo nuovoToDo = new ToDo(titoloTodo, descrizioneTodo, link, dataScelto.get(), coloreScelto.get(),
                        immagineScelta.get(), null);
                ArrayList<Attivita> attivitaDaSalvare = estraiAttivitaDaFields(attivitaFields);
                if (attivitaDaSalvare != null && !attivitaDaSalvare.isEmpty()) {
                    Checklist c = new Checklist(titoloCheckListRef.get().getText(), attivitaDaSalvare);
                    nuovoToDo.setChecklist(c);
                }

                salvaTodoNelDatabase(nuovoToDo, creaTodoDialog,
                        stringToBacheca(bachecaSelezionata, creaTodoDialog), null);
                // aggiorna bacheche in memoria
                gestisciTodoInBacheca(nuovoToDo, bachecaSelezionata, creaTodoDialog, AGGIUNGI);
                creaTodoDialog.dispose();
                // Aggiorna l'interfaccia
                aggiornaInterfacciaUtente(mainView);
                view.pack();
                view.revalidate();
                view.repaint();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(creaTodoDialog,
                        "Errore durante la creazione del ToDo: " + ex.getMessage(),
                        ERRORMESSAGE, JOptionPane.ERROR_MESSAGE);
            }
        };
    }

    /**
     * Metodo per mostrare il pannello CreaToDo a modi pop up e gestione del
     * salvataggio in memoria dei dati
     */
    private void aggiuntaTodo() {
        Main mainView = view.getLogInView().getMainView();
        CreaToDo creaTodoDialog = new CreaToDo();
        creaTodoDialog.setContentPane(creaTodoDialog.getMainPanel());
        creaTodoDialog.setLocationRelativeTo(mainView.getMain());

        rimuoviTuttiActionListener(creaTodoDialog.getSalvaButton());
        rimuoviTuttiActionListener(creaTodoDialog.getChecklistButton());
        rimuoviTuttiActionListener(creaTodoDialog.getSfogliaButton());
        rimuoviTuttiActionListener(creaTodoDialog.getColorButton());
        rimuoviTuttiActionListener(creaTodoDialog.getDataScadenzaButton());

        DialogReferences references = configuraActionListenerSelezione(creaTodoDialog);

        ArrayList<JTextField> attivitaFields = new ArrayList<>();
        AtomicReference<JTextField> titoloCheckListRef = new AtomicReference<>();
        creaTodoDialog.getChecklistButton().addActionListener(
                generaActionListenerAggiungiCheckList(attivitaFields, titoloCheckListRef, creaTodoDialog));

        // Action listener per il pulsante Salva
        creaTodoDialog.getSalvaButton()
                .addActionListener(generaActionListenerSalvataggio(mainView, creaTodoDialog, attivitaFields,
                        references.getDataScelto(), references.getColoreScelto(), references.getImmagineScelta(),
                        titoloCheckListRef));

        // AGGIUNGI questo ActionListener per il pulsante di chiusura/cancella
        creaTodoDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                creaTodoDialog.dispose();
            }
        });

        creaTodoDialog.pack();
        creaTodoDialog.setLocationRelativeTo(view.getLogInView().getMainView().getMain());
        creaTodoDialog.setVisible(true);

    }

    /**
     * Estrae una lista di oggetti Attivita dai JTextField forniti
     * Ignora i campi null o vuoti.
     *
     * @param attivitaFields
     * @return ArrayList di Attivita create dai testi validi nei campi.
     */
    private ArrayList<Attivita> estraiAttivitaDaFields(ArrayList<JTextField> attivitaFields) {
        ArrayList<Attivita> attivita = new ArrayList<>();
        // Se non ci sono campi attività, ritorna lista vuota
        if (attivitaFields == null || attivitaFields.isEmpty()) {
            return attivita; // Ritorna lista vuota invece di null
        }

        // Processa solo i campi non null e non vuoti
        for (JTextField field : attivitaFields) {
            if (field != null) {
                String testo = field.getText();
                if (testo != null && !testo.trim().isEmpty()) {
                    attivita.add(new Attivita(testo.trim(),false));
                }
            }
        }
        return attivita;
    }

    /**
     * Genera un ActionListener per modificare la descrizione di una bacheca.
     * Mostra un dialog per l'inserimento della nuova descrizione, la salva nel database
     * e aggiorna l'interfaccia utente.
     * @param b
     * @param mainView
     * @return ActionListener da assegnare a un componente per modificare la descrizione.
     */
    private ActionListener generaActionListnerModificaDescrizione(Bacheca b, Main mainView) {
        return e -> {
            ModificaDescrizione modificaDescrizioneDialog = new ModificaDescrizione();
            modificaDescrizioneDialog.setTitle("Modifica descrizione bacheca: " + b.getTitolo());
            SetPlaceHolder.setTP(modificaDescrizioneDialog.getTextField(), "Descrizione",
                    GestioneDarkMode.isDarkMode());
            modificaDescrizioneDialog.setDescrizione(b.getDescrizione());
            modificaDescrizioneDialog.getButtonOK().addActionListener(
                    ex -> {
                        String nuovaDescrizione = modificaDescrizioneDialog.getTextFieldText();
                        b.setDescrizione(nuovaDescrizione);

                        // Salva la descrizione nel database
                        try {
                            if (b.getTitolo() == null) {
                                JOptionPane.showMessageDialog(modificaDescrizioneDialog,
                                        "Errore: titolo della bacheca non definito",
                                        ERRORMESSAGE, JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                            bachecaDAO.salvaDescrizioneBacheca(utenteAttuale.getEmail(), b.getTitolo(),
                                    nuovaDescrizione);
                        } catch (Exception e1) {
                            JOptionPane.showMessageDialog(modificaDescrizioneDialog,
                                    "Errore durante il salvataggio della descrizione: " + e1.getMessage(),
                                    ERRORMESSAGE, JOptionPane.ERROR_MESSAGE);
                        }

                        // Chiudi il dialog
                        modificaDescrizioneDialog.dispose();
                        // Ricarica l'interfaccia principale
                        aggiornaInterfacciaUtente(mainView);
                    });
            modificaDescrizioneDialog.pack();
            modificaDescrizioneDialog.setLocationRelativeTo(view.getLogInView().getMainView().getMain());
            modificaDescrizioneDialog.setVisible(true);
        };
    }

    /**
     * Genera un ActionListener per modificare l'ordinamento di una bacheca.
     * Mostra un dialog con le opzioni di ordinamento, salva la scelta nel database
     * e aggiorna l'interfaccia utente.
     *
     * @param b
     * @param mainView
     * @return ActionListener da assegnare a un componente per modificare la descrizione.
     */
    private ActionListener generaActionListnerModificaOrdine(Bacheca b, Main mainView) {
        return _ -> {
            GestioneOrdine gestioneOrdineDialog = new GestioneOrdine();

            gestioneOrdineDialog.getButtonOK().addActionListener(
                    ex -> {
                        String selectedCommand = gestioneOrdineDialog.getGroup().getSelection() != null
                                ? gestioneOrdineDialog.getGroup().getSelection().getActionCommand()
                                : null;

                        Ordinamento nuovoOrdinamento = null;
                        if (selectedCommand != null) {
                            // Usa uno switch per gestire le opzioni selezionate
                            switch (selectedCommand) {
                                case "AZ":
                                    nuovoOrdinamento = Ordinamento.AZ;
                                    break;
                                case "ZA":
                                    nuovoOrdinamento = Ordinamento.ZA;
                                    break;
                                case "CreazioneCrescente":
                                    nuovoOrdinamento = Ordinamento.CREAZIONE_ASC;
                                    break;
                                case "CreazioneDecrescente":
                                    nuovoOrdinamento = Ordinamento.CREAZIONE_DESC;
                                    break;
                                case "ScadenzaCrescente":
                                    nuovoOrdinamento = Ordinamento.SCADENZA_ASC;
                                    break;
                                case "ScadenzaDecrescente":
                                    nuovoOrdinamento = Ordinamento.SCADENZA_DESC;
                                    break;
                                default:
                                    nuovoOrdinamento = Ordinamento.AZ;
                                    break;
                            }
                        }

                        // Aggiorna l'ordinamento in memoria
                        b.setOrdinamento(nuovoOrdinamento);

                        // Salva l'ordinamento nel database
                        try {
                            bachecaDAO.salvaOrdinamentoBacheca(utenteAttuale.getEmail(), b.getTitolo(),
                                    nuovoOrdinamento);
                        } catch (Exception e1) {
                            JOptionPane.showMessageDialog(gestioneOrdineDialog,
                                    "Errore durante il salvataggio dell'ordinamento: " + e1.getMessage(),
                                    ERRORMESSAGE, JOptionPane.ERROR_MESSAGE);
                        }

                        gestioneOrdineDialog.dispose();
                        // Ricarica l'interfaccia principale
                        aggiornaInterfacciaUtente(mainView);
                    });
            gestioneOrdineDialog.pack();
            gestioneOrdineDialog.setLocationRelativeTo(view.getLogInView().getMainView().getMain());
            gestioneOrdineDialog.setVisible(true);
        };
    }

    /**
     * Metodo per generare la bacheca tempo libero, mostra il pannello solo se sono
     * presenti dei todo e richiama i metodi per generare gli action listener
     */
    private void generaTempoLibero(Main mainView) {
        // Bacheca Tempo Libero
        Bacheca tempoLibero = utenteAttuale.getTempoLibero();
        boolean haToDoTempoLibero = false;
        if (tempoLibero != null && tempoLibero.getToDoList() != null) {
            JPanel contenitoreToDoT = mainView.getContenitoreToDoT();
            contenitoreToDoT.removeAll();
            mainView.setDescrizioneFreText(tempoLibero.getDescrizione());

            rimuoviTuttiActionListener(mainView.getModificaDescrizioneFre());
            rimuoviTuttiActionListener(mainView.getOrdineFreButton());

            mainView.getModificaDescrizioneFre().addActionListener(
                    generaActionListnerModificaDescrizione(tempoLibero, mainView));

            mainView.getOrdineFreButton().addActionListener(
                    generaActionListnerModificaOrdine(tempoLibero, mainView));
            if (tempoLibero.getOrdinamento() != null) {
                tempoLibero.setToDoList(ordinaToDoList(tempoLibero.getToDoList(), tempoLibero.getOrdinamento()));
            }
            for (ToDo todo : tempoLibero.getToDoList()) {
                if (mostraCompletati || !todo.isCompletato()) {
                    visualizzaToDo(todo, contenitoreToDoT, null);
                    haToDoTempoLibero = true;
                }
            }

        }
        mainView.getBaFre().setVisible(haToDoTempoLibero);
    }

    /**
     * Metodo per generare la bacheca lavoro, mostra il pannello solo se sono
     * presenti dei todo e richiama i metodi per generare gli action listener
     */
    private void generaLavoro(Main mainView) {
        // Bacheca Lavoro
        Bacheca lavoro = utenteAttuale.getLavoro();
        boolean haToDoLavoro = false;
        if (lavoro != null && lavoro.getToDoList() != null) {
            JPanel contenitoreToDoL = mainView.getContenitoreToDoL();
            contenitoreToDoL.removeAll();
            mainView.setDescrizioneLavText(lavoro.getDescrizione());

            rimuoviTuttiActionListener(mainView.getModificaDescrizioneLav());
            rimuoviTuttiActionListener(mainView.getOrdineLavButton());

            mainView.getModificaDescrizioneLav().addActionListener(
                    generaActionListnerModificaDescrizione(lavoro, mainView));
            mainView.getOrdineLavButton().addActionListener(
                    generaActionListnerModificaOrdine(lavoro, mainView));

            if (lavoro.getOrdinamento() != null) {
                lavoro.setToDoList(ordinaToDoList(lavoro.getToDoList(), lavoro.getOrdinamento()));
            }
            for (ToDo todo : lavoro.getToDoList()) {
                if (mostraCompletati || !todo.isCompletato()) {
                    visualizzaToDo(todo, contenitoreToDoL, null);
                    haToDoLavoro = true;
                }
            }
        }
        mainView.getBaLav().setVisible(haToDoLavoro);
    }

    /**
     * Metodo per generare la bacheca università, mostra il pannello solo se sono
     * presenti dei todo e richiama i metodi per generare gli action listener
     */
    private void generaUniversita(Main mainView) {
        // Bacheca Università
        Bacheca universita = utenteAttuale.getUniversita();
        boolean haToDoUniversita = false;
        if (universita != null && universita.getToDoList() != null) {
            JPanel contenitoreToDoU = mainView.getContenitoreToDoU();
            contenitoreToDoU.removeAll();
            mainView.setDescrizioneUniText(universita.getDescrizione());

            rimuoviTuttiActionListener(mainView.getModificaDescrizioneUni());
            rimuoviTuttiActionListener(mainView.getOrdineUniButton());

            mainView.getModificaDescrizioneUni().addActionListener(
                    generaActionListnerModificaDescrizione(universita, mainView));
            mainView.getOrdineUniButton().addActionListener(
                    generaActionListnerModificaOrdine(universita, mainView));
            if (universita.getOrdinamento() != null) {
                universita.setToDoList(ordinaToDoList(universita.getToDoList(), universita.getOrdinamento()));
            }
            for (ToDo todo : universita.getToDoList()) {
                if (mostraCompletati || !todo.isCompletato()) {
                    visualizzaToDo(todo, contenitoreToDoU, null);
                    haToDoUniversita = true;
                }
            }
        }
        mainView.getBaUni().setVisible(haToDoUniversita);
    }

    /**
     * Controlla se nella bacheca ci sono ToDo scaduti o in scadenza oggi (o prima di oggi).
     * Se trova almeno un ToDo scaduto, lo visualizza nel contenitore specificato e ritorna true.
     *
     * @param bacheca
     * @param oggi
     * @param contenitoreToDoSca
     * @return true se alemno un ToDo è scaduto o in scadenza oggi, false altrimenti
     */
    private Boolean checkScaduti(Bacheca bacheca, Calendar oggi, JPanel contenitoreToDoSca) {
        if (bacheca != null && bacheca.getToDoList() != null) {
            for (ToDo todo : bacheca.getToDoList()) {
                if (todo.getScadenza() != null && todo.getScadenza().compareTo(oggi) <= 0) {
                    visualizzaToDo(todo, contenitoreToDoSca, null);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Genera e aggiorna la lista dei ToDo in scadenza oggi o già scaduti.
     * Controlla tutte le bacheche dell'utente (università, tempo libero, lavoro) e
     * i ToDo condivisi con l'utente, visualizzandoli nel contenitore dedicato.
     * Se non ci sono ToDo in scadenza, mostra un messaggio indicativo.
     *
     * @param mainView
     */
    private void generaInScadenza(Main mainView) {
        Bacheca universita = utenteAttuale.getUniversita();
        Bacheca tempoLibero = utenteAttuale.getTempoLibero();
        Bacheca lavoro = utenteAttuale.getLavoro();
        JPanel contenitoreToDoSca = mainView.getContenitoreTodoSca();
        contenitoreToDoSca.removeAll();
        Calendar oggi = Calendar.getInstance();
        oggi.set(Calendar.HOUR_OF_DAY, 0);
        oggi.set(Calendar.MINUTE, 0);
        oggi.set(Calendar.SECOND, 0);
        oggi.set(Calendar.MILLISECOND, 0);

        boolean haToDoUniversita = checkScaduti(universita, oggi, contenitoreToDoSca);
        boolean haToDoTempoLibero = checkScaduti(tempoLibero, oggi, contenitoreToDoSca);
        boolean haToDoLavoro = checkScaduti(lavoro, oggi, contenitoreToDoSca);
        ArrayList<Condivisione> arrayCondivisi = condivisioneDAO.getToDoPerUtenteCondiviso(utenteAttuale.getEmail());
        for (Condivisione condivisione : arrayCondivisi) {
            if (condivisione.getToDoCondiviso().getScadenza() != null
                    && condivisione.getToDoCondiviso().getScadenza().compareTo(oggi) <= 0) {
                visualizzaToDo(condivisione.getToDoCondiviso(), contenitoreToDoSca, condivisione);
            }
        }

        if (!haToDoUniversita && !haToDoTempoLibero && !haToDoLavoro) {
            // Soluzione più semplice: aggiungi direttamente il messaggio
            JLabel messaggioVuoto = new JLabel("Nessun todo in scadenza oggi");
            contenitoreToDoSca.add(messaggioVuoto);

            // Forza il refresh del contenitore
            contenitoreToDoSca.revalidate();
            contenitoreToDoSca.repaint();
        }
    }

    /**
     * Metodo per rimuovere i vecchi action listner altrimenti i metodi per generare
     * le bacheche alle loro pulsanti aprirebbero troppe volte i dialog
     */
    private void rimuoviTuttiActionListener(JButton button) {
        for (ActionListener al : button.getActionListeners()) {
            button.removeActionListener(al);
        }
    }

    private ArrayList<ToDo> ordinaToDoList(ArrayList<ToDo> toDoList, Ordinamento ordinamento) {
        switch (ordinamento) {
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
                Collections.sort(toDoList, Comparator.comparing(ToDo::getScadenza,
                        Comparator.nullsLast(Comparator.naturalOrder())));
                break;
            case SCADENZA_DESC:
                Collections.sort(toDoList, Comparator.comparing(ToDo::getScadenza,
                        Comparator.nullsFirst(Comparator.reverseOrder())));
                break;
        }
        return toDoList;
    }

    /**
     * Aggiorna l'interfaccia utente con i dati dell'utente attuale
     * 
     * @param mainView MainView della GUI
     */
    private void aggiornaInterfacciaUtente(Main mainView) {
        // Verifica se l'utente attuale è valido
        if (utenteAttuale != null) {
            mainView.setNomeText(utenteAttuale.getEmail());
            generaTempoLibero(mainView);
            generaLavoro(mainView);
            generaUniversita(mainView);
            generaInScadenza(mainView);
            generaCondiviso(mainView);
        }
    }

    /**
     * Ordina una lista di ToDo in base al criterio specificato.
     * I criteri di ordinamento disponibili sono:
     *   AZ: ordine alfabetico crescente per titolo
     *   ZA: ordine alfabetico decrescente per titolo
     *   CREAZIONE_ASC: ordine crescente per data di creazione
     *   CREAZIONE_DESC: ordine decrescente per data di creazione
     *   SCADENZA_ASC: ordine crescente per data di scadenza (null posizionati in coda)
     *   SCADENZA_DESC: ordine decrescente per data di scadenza (null posizionati in testa)
     *
     * @param mainView
     */
    private void generaCondiviso(Main mainView) {
        boolean haToDoCondivisi = false;
        ArrayList<Condivisione> arrayCondivisi = condivisioneDAO.getToDoPerUtenteCondiviso(utenteAttuale.getEmail());

        // Rimuovi tutti i contenuti una sola volta, all'inizio
        JPanel contenitoreToDoCon = mainView.getContenitoreToDoCon();
        contenitoreToDoCon.removeAll();

        for (Condivisione condivisione : arrayCondivisi) {
            if (mostraCompletati || !condivisione.getToDoCondiviso().isCompletato()) {
                visualizzaToDo(condivisione.getToDoCondiviso(), contenitoreToDoCon, condivisione);
                haToDoCondivisi = true;
            }
        }

        // Usa il pannello corretto per i ToDo condivisi (sostituisci getBaLav con il
        // nome corretto)
        mainView.getBaCon().setVisible(haToDoCondivisi);
    }

    /**
     * Metodo helper per configurare gli action listener comuni per la selezione di
     * colore, data e immagine
     * 
     * @param dialog il dialog su cui configurare gli action listener
     * @return un oggetto DialogReferences contenente i riferimenti alle selezioni
     */
    private DialogReferences configuraActionListenerSelezione(CreaToDo dialog) {
        DialogReferences references = new DialogReferences();

        dialog.getSfogliaButton().addActionListener(
                generaActionListenerSceltaImmagine(references.getImmagineScelta(), dialog));

        dialog.getColorButton().addActionListener(
                generaActionListenerSceltaColore(references.getColoreScelto(), dialog));

        dialog.getDataScadenzaButton().addActionListener(
                generaActionListenerSceltaScadenza(references.getDataScelto(), dialog));

        return references;
    }

    /**
     *  Imposta i campi del dialog di modifica con i valori presenti nel ToDo passato.
     *
     * @param todo
     * @param modificaTodoDialog
     * @param references
     */
    private void setOggettiPresenti(ToDo todo, CreaToDo modificaTodoDialog, DialogReferences references) {

        // Imposta il link se presente
        if (todo.getLink() != null) {
            modificaTodoDialog.getLinkField().setText(todo.getLink().toString());
        }

        // Imposta il colore se presente
        if (todo.getSfondo() != null) {
            references.getColoreScelto().set(todo.getSfondo());
        }

        // Imposta la data se presente
        if (todo.getScadenza() != null) {
            references.getDataScelto().set(todo.getScadenza()); // Imposta il Calendar nel riferimento
            SimpleDateFormat dateFormat = new SimpleDateFormat(FORMATODATA);
            modificaTodoDialog.getDataScadenzaButton().setText(dateFormat.format(todo.getScadenza().getTime()));
        }

        // Carica l'immagine se presente
        if (todo.getImmagine() != null) {
            references.getImmagineScelta().set(todo.getImmagine());
            caricaImmagine(new ImageIcon(todo.getImmagine()), modificaTodoDialog.getPreviewPanel(), modificaTodoDialog);
        }

        if (todo.getSfondo() != null) {
            // Cambia il colore del bottone per mostrare il colore scelto
            modificaTodoDialog.getColorButton().setForeground(todo.getSfondo());
            modificaTodoDialog.getColorButton().repaint();
            modificaTodoDialog.getColorButton().revalidate();
        }
    }

    /**
     * Crea e configura il dialog di modifica associato al pulsante di modifica.
     * @param modificaButton
     * @param todo
     */
    private void creazioneModificaDialog(@NotNull JButton modificaButton, ToDo todo) {
        // RIMUOVI gli ActionListener esistenti prima di aggiungerne uno nuovo
        for (ActionListener al : modificaButton.getActionListeners()) {
            modificaButton.removeActionListener(al);
        }
        
        modificaButton.addActionListener(e -> {
            // Crea un'istanza del pannello CreaToDo per la modifica
            CreaToDo modificaTodoDialog = new CreaToDo();
            modificaTodoDialog.setContentPane(modificaTodoDialog.getMainPanel());
            modificaTodoDialog.setLocationRelativeTo(view.getLogInView().getMainView().getMain());

            // Rimuovi ActionListener esistenti prima di configurare
            rimuoviTuttiActionListener(modificaTodoDialog.getSalvaButton());
            rimuoviTuttiActionListener(modificaTodoDialog.getChecklistButton());
            rimuoviTuttiActionListener(modificaTodoDialog.getSfogliaButton());
            rimuoviTuttiActionListener(modificaTodoDialog.getColorButton());
            rimuoviTuttiActionListener(modificaTodoDialog.getDataScadenzaButton());

            // Popola i campi con i dati del todo corrente
            modificaTodoDialog.getTitolo().setText(todo.getTitolo());
            modificaTodoDialog.getDescrizioneField().setText(todo.getDescrizione());

            // Usa il metodo helper
            DialogReferences references = configuraActionListenerSelezione(modificaTodoDialog);

            setOggettiPresenti(todo, modificaTodoDialog, references);

            ArrayList<JTextField> attivitaFieldsNuovo = new ArrayList<>();
            AtomicReference<JTextField> titoloCheckListRefNuovo = new AtomicReference<>();
            modificaTodoDialog.getChecklistButton().addActionListener(generaActionListenerAggiungiCheckList(
                    attivitaFieldsNuovo, titoloCheckListRefNuovo, modificaTodoDialog));

            if (todo.getChecklist() != null) {
                JPanel checklistPanel = new JPanel();
                checklistPanel.setLayout(new BoxLayout(checklistPanel, BoxLayout.Y_AXIS));

                JPanel titoloChecklist = new JPanel();
                titoloChecklist.setLayout(new BoxLayout(titoloChecklist, BoxLayout.X_AXIS));
                JTextField titoloCheckListField = new JTextField(20);
                SetPlaceHolder.setTP(titoloCheckListField, "Titolo CheckList", GestioneDarkMode.isDarkMode());
                titoloCheckListField.setText(todo.getChecklist().getNomeChecklist());
                titoloChecklist.add(titoloCheckListField);
                titoloCheckListRefNuovo.set(titoloCheckListField);
                JButton aggiungiAttivitaButton = new JButton("+");
                titoloChecklist.add(aggiungiAttivitaButton);
                JButton cancellaCheckListButton = new JButton("X");
                titoloChecklist.add(cancellaCheckListButton);
                titoloChecklist.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));

                checklistPanel.add(titoloChecklist);

                modificaTodoDialog.getChecklistButton().setEnabled(false);
                modificaTodoDialog.getChecklistButton().setText("V");
                modificaTodoDialog.getChecklistButton().setBackground(Color.GRAY);

                cancellaCheckListButton.addActionListener(cancellaCheckListEvent -> {
                    checklistPanel.removeAll();

                    modificaTodoDialog.getChecklistButton().setEnabled(true);
                    modificaTodoDialog.getChecklistButton().setText("Inserisci");
                    modificaTodoDialog.getChecklistButton().setBackground(Color.WHITE);
                    modificaTodoDialog.repaint();
                    modificaTodoDialog.pack();
                    modificaTodoDialog.revalidate();

                });

                aggiungiAttivitaButton.addActionListener(attivitaAddEvent -> {
                    JPanel attivitaPanel = new JPanel();
                    attivitaPanel.setLayout(new BoxLayout(attivitaPanel, BoxLayout.X_AXIS));
                    JTextField titoloAttivitaField = new JTextField(20);
                    SetPlaceHolder.setTP(titoloAttivitaField, TA, GestioneDarkMode.isDarkMode());
                    attivitaFieldsNuovo.add(titoloAttivitaField);

                    attivitaPanel.add(titoloAttivitaField);
                    JButton cancellaAttivitaButton = new JButton("-");
                    attivitaPanel.add(cancellaAttivitaButton);

                    cancellaAttivitaButton.addActionListener(cancellaAttivitaEvent -> {
                        attivitaFieldsNuovo.remove(titoloAttivitaField);
                        attivitaPanel.removeAll();
                        modificaTodoDialog.repaint();
                        modificaTodoDialog.pack();
                        modificaTodoDialog.revalidate();
                    });

                    checklistPanel.add(attivitaPanel);
                    modificaTodoDialog.repaint();
                    modificaTodoDialog.pack();
                    modificaTodoDialog.revalidate();

                });

                for (Attivita attivitaGiaPresente : todo.getChecklist().getAttivita()) {
                    JPanel attivitaPanel = new JPanel();
                    attivitaPanel.setLayout(new BoxLayout(attivitaPanel, BoxLayout.X_AXIS));
                    JTextField titoloAttivitaField = new JTextField(20);
                    SetPlaceHolder.setTP(titoloAttivitaField, TA, GestioneDarkMode.isDarkMode());
                    titoloAttivitaField.setText(attivitaGiaPresente.getNome());
                    attivitaFieldsNuovo.add(titoloAttivitaField);

                    attivitaPanel.add(titoloAttivitaField);
                    JButton cancellaAttivitaButton = new JButton("-");
                    attivitaPanel.add(cancellaAttivitaButton);

                    cancellaAttivitaButton.addActionListener(cancellaAttivitaEvent -> {
                        attivitaFieldsNuovo.remove(titoloAttivitaField);
                        attivitaPanel.removeAll();
                        modificaTodoDialog.repaint();
                        modificaTodoDialog.pack();
                        modificaTodoDialog.revalidate();
                    });

                    checklistPanel.add(attivitaPanel);
                    modificaTodoDialog.repaint();
                    modificaTodoDialog.pack();
                    modificaTodoDialog.revalidate();
                }

                modificaTodoDialog.getPannelloAggiungibile().add(checklistPanel);
                modificaTodoDialog.repaint();
                modificaTodoDialog.pack();
                modificaTodoDialog.revalidate();
            }

            // Determina la bacheca corrente del todo
            final String bachecaCorrente = getBachecaCorrente(todo);

            // Imposta la selezione della bacheca
            modificaTodoDialog.getBachecaBox().setSelectedItem(bachecaCorrente);

            modificaTodoDialog.getSalvaButton().addActionListener(generaActionListnerSalvaModifica(
                    modificaTodoDialog, todo, bachecaCorrente, attivitaFieldsNuovo, titoloCheckListRefNuovo,
                    references));

            // Mostra il dialog di modifica
            modificaTodoDialog.pack();
            modificaTodoDialog.setLocationRelativeTo(view.getLogInView().getMainView().getMain());
            modificaTodoDialog.setVisible(true);
        });
    }

    /**
     * Genera l'ActionListener per il pulsante Salva del dialog di modifica.
     * Gestisce la validazione e il salvataggio delle modifiche apportate al ToDo.
     *
     * @param modificaTodoDialog
     * @param todo
     * @param bachecaCorrente
     * @param attivitaFieldsNuovo
     * @param titoloCheckListRefNuovo
     * @param references
     * @return ActionListener per il pulsante Salva
     */
    private ActionListener generaActionListnerSalvaModifica(CreaToDo modificaTodoDialog, ToDo todo,
            String bachecaCorrente, ArrayList<JTextField> attivitaFieldsNuovo,
            AtomicReference<JTextField> titoloCheckListRefNuovo, DialogReferences references) {
        return _ -> {
            // Validazione del titolo
            String titoloVecchio = todo.getTitolo();
            String nuovoTitolo = modificaTodoDialog.getTitolo().getText();

            if (nuovoTitolo == null || nuovoTitolo.trim().isEmpty()) {
                JOptionPane.showMessageDialog(modificaTodoDialog,
                        "Inserisci un titolo valido per il ToDo",
                        ERRORMESSAGE, JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Aggiorna i dati del todo
            todo.setTitolo(nuovoTitolo);
            todo.setDescrizione(modificaTodoDialog.getDescrizioneField().getText());

            ArrayList<Attivita> attivitaDaSalvareNuovo = estraiAttivitaDaFields(attivitaFieldsNuovo);
        if (attivitaDaSalvareNuovo != null && !attivitaDaSalvareNuovo.isEmpty() && titoloCheckListRefNuovo.get() != null) {
            // Se la checklist esiste, aggiornala
            if (todo.getChecklist() != null) {
                todo.getChecklist().setNomeChecklist(titoloCheckListRefNuovo.get().getText());
                todo.getChecklist().setAttivita(attivitaDaSalvareNuovo);
            } else {
                // Se la checklist non esiste, creane una nuova
                Checklist nuovaChecklist = new Checklist(titoloCheckListRefNuovo.get().getText(), attivitaDaSalvareNuovo);
                todo.setChecklist(nuovaChecklist);
            }
        } else if (attivitaDaSalvareNuovo == null || attivitaDaSalvareNuovo.isEmpty()) {
            // Se non ci sono attività, rimuovi la checklist
            todo.setChecklist(null);
        }

            // Aggiorna il link se necessario
            String nuovoLink = modificaTodoDialog.getLinkField().getText();
            if (nuovoLink != null && !nuovoLink.trim().isEmpty()) {
                try {
                    todo.setLink(new URI(nuovoLink));
                } catch (URISyntaxException _) {
                    JOptionPane.showMessageDialog(modificaTodoDialog,
                            "Il link inserito non è valido. Formato corretto: https://esempio.com",
                            ERRORMESSAGE, JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                todo.setLink(null);
            }

            todo.setSfondo(references.getColoreScelto().get());
            todo.setImmagine(references.getImmagineScelta().get());
            todo.setScadenza(references.getDataScelto().get());

            // Gestisci il cambio di bacheca se necessario
            String nuovaBacheca = (String) modificaTodoDialog.getBachecaBox().getSelectedItem();
            if (!nuovaBacheca.equals(bachecaCorrente)) {
                gestisciTodoInBacheca(todo, bachecaCorrente, modificaTodoDialog, RIMUOVI);
                gestisciTodoInBacheca(todo, nuovaBacheca, modificaTodoDialog, AGGIUNGI);
            }

            // salvataggio modifiche nel db
            salvaTodoNelDatabase(todo, modificaTodoDialog, stringToBacheca(nuovaBacheca, modificaTodoDialog),
                    titoloVecchio);

            // Chiudi il dialog e aggiorna l'interfaccia
            modificaTodoDialog.dispose();
            aggiornaInterfacciaUtente(view.getLogInView().getMainView());
            view.revalidate();
            view.repaint();
        };
    }

    /**
     * Salva il ToDo nel database, aggiornandolo o creandolo a seconda che il titolo precedente sia presente.
     * Mostra un messaggio di errore in caso di problemi di salvataggio.
     *
     * @param todo
     * @param dialog
     * @param bachecaCorrente
     * @param titoloVecchio
     */
    private void salvaTodoNelDatabase(ToDo todo, JDialog dialog, Bacheca bachecaCorrente, String titoloVecchio) {
        try {
            if (titoloVecchio != null) {
                toDoDAO.modificaToDo(utenteAttuale.getEmail(), todo, bachecaCorrente, titoloVecchio);
            } else {
                toDoDAO.creaToDo(utenteAttuale.getEmail(), todo, bachecaCorrente);
            }
        } catch (Exception daoEx) {
            String operazione = titoloVecchio != null ? "modifica" : "salvataggio";
            JOptionPane.showMessageDialog(dialog,
                    "Errore nel " + operazione + " del ToDo nel database: " + daoEx.getMessage(),
                    ERRORMESSAGE, JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Converte una stringa rappresentante il nome della bacheca nel corrispondente oggetto Bacheca.
     * Imposta il titolo e l'ordinamento predefinito della bacheca.
     *
     * @param stringa
     * @param dialog
     * @return l'oggetto Bacheca corrispondente
     * @throws IllegalArgumentException se la stringa non corrisponde a nessuna bacheca valida
     */
    private Bacheca stringToBacheca(String stringa, JDialog dialog) {
        Bacheca bacheca;
        Titolo titolo;

        switch (stringa) {
            case TL -> {
                bacheca = utenteAttuale.getTempoLibero();
                titolo = Titolo.TEMPO_LIBERO;
            }
            case LAV -> {
                bacheca = utenteAttuale.getLavoro();
                titolo = Titolo.LAVORO;
            }
            case UNI -> {
                bacheca = utenteAttuale.getUniversita();
                titolo = Titolo.UNIVERSITA;
            }
            default -> {
                JOptionPane.showMessageDialog(dialog,
                        "Seleziona una bacheca valida",
                        ERRORMESSAGE, JOptionPane.ERROR_MESSAGE);
                throw new IllegalArgumentException("Bacheca non valida: " + stringa);
            }
        }

        // Imposta esplicitamente il titolo della bacheca
        bacheca.setTitolo(titolo);
        if (bacheca.getOrdinamento() == null) {
            bacheca.setOrdinamento(Ordinamento.AZ); // Imposta un ordinamento predefinito
        }

        return bacheca;
    }

    /**
     * Gestisce l'aggiunta o la rimozione di un ToDo da una determinata bacheca dell'utente.
     * @param todo
     * @param bacheca
     * @param dialog
     * @param operazione
     */
    private void gestisciTodoInBacheca(ToDo todo, String bacheca, JDialog dialog, String operazione) {
        switch (bacheca) {
            case TL -> {
                if (AGGIUNGI.equals(operazione)) {
                    utenteAttuale.getTempoLibero().getToDoList().add(todo);
                } else if (RIMUOVI.equals(operazione)) {
                    utenteAttuale.getTempoLibero().getToDoList().remove(todo);
                }
            }
            case LAV -> {
                if (AGGIUNGI.equals(operazione)) {
                    utenteAttuale.getLavoro().getToDoList().add(todo);
                } else if (RIMUOVI.equals(operazione)) {
                    utenteAttuale.getLavoro().getToDoList().remove(todo);
                }
            }
            case UNI -> {
                if (AGGIUNGI.equals(operazione)) {
                    utenteAttuale.getUniversita().getToDoList().add(todo);
                } else if (RIMUOVI.equals(operazione)) {
                    utenteAttuale.getUniversita().getToDoList().remove(todo);
                }
            }
            default -> {
                JOptionPane.showMessageDialog(dialog,
                        "Seleziona una bacheca valida",
                        ERRORMESSAGE, JOptionPane.ERROR_MESSAGE);
                throw new IllegalArgumentException("Bacheca non valida: " + bacheca);
            }
        }
    }

    /**
     * Restituisce il nome della bacheca in cui è contenuto il ToDo.
     * @param todo
     * @return il nome della bacheca o null se non è contenuta in nessuna
     */
    private @Nullable String getBachecaCorrente(ToDo todo) {
        final String bachecaCorrente;
        if (utenteAttuale.getTempoLibero().getToDoList().contains(todo)) {
            bachecaCorrente = TL;
        } else if (utenteAttuale.getLavoro().getToDoList().contains(todo)) {
            bachecaCorrente = LAV;
        } else if (utenteAttuale.getUniversita().getToDoList().contains(todo)) {
            bachecaCorrente = UNI;
        } else {
            bachecaCorrente = null;
        }
        return bachecaCorrente;
    }

    /**
     * Carica un'immagine in un pannello, ridimensionandola per mostrare un'anteprima mantenendo le proporzioni.
     *
     * @param originalIcon
     * @param pannelloImmagine
     * @param dialogo
     */
    private void caricaImmagine(ImageIcon originalIcon, JPanel pannelloImmagine, JDialog dialogo) {
        try {
            // Carica l'immagine per l'anteprima

            Image originalImage = originalIcon.getImage();

            // Definisci le dimensioni per l'anteprima
            int previewWidth = 200;
            int previewHeight = 150;

            // Calcola le dimensioni mantenendo le proporzioni
            int originalWidth = originalIcon.getIconWidth();
            int originalHeight = originalIcon.getIconHeight();
            double ratio = (double) originalWidth / originalHeight;

            int targetWidth;
            int targetHeight;
            if (ratio > 1) {
                // Immagine più larga che alta
                targetWidth = previewWidth;
                targetHeight = (int) (previewWidth / ratio);
            } else {
                // Immagine più alta che larga
                targetHeight = previewHeight;
                targetWidth = (int) (previewHeight * ratio);
            }

            // Ridimensiona e mostra l'anteprima
            Image resizedImage = originalImage.getScaledInstance(
                    targetWidth, targetHeight, Image.SCALE_SMOOTH);
            ImageIcon resizedIcon = new ImageIcon(resizedImage);

            // Mostra l'anteprima nel pannello
            pannelloImmagine.removeAll();
            JLabel previewLabel = new JLabel(resizedIcon);
            pannelloImmagine.add(previewLabel);
            pannelloImmagine.setVisible(true);
            pannelloImmagine.setBorder(BorderFactory.createTitledBorder("Anteprima immagine"));
            dialogo.pack();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dialogo,
                    "Errore nel caricamento dell'anteprima: " + ex.getMessage(),
                    ERRORMESSAGE, JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Crea un JButton per modificare un ToDo con icona e configurazione grafica.
     *
     * @param coloreTesto
     * @param todo
     * @return il JButton configurato
     */
    private JButton generaModificaButton(Color coloreTesto, ToDo todo) {
        // Pulsante modifica con icona di modifica (Unicode per pencil)
        JButton modificaButton = new JButton("✏");
        modificaButton.setFont(FONTICONE);
        modificaButton.setToolTipText("Modifica");
        modificaButton.setFocusPainted(false);
        modificaButton.setBorderPainted(false);
        modificaButton.setContentAreaFilled(false);
        modificaButton.setForeground(coloreTesto);
        modificaButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        creazioneModificaDialog(modificaButton, todo);
        return modificaButton;
    }

    /**
     * Crea un JButton per condividere un ToDo, mostrando il numero di condivisioni.
     *
     * @param coloreTesto
     * @param todo
     * @return il JButton configurato
     */
    private JButton generaShareButton(Color coloreTesto, ToDo todo) {
        JButton condividiButton = new JButton("📤");
        int conteggio = condivisioneDAO.getUtentiCondivisiPerToDo(utenteAttuale.getEmail(), todo.getTitolo()).size();
        if (conteggio > 0) {
            condividiButton.setText(conteggio + "📤");
        }
        condividiButton.setFont(new Font("Dialog", Font.PLAIN, 14));
        condividiButton.setToolTipText("Modifica");
        condividiButton.setFocusPainted(false);
        condividiButton.setBorderPainted(false);
        condividiButton.setContentAreaFilled(false);
        condividiButton.setForeground(coloreTesto);
        condividiButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        creazioneCondividiDialog(condividiButton, todo);
        return condividiButton;
    }

    /**
     * Crea un JButton per eliminare un ToDo o una sua condivisione.
     * Mostra un messaggio di conferma prima di procedere con l'eliminazione.
     *
     * @param coloreTesto
     * @param todo
     * @param condivisione
     * @return il JButton configurato con il relativo ActionListener
     */
    public JButton generaEliminaButton(Color coloreTesto, ToDo todo, Condivisione condivisione) {
        // Pulsante elimina con icona del cestino (Unicode per trash)
        JButton eliminaButton = new JButton("🗑");
        eliminaButton.setFont(FONTICONE);
        eliminaButton.setToolTipText("Elimina");
        eliminaButton.setFocusPainted(false);
        eliminaButton.setBorderPainted(false);
        eliminaButton.setContentAreaFilled(false);
        eliminaButton.setForeground(coloreTesto);
        eliminaButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        eliminaButton.addActionListener(e -> {
            String messaggioConferma = "Sei sicuro di voler eliminare questo ToDo?";

            int conferma = JOptionPane.showConfirmDialog(null,
                    messaggioConferma,
                    "Conferma eliminazione", JOptionPane.YES_NO_OPTION);

            if (conferma != JOptionPane.YES_OPTION) {
                return;
            }

            if (condivisione != null) {
                // Elimina solo la condivisione
                try {
                    condivisioneDAO.eliminaCondivisione(
                            condivisione.getCreatore(),
                            condivisione.getToDoCondiviso().getTitolo(),
                            utenteAttuale.getEmail());

                    // Aggiorna l'interfaccia utente
                    aggiornaInterfacciaUtente(view.getLogInView().getMainView());
                    view.revalidate();
                    view.repaint();

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null,
                            "Errore durante la rimozione della condivisione: " + ex.getMessage(),
                            ERRORMESSAGE, JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // Comportamento esistente per i ToDo normali
                boolean rimosso = false;

                if (utenteAttuale.getTempoLibero().getToDoList().remove(todo))
                    rimosso = true;
                if (utenteAttuale.getLavoro().getToDoList().remove(todo))
                    rimosso = true;
                if (utenteAttuale.getUniversita().getToDoList().remove(todo))
                    rimosso = true;

                if (rimosso) {
                    try {
                        toDoDAO.eliminaToDo(utenteAttuale.getEmail(), todo.getTitolo());
                        aggiornaInterfacciaUtente(view.getLogInView().getMainView());
                        view.revalidate();
                        view.repaint();
                    } catch (Exception daoEx) {
                        JOptionPane.showMessageDialog(null,
                                "Errore durante l'eliminazione nel database: " + daoEx.getMessage(),
                                ERRORMESSAGE, JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        return eliminaButton;
    }

    /**
     * Metodo che genera il codice swing per la gui partendo dal todo in memoria
     * 
     * @param todo            il todo da visualizzare
     * @param contenitoreToDo il contenitore a cui applicare il todo generato
     */
    private void visualizzaToDo(@NotNull ToDo todo, @NotNull JPanel contenitoreToDo, Condivisione condivisione) {

        // Crea un nuovo JPanel per il singolo ToDo
        JPanel todoPanel = new JPanel();
        todoPanel.setLayout(new BoxLayout(todoPanel, BoxLayout.Y_AXIS));

        // Gestione del colore di sfondo
        Color backgroundColor;
        if (todo.getSfondo() != null) {
            backgroundColor = todo.getSfondo();
        } else {
            backgroundColor = new Color(255, 255, 255);
        }
        Color coloreTesto = getContrasto(backgroundColor);
        todoPanel.setBackground(backgroundColor);

        JPanel titoloPanel = new JPanel();
        titoloPanel.setLayout(new BorderLayout());
        titoloPanel.setBackground(backgroundColor);

        JLabel titoloLabel = new JLabel(todo.getTitolo());
        titoloLabel.setForeground(coloreTesto);
        titoloLabel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        titoloPanel.add(titoloLabel, BorderLayout.CENTER);

        // Pannello per i pulsanti a destra
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 2, 0));
        buttonsPanel.setBackground(backgroundColor);

        // Aggiungi i pulsanti al pannello dei pulsanti
        buttonsPanel.add(generaModificaButton(coloreTesto, todo));
        buttonsPanel.add(generaEliminaButton(coloreTesto, todo, condivisione));
        buttonsPanel.add(generaShareButton(coloreTesto, todo));

        // Aggiungi il pannello dei pulsanti al pannello del titolo
        titoloPanel.add(buttonsPanel, BorderLayout.EAST);

        // Aggiungi il pannello del titolo al pannello principale
        todoPanel.add(titoloPanel);

        // Aggiungi un bordo al pannello principale
        todoPanel.setBorder(BorderFactory.createLineBorder(coloreTesto));

        // panel con label e checkbox
        JPanel descrizionePanel = new JPanel();
        descrizionePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        descrizionePanel.setBackground(backgroundColor);

        // Crea e aggiungi una JLabel con il titolo del ToDo
        JLabel descrizioneLabel = new JLabel(todo.getDescrizione());
        descrizioneLabel.setForeground(coloreTesto);

        // creazione checkbox
        JCheckBox checkboxTodo = new JCheckBox();
        checkboxTodo.setSelected(todo.isCompletato());
        checkboxTodo.setBackground(backgroundColor);
        checkboxTodo.setForeground(coloreTesto);

        checkboxTodo.addItemListener(e -> {

            todo.setCompletato(checkboxTodo.isSelected());
            toDoDAO.completaToDo(utenteAttuale.getEmail(), todo.getTitolo(), todo.isCompletato());
            if (todo.getChecklist() != null && todo.getChecklist().getAttivita() != null) {
                for (Attivita att : todo.getChecklist().getAttivita()) {
                    att.setCompletata(checkboxTodo.isSelected());
                    toDoDAO.completaAtt(utenteAttuale.getEmail(),todo.getTitolo(),att.isCompletata(),att.getNome());
                }
            }
            aggiornaInterfacciaUtente(view.getLogInView().getMainView());
            view.revalidate();
            view.repaint();
        });

        // aggiunta al panel descrizione
        descrizionePanel.add(checkboxTodo);
        descrizionePanel.add(descrizioneLabel);

        // aggiunta al panel todo
        todoPanel.add(descrizionePanel);

        // aggiunta checklist
        if (todo.getChecklist() != null) {
            todoPanel.add(generaPanelCheckList(todo, backgroundColor, coloreTesto));
        }
        // fine checklist

        // Dopo l'aggiunta della checklist, aggiungi il link se esiste
        if (todo.getLink() != null) {
            todoPanel.add(generaPanelLink(todo, backgroundColor, coloreTesto, todoPanel));
        }

        // Aggiungi la data di scadenza
        if (todo.getScadenza() != null) {
            todoPanel.add(generaPanelScadenza(todo, backgroundColor, coloreTesto));
        }

        if (todo.getImmagine() != null) {
            todoPanel.add(generaPanelImmagine(todo, backgroundColor));
        }

        if (condivisione != null) {
            todoPanel.add(generaPanelCondiviso(condivisione, coloreTesto));
        }

        todoPanel.revalidate();
        todoPanel.repaint();

        // aggiunta al contenitore
        contenitoreToDo.add(todoPanel);

        contenitoreToDo.revalidate();
        contenitoreToDo.repaint();
    }

    /**
     * Genera un JPanel contenente la checklist di un ToDo con le checkbox per ogni attività.
     *  * Ogni checkbox aggiorna lo stato dell'attività e del ToDo nel database e cambia il colore
     *  * del pannello se tutte le attività sono completate.
     *
     * @param todo
     * @param backgroundColor
     * @param coloreTesto
     * @return un JPanel con la checklist completa
     */
    private @NotNull JPanel generaPanelCheckList(ToDo todo, Color backgroundColor, Color coloreTesto) {
        JPanel checklistPanel = new JPanel();
        checklistPanel.setLayout(new BoxLayout(checklistPanel, BoxLayout.Y_AXIS));
        checklistPanel.setBorder(BorderFactory.createTitledBorder(todo.getChecklist().getNomeChecklist()));
        checklistPanel.setBackground(backgroundColor);

        // Crea checkbox + label per ogni attività
        for (Attivita att : todo.getChecklist().getAttivita()) {
            JPanel attivitaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            attivitaPanel.setBackground(backgroundColor);

            JCheckBox checkBoxAtt = new JCheckBox();
            checkBoxAtt.setSelected(att.isCompletata());
            checkBoxAtt.setBackground(backgroundColor);
            checkBoxAtt.setForeground(coloreTesto);

            JLabel attLabel = new JLabel(att.getNome());
            attLabel.setForeground(coloreTesto);

            // Listener sulla checkbox attività
            checkBoxAtt.addItemListener(e -> {
                att.setCompletata(checkBoxAtt.isSelected());
                toDoDAO.completaAtt(utenteAttuale.getEmail(),todo.getTitolo(),checkBoxAtt.isSelected(), att.getNome());
                // Ricontrolla se tutte le attività sono completate
                boolean tutteCompletate = todo.getChecklist().getAttivita().stream()
                        .allMatch(Attivita::isCompletata);

                // Aggiorna stato checklist
                todo.getChecklist().setCompletata(tutteCompletate);

                // Aggiorna stato ToDo
                todo.setCompletato(tutteCompletate);
                toDoDAO.completaToDo(utenteAttuale.getEmail(), todo.getTitolo(), tutteCompletate);

                // Aggiorna colore e bordo
                if (tutteCompletate) {
                    checklistPanel.setBackground(Color.GREEN);
                    TitledBorder titledBorder = BorderFactory.createTitledBorder(
                            BorderFactory.createLineBorder(Color.GREEN, 2),
                            todo.getChecklist().getNomeChecklist());
                    titledBorder.setTitleColor(Color.GREEN.darker());
                    checklistPanel.setBorder(titledBorder);
                } else {
                    checklistPanel.setBackground(backgroundColor);
                    checklistPanel.setBorder(BorderFactory.createTitledBorder(todo.getChecklist().getNomeChecklist()));
                }

                aggiornaInterfacciaUtente(view.getLogInView().getMainView());
                view.revalidate();
                view.repaint();
            });

            attivitaPanel.add(checkBoxAtt);
            attivitaPanel.add(attLabel);
            checklistPanel.add(attivitaPanel);
        }

        // Verifica iniziale dello stato checklist una sola volta
        boolean tutteCompletateInit = todo.getChecklist().getAttivita().stream()
                .allMatch(Attivita::isCompletata);

        todo.getChecklist().setCompletata(tutteCompletateInit);
        todo.setCompletato(tutteCompletateInit);
        toDoDAO.completaToDo(utenteAttuale.getEmail(), todo.getTitolo(), tutteCompletateInit);

        if (tutteCompletateInit) {
            checklistPanel.setBackground(Color.GREEN);
            TitledBorder titledBorder = BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(Color.GREEN, 2),
                    todo.getChecklist().getNomeChecklist());
            titledBorder.setTitleColor(Color.GREEN.darker());
            checklistPanel.setBorder(titledBorder);
        }

        checklistPanel.revalidate();
        checklistPanel.repaint();
        return checklistPanel;
    }

    /**
     * Genera un JPanel contenente un link cliccabile associato al ToDo.
     * Il link viene mostrato in forma abbreviata e, se cliccato, apre il browser predefinito.
     *
     * @param todo
     * @param backgroundColor
     * @param coloreTesto
     * @param todoPanel
     * @return un JPanel con il link cliccabile
     */
    private static @NotNull JPanel generaPanelLink(ToDo todo, Color backgroundColor, Color coloreTesto,
            JPanel todoPanel) {
        JPanel uriPanel = new JPanel();
        uriPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        uriPanel.setBackground(backgroundColor);

        JLabel uriLabel = new JLabel("Link: ");
        uriLabel.setForeground(coloreTesto);

        String fullUrl = todo.getLink().toString();
        String visibleText;
        try {
            URI uri = todo.getLink();
            String host = uri.getHost();
            visibleText = (host != null) ? host : fullUrl;
        } catch (Exception e) {
            visibleText = fullUrl;
        }
        // Accorcia il link in base al numero massimo di caratteri da visualizzare
        int maxChars = 25; // imposta quanti caratteri vuoi mostrare
        if (visibleText.length() > maxChars) {
            visibleText = visibleText.substring(0, maxChars);
        }

        JLabel linkLabel = new JLabel("<html><a href=''>" + visibleText + "..." + "</a></html>");
        linkLabel.setForeground(Color.BLUE);
        linkLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        linkLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().browse(new URI(fullUrl));

                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(todoPanel,
                                "Impossibile aprire il link: " + ex.getMessage(),
                                ERRORMESSAGE, JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        uriPanel.add(uriLabel);
        uriPanel.add(linkLabel);
        return uriPanel;
    }

    /**
     * Genera un JPanel contenente l'immagine associata al ToDo.
     * L'immagine viene ridimensionata mantenendo le proporzioni con altezza fissa di 100px.
     * @param todo
     * @param backgroundColor
     * @return un JPanel con l'immagine ridimensionata
     */
    private static @NotNull JPanel generaPanelImmagine(@NotNull ToDo todo, Color backgroundColor) {
        JPanel immaginePanel = new JPanel();
        immaginePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        immaginePanel.setBackground(backgroundColor);

        URL percorsoImmagine = todo.getImmagine();
        ImageIcon iconaOriginale = new ImageIcon(percorsoImmagine);
        Image immagine = iconaOriginale.getImage();

        // Ottieni le dimensioni originali
        int larghezzaOriginale = iconaOriginale.getIconWidth();
        int altezzaOriginale = iconaOriginale.getIconHeight();

        // Calcola la nuova larghezza mantenendo le proporzioni
        int altezzaDesiderata = 100;
        int nuovaLarghezza = (int) (larghezzaOriginale * ((double) altezzaDesiderata / altezzaOriginale));

        // Ridimensiona l'immagine mantenendo le proporzioni
        Image immagineRidimensionata = immagine.getScaledInstance(nuovaLarghezza, altezzaDesiderata,
                Image.SCALE_SMOOTH);
        ImageIcon iconaRidimensionata = new ImageIcon(immagineRidimensionata);

        JLabel labelImmagine = new JLabel(iconaRidimensionata);

        immaginePanel.add(labelImmagine);
        return immaginePanel;
    }

    /**
     * Genera un JPanel contenente la data di scadenza del ToDo.
     * Se la scadenza è passata, la data viene mostrata in rosso e in grassetto.
     *
     * @param todo
     * @param backgroundColor
     * @param coloreTesto
     * @return un JPanel con la data di scadenza formatta e colorata
     */
    private static @NotNull JPanel generaPanelScadenza(@NotNull ToDo todo, Color backgroundColor, Color coloreTesto) {
        JPanel scadenzaPanel = new JPanel();
        scadenzaPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        scadenzaPanel.setBackground(backgroundColor);

        // Formatta la data
        SimpleDateFormat dateFormat = new SimpleDateFormat(FORMATODATA);
        String dataFormattata = dateFormat.format(todo.getScadenza().getTime());

        JLabel scadenzaLabel = new JLabel("Scadenza: ");
        JLabel dataLabel = new JLabel(dataFormattata);
        dataLabel.setForeground(coloreTesto);
        scadenzaLabel.setForeground(coloreTesto);

        // Controlla se la data di scadenza è passata
        Calendar oggi = Calendar.getInstance();
        oggi.set(Calendar.HOUR_OF_DAY, 0);
        oggi.set(Calendar.MINUTE, 0);
        oggi.set(Calendar.SECOND, 0);
        oggi.set(Calendar.MILLISECOND, 0);

        // Imposta il colore rosso se la scadenza è già passata

        if (todo.getScadenza().before(oggi)) {
            todo.setScaduto(true);
        }
        if (todo.isScaduto()) {
            dataLabel.setForeground(Color.RED);
            dataLabel.setFont(new Font(dataLabel.getFont().getName(), Font.BOLD, dataLabel.getFont().getSize()));
        }

        scadenzaPanel.add(scadenzaLabel);
        scadenzaPanel.add(dataLabel);
        return scadenzaPanel;
    }

    /**
     * Genera un JPanel che mostra informazioni sulla condivisione di un ToDo.
     * Viene mostrato il nome del creatore del ToDo condiviso.
     *
     * @param condivisione
     * @param coloreTesto
     * @return un JPanel con le informazioni sulla condivisione
     */
    public JPanel generaPanelCondiviso(Condivisione condivisione, Color coloreTesto) {
        JPanel condivisionePanel = new JPanel();
        condivisionePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        condivisionePanel.setBackground(condivisione.getToDoCondiviso().getSfondo());
        JLabel condivisoDaLabel = new JLabel("Condiviso da: ");
        condivisoDaLabel.setForeground(coloreTesto);
        JLabel condivisoDa = new JLabel(condivisione.getCreatore());
        condivisoDa.setForeground(Color.BLUE);
        condivisionePanel.add(condivisoDaLabel);
        condivisionePanel.add(condivisoDa);
        return condivisionePanel;
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

        // Configurazione dei componenti dopo l'inizializzazione
        view.getLogInView().setupComponents();
        view.getRegisterView().setupComponents();

        view.getLogInButton().addActionListener(e -> {
            this.mostraPanel(view.getLogInView().getMainLogIn());

            LogIn loginView = view.getLogInView();
            loginView.getPassDime().addActionListener(ev -> gestisciPassDime(loginView));
            loginView.getBack().addActionListener(ev -> mostraPanel(view.getScelta()));
            // Gestione click su "entra"
            loginView.getEntra().addActionListener(ev -> gestisciLogin(loginView));
        });
        view.getRegistratiButton().addActionListener(e -> {
            this.mostraPanel(view.getRegisterView().getMainRegistrazione());

            Register registerView = view.getRegisterView();
            registerView.getBackButton().addActionListener(ev -> mostraPanel(view.getScelta()));
            // Listener per il pulsante "Registrati"
            registerView.getRegistratiButton().addActionListener(ev -> gestisciRegistrazione(registerView));
        });

    }

    /**
     * Metodo per scegliere se usare il testo chiaro o scuro in base allo sfondo di
     * personalizzato di un todo
     * 
     * @param background il colore di sfondo usato
     * @return Il colore da utilizzare nel testo
     */
    private Color getContrasto(@NotNull Color background) {
        // Formula per calcolare la luminosità del colore
        // Basata sulla percezione dell'occhio umano (0.299R + 0.587G + 0.114B)
        double luminance = (0.299 * background.getRed() +
                0.587 * background.getGreen() +
                0.114 * background.getBlue()) / 255;

        // Se la luminosità è superiore a 0.5, usare testo scuro, altrimenti testo
        // chiaro
        return luminance > 0.5 ? Color.BLACK : Color.WHITE;
    }

    /**
     * Gestisce il login dell'utente.
     * Controlla se i campi sono compilati e verifica le credenziali.
     *
     * @param loginView la vista di login
     */
    private void gestisciLogin(@NotNull LogIn loginView) {
        String email = loginView.getEmailText();
        String password = loginView.getPasswordText();

        if (email.isEmpty() || password.isEmpty() ||
                email.equals("Email") || password.equals("Password")) {
            JOptionPane.showMessageDialog(loginView, "Compila tutti i campi.", ATTENZIONE,
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Verifica le credenziali
        try {
            if (utenteDAO.loginValido(email, password)) {
                utenteAttuale = new Utente(email, password, new Bacheca(), new Bacheca(), new Bacheca());
                this.importaBacheca();
                this.mostraMain();
            } else {
                JOptionPane.showMessageDialog(loginView,
                        "Credenziali non valide.",
                        ATTENZIONE,
                        JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {

            JOptionPane.showMessageDialog(loginView,
                    e.getMessage(),
                    ERRORMESSAGE, JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Importa le bacheche dell'utente attuale dal database.
     * Carica le bacheche per i titoli TEMPO_LIBERO, LAVORO e UNIVERSITA,
     * quindi assegna le relative liste di ToDo all'utente attuale.
     * Aggiorna inoltre titolo, descrizione e ordinamento di ciascuna bacheca.
     * Se una bacheca o la sua lista ToDo non sono presenti, viene assegnata una lista vuota.
     */
    private void importaBacheca() {
        Bacheca b1 = toDoDAO.caricaBacheca(utenteAttuale.getEmail(), Titolo.TEMPO_LIBERO);
        Bacheca b2 = toDoDAO.caricaBacheca(utenteAttuale.getEmail(), Titolo.LAVORO);
        Bacheca b3 = toDoDAO.caricaBacheca(utenteAttuale.getEmail(), Titolo.UNIVERSITA);
        List<ToDo> tempoLibero = (b1 != null && b1.getToDoList() != null) ? b1.getToDoList() : new ArrayList<>();
        List<ToDo> lavoro = (b2 != null && b2.getToDoList() != null) ? b2.getToDoList() : new ArrayList<>();
        List<ToDo> universita = (b3 != null && b3.getToDoList() != null) ? b3.getToDoList() : new ArrayList<>();

        // Assegna le liste alle bacheche dell'utente attuale
        utenteAttuale.getTempoLibero().setToDoList(tempoLibero);
        utenteAttuale.getLavoro().setToDoList(lavoro);
        utenteAttuale.getUniversita().setToDoList(universita);

        utenteAttuale.getTempoLibero().setTitolo(Titolo.TEMPO_LIBERO);
        utenteAttuale.getLavoro().setTitolo(Titolo.LAVORO);
        utenteAttuale.getUniversita().setTitolo(Titolo.UNIVERSITA);

        utenteAttuale.getTempoLibero()
                .setDescrizione(bachecaDAO.getDescrizioneBacheca(utenteAttuale.getEmail(), Titolo.TEMPO_LIBERO));
        utenteAttuale.getTempoLibero()
                .setOrdinamento(bachecaDAO.getOrdinamentoBacheca(utenteAttuale.getEmail(), Titolo.TEMPO_LIBERO));

        utenteAttuale.getLavoro()
                .setDescrizione(bachecaDAO.getDescrizioneBacheca(utenteAttuale.getEmail(), Titolo.LAVORO));
        utenteAttuale.getLavoro()
                .setOrdinamento(bachecaDAO.getOrdinamentoBacheca(utenteAttuale.getEmail(), Titolo.LAVORO));

        utenteAttuale.getUniversita()
                .setDescrizione(bachecaDAO.getDescrizioneBacheca(utenteAttuale.getEmail(), Titolo.UNIVERSITA));
        utenteAttuale.getUniversita()
                .setOrdinamento(bachecaDAO.getOrdinamentoBacheca(utenteAttuale.getEmail(), Titolo.UNIVERSITA));

    }

    /**
     * Gestisce la registrazione dell'utente.
     * Controlla se i campi sono compilati correttamente e registra il nuovo utente.
     *
     * @param registerView la vista di registrazione
     */
    private void gestisciRegistrazione(@NotNull Register registerView) {
        String email = registerView.getEmailText();
        String password = registerView.getPasswordText();
        String confermaPassword = registerView.getConfermaPasswordText();

        if (email.isEmpty() || password.isEmpty() || confermaPassword.isEmpty() ||
                email.equals("Email") || password.equals("Password") || confermaPassword.equals("Conferma password")) {
            JOptionPane.showMessageDialog(registerView, "Compila tutti i campi.", ATTENZIONE,
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!password.equals(confermaPassword)) {
            JOptionPane.showMessageDialog(registerView, "Le password non corrispondono.", ERRORMESSAGE,
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Registrazione del nuovo utente con bacheche vuote
        try {
            if (utenteDAO.loginValido(email, password)) {
                JOptionPane.showMessageDialog(registerView,
                        "Utente già registrato!",
                        ATTENZIONE,
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            utenteDAO.registraUtente(email, password);

            JOptionPane.showMessageDialog(registerView,
                    "Registrazione avvenuta con successo! Effettua il LogIn",
                    "Successo",
                    JOptionPane.INFORMATION_MESSAGE);
            mostraPanel(view.getLogInView().getMainLogIn());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(registerView,
                    "Errore durante la registrazione: " + ex.getMessage(),
                    ERRORMESSAGE, JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Aggiunge un ActionListener al bottone di condivisione che apre un dialog per
     * selezionare gli utenti con cui condividere un ToDo.
     * @param condiviButton
     * @param todo
     */
    private void creazioneCondividiDialog(JButton condiviButton, ToDo todo) {
        // RIMUOVI gli ActionListener esistenti
        for (ActionListener al : condiviButton.getActionListeners()) {
            condiviButton.removeActionListener(al);
        }
        
        condiviButton.addActionListener(e -> {
            ListaUtenti listaUtenti = new ListaUtenti();
            JDialog condividiDialog = new JDialog();
            condividiDialog.setTitle("Condividi ToDo");
            condividiDialog.setContentPane(listaUtenti.getCondiviPanel());
            condividiDialog.setModal(true);
            condividiDialog.setSize(350, 450);
            condividiDialog.setLocationRelativeTo(view.getLogInView().getMainView().getMain());

            // Assicurati che il ToDo abbia un'email utente impostata
            if (todo.getEmailUtente() == null) {
                todo.setEmailUtente(utenteAttuale.getEmail());
            }
            ArrayList<JCheckBox> utentiCb = new ArrayList<>();

            ArrayList<Utente> utenti = utenteDAO.getUtentiAll();
            generaUtentiCondivisibili(listaUtenti, utenti, todo,utentiCb); // Passa todo come parametro
            //Listener per la barra di ricerca
            listaUtenti.getCercaField().getDocument().addDocumentListener(new DocumentListener() {
                private void aggiornaFiltro(){
                    String filtro = listaUtenti.getCercaField().getText().toLowerCase();
                    for (JCheckBox utenteCb : utentiCb) {
                        utenteCb.setVisible(utenteCb.getText().toLowerCase().contains(filtro));
                    }
                    listaUtenti.getUtentiPanel().revalidate();
                    listaUtenti.getUtentiPanel().repaint();
                }
                public void insertUpdate(DocumentEvent e) {aggiornaFiltro();}
                public void removeUpdate(DocumentEvent e) {aggiornaFiltro();}
                public void changedUpdate(DocumentEvent e) {aggiornaFiltro();}
            });

            listaUtenti.getCondivButton().addActionListener(ev -> {
                List<String> selezionati = getUtentiSelezionati(listaUtenti,utentiCb);
                for (String email : selezionati) {
                    condivisioneDAO.aggiungiCondivisione(todo.getEmailUtente(), todo.getTitolo(), email);
                }
                condividiDialog.dispose();
                mostraMain();
            });
            condividiDialog.setVisible(true);
        });
    }

    /**
     * Gestisce il processo di reset della password tramite dialog in
     * interfaccia di login.
     *
     * Aggiunge un ActionListener al bottone di reset password che apre un dialog
     * per inserire email e nuova password, verifica i dati, e aggiorna la
     * password dell'utente nel database.
     *
     * @param loginView
     */
    private void gestisciPassDime(@NotNull LogIn loginView) {
        loginView.getPassDime().addActionListener(e -> {
            ResetPassword resetDialog = new ResetPassword();
            
            // Rimuovi eventuali action listener esistenti prima di aggiungerne uno nuovo
            for (ActionListener al : resetDialog.getBottoneReset().getActionListeners()) {
                resetDialog.getBottoneReset().removeActionListener(al);
            }
            
            resetDialog.getBottoneReset().addActionListener(ex -> {
                String newPassword = String.valueOf(resetDialog.getPassword().getPassword());
                String email = resetDialog.getEmail().getText();

                // controllo campi vuoti
                if (email.isEmpty() || newPassword.isEmpty()) {
                    JOptionPane.showMessageDialog(null,
                            "I campi sono vuoti!",
                            ATTENZIONE, JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // controllo corrispondenza password
                if (!newPassword.equals(resetDialog.getConfermaPasswordText())) {
                    JOptionPane.showMessageDialog(null,
                            "Le password non coincidono",
                            ATTENZIONE, JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // recupera utente dal db
                Utente utente = utenteDAO.trovaUtenteDaMail(email);

                // controllo utente nullo
                if (utente == null) {
                    JOptionPane.showMessageDialog(null,
                            "Non esiste un utente con questa email",
                            ATTENZIONE, JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // aggiorna la password nel db
                if (utenteDAO.aggiornaPassword(email, newPassword)) {
                    utente.setPassword(newPassword);
                    this.utenteAttuale = utente;
                    resetDialog.dispose();
                    resetDialog.setVisible(false);

                    JOptionPane.showMessageDialog(null,
                            "Password aggiornata con successo",
                            ATTENZIONE, JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null,
                            "Errore durante l'aggiornamento della password",
                            ERRORMESSAGE, JOptionPane.ERROR_MESSAGE);
                }
            });

            resetDialog.setVisible(true); // Sposta qui il setVisible
        });
    }

    /**
     * Popola il pannello della lista utenti con gli utenti disponibili per la
     * condivisione di un ToDo, escludendo l'utente attuale e quelli con cui il ToDo
     * è già condiviso.
     *
     * @param listaUtenti
     * @param utenti
     * @param todo
     */
    public void generaUtentiCondivisibili(ListaUtenti listaUtenti, List<Utente> utenti, ToDo todo,ArrayList<JCheckBox> utentiCb) {
        listaUtenti.getUtentiPanel().removeAll();
        utentiCb.clear();

        int rows = Math.max(utenti.size(), 1);
        listaUtenti.getUtentiPanel().setLayout(new GridLayout(rows, 1, 5, 5));

        String emailUtenteAttuale = (utenteAttuale != null) ? utenteAttuale.getEmail() : null;

        // Ottieni la lista di utenti con cui il ToDo è già stato condiviso
        String emailAutore = (todo.getEmailUtente() != null) ? todo.getEmailUtente() : emailUtenteAttuale;
        ArrayList<String> utentiGiaCondivisi = condivisioneDAO.getUtentiCondivisiPerToDo(emailAutore, todo.getTitolo());

        for (Utente u : utenti) {
            // Verifica che l'utente corrente non sia nullo, che entrambe le email siano
            // valide,
            // e che il ToDo non sia già stato condiviso con questo utente
            if (u != null && u.getEmail() != null && emailUtenteAttuale != null
                    && !u.getEmail().equals(emailUtenteAttuale)
                    && !utentiGiaCondivisi.contains(u.getEmail())) {
                JCheckBox cb = new JCheckBox(u.getEmail());
                utentiCb.add(cb);
                listaUtenti.getUtentiPanel().add(cb);
            }
        }

        listaUtenti.getUtentiPanel().revalidate();
        listaUtenti.getUtentiPanel().repaint();
    }

    /**
     * Ottiene la lista di email degli utenti selezionati nella lista utenti.
     * @param listaUtenti
     * @return Lista di email degli utenti selezionati
     */
    public List<String> getUtentiSelezionati(ListaUtenti listaUtenti,ArrayList<JCheckBox> utentiCb) {
        List<String> selezionati = new ArrayList<>();
        for (JCheckBox cb : utentiCb) {
            if (cb.isSelected()) {
                selezionati.add(cb.getText());
            }
        }
        return selezionati;
    }

    /**
     * Punto di ingresso principale dell'applicazione.
     * Applica lo stile e inizializza la vista principale.
     *
     * @param args Argomenti da linea di comando (non utilizzati).
     */
    public static void main(String[] args) {
        StileSwing.applicaStile();
        SwingUtilities.invokeLater(() -> {
            Scelta view = new Scelta();
            new Controller(view);
        });
    }
}