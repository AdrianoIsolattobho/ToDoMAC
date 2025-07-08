package controller;

import gui.*;

import implementazioniPostgresDAO.ToDoImplementazionePostgresDAO;
import implementazioniPostgresDAO.UtenteImplementazionePostgresDAO;
import model.*;
import org.jetbrains.annotations.NotNull;
import dao.ToDoDAO;
import dao.UtenteDAO;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.awt.Image;
import java.util.concurrent.atomic.AtomicReference;

import static java.nio.file.Files.newInputStream;


/**
 *  Controller che gestisce tutte le interazioni tra modello e view
 *  come nel paradigma Model-View-Controller (MVC)
 *  o Boundary-Control-Entity (BCE)
 */
public class Controller {

    private Scelta view;
    private boolean mostraCompletati=false;
    private Utente utenteAttuale;
    private ToDoDAO toDoDAO = new ToDoImplementazionePostgresDAO();
    private UtenteDAO utenteDAO = new UtenteImplementazionePostgresDAO();

    //costanti stringhe per aggraziarsi SonarQube
    private static final String ERRORMESSAGE = "Errore";
    private static final String FORMATODATA = "dd/MM/yyyy";
    private static final String TA ="Titolo attività";
    private static final String TL ="Tempo libero";
    private static final String LAV= "Lavoro";
    private static final String UNI= "Università";
    private static final String ATTENZIONE="Attenzione";

    /**
     * Metodo helper che esegue delle funzioni di base per mostrare al meglio un pannello
     * @param scelto il pannello da mostrare
     */
    private void mostraPanel(JPanel scelto){
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

        //action listener della aggiunta di un nuovo todo
        mainView.getAggiungiToDo().addActionListener(e -> aggiuntaTodo());

        //action listener per mostrare i todo già completati
        mainView.getMostraCompletati().addActionListener(
                e-> {this.mostraCompletati= !this.mostraCompletati;
                    mainView.getMostraCompletati().setText(mostraCompletati ? "Mostra senza completati" : "Mostra tutti");
                aggiornaInterfacciaUtente(mainView);}
        );

        //action listener per uscire dal programma
        mainView.getEsci().addActionListener(e->mostraPanel(view.getScelta()));

    }

    /**
     * Metodo estratto da AggiuntaTodo e usato anche in ModificaTodo per salvare l'immagine selezionata
     * ActionListener per la selezione dell'immagine
     * @param immagineScelta riferimento dell'immagine selezionata
     * @param creaTodoDialog il dialog da cui viene chiamato il metodo
     */
    private ActionListener generaActionListenerSceltaImmagine(AtomicReference<URL> immagineScelta, CreaToDo creaTodoDialog){
        return _-> {
            //utilizziamo la libreria JFile Chooser che ci permette di scegliere i file
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Seleziona un'immagine");

            // Filtro per mostrare solo file di immagine
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "Immagini (JPG, PNG, GIF)", "jpg", "jpeg", "png", "gif");
            fileChooser.setFileFilter(filter);

            //check della validità del file, se il file è valido chiama caricaImmagine che ne mostra una preview
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
                    BufferedImage image = ImageIO.read(newInputStream(selectedFile.toPath()));
                    caricaImmagine(new ImageIcon(image),
                            creaTodoDialog.getPreviewPanel(), creaTodoDialog);
                    immagineScelta.set(selectedFile.toURI().toURL());
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(creaTodoDialog,
                            "Errore durante il caricamento dell'immagine: " + e.getMessage(),
                            ERRORMESSAGE, JOptionPane.ERROR_MESSAGE);
                }
            }
        };
    }

    /**
     * Metodo estratto da AggiuntaTodo e usato anche in ModificaTodo per salvare il colore
     * ActionListener per la selezione del colore
     * @param coloreScelto riferimento al colore selezionato
     * @param creaTodoDialog il dialog da cui viene chiamato il metodo
     */
    private ActionListener generaActionListenerSceltaColore(AtomicReference<Color> coloreScelto,CreaToDo creaTodoDialog){
        return _->{
            //usiamo la funzione base JColorChooser che ci permette di scegliere il colore
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
     * Metodo estratto da AggiuntaTodo e usato anche in ModificaTodo per salvare la data di scadenza
     * ActionListener per la selezione della data di scadenza
     * @param dataScelto riferimento alla data selezionata
     * @param creaTodoDialog il dialog da cui viene chiamato il metodo
     */
    private ActionListener generaActionListenerSceltaScadenza(AtomicReference<Calendar> dataScelto, CreaToDo creaTodoDialog){
        return _->{
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

            cancelButton.addActionListener(_->
                dateDialog.dispose()
            );

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
     * Metodo estratto da AggiuntaTodo e usato anche in ModificaTodo per salvare la CheckList
     * ActionListener per la creare il pannello da cui verrà poi ricavata la CheckList
     * @param attivitaFields riferimento all' array di attività da salvare
     * @param creaTodoDialog il dialog da cui viene chiamato il metodo
     */
    private ActionListener generaActionListenerAggiungiCheckList(ArrayList<JTextField> attivitaFields,AtomicReference<JTextField> titoloCheckListRef,CreaToDo creaTodoDialog){
        return _->{
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
     * @param linkTesto il link da controllare
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

    private ActionListener generaActionListenerSalvataggio(Main mainView,CreaToDo creaTodoDialog,ArrayList<JTextField> attivitaFields,AtomicReference<Calendar> dataScelto,AtomicReference<Color> coloreScelto,AtomicReference<URL> immagineScelta,AtomicReference<JTextField> titoloCheckListRef, Utente utenteAttuale){
        return _->{
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

            //check del link
            try {
                URI link = parseLink(linkTesto, creaTodoDialog);
                if (link == null && linkTesto != null && !linkTesto.trim().isEmpty()) {
                    return; // Esci se il link è invalido
                }


                // Crea il nuovo ToDo con tutti i dati raccolti

                ArrayList<Attivita> attivitaDaSalvare = estraiAttivitaDaFields(attivitaFields);
                ToDo nuovoToDo = new ToDo(titoloTodo, descrizioneTodo, link, dataScelto.get(), coloreScelto.get(), immagineScelta.get(), null);
                if (attivitaDaSalvare != null) {
                    Checklist c =new Checklist(titoloCheckListRef.get().getText() ,attivitaDaSalvare);
                    nuovoToDo.setChecklist(c);
                }


                try{
                    toDoDAO.creaToDo(utenteAttuale.getEmail(), nuovoToDo);
                } catch (Exception daoEx) {
                    JOptionPane.showMessageDialog(creaTodoDialog,
                            "Errore nel salvataggio del ToDo nel database: " + daoEx.getMessage(),
                            ERRORMESSAGE, JOptionPane.ERROR_MESSAGE);
                    return;
                }
                //aggiorna bacheche in memoria
                switch (bachecaSelezionata) {
                    case TL -> utenteAttuale.getTempoLibero().getToDoList().add(nuovoToDo);
                    case LAV -> utenteAttuale.getLavoro().getToDoList().add(nuovoToDo);
                    case UNI -> utenteAttuale.getUniversita().getToDoList().add(nuovoToDo);
                    default -> {
                        JOptionPane.showMessageDialog(creaTodoDialog,
                                "Seleziona una bacheca",
                                ERRORMESSAGE, JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                creaTodoDialog.dispose();
                // Aggiorna l'interfaccia
                aggiornaInterfacciaUtente(mainView);
                view.pack();
                view.revalidate();
                view.repaint();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(creaTodoDialog,
                        "Errore durante la creazione del ToDo: "+ ex.getMessage(),
                        ERRORMESSAGE, JOptionPane.ERROR_MESSAGE);
            }
        };
    }

    /**
    * Metodo per mostrare il pannello CreaToDo a modi pop up e gestione del salvataggio in memoria dei dati
    */
    private void aggiuntaTodo() {
        Main mainView = view.getLogInView().getMainView();
        CreaToDo creaTodoDialog = new CreaToDo();
        creaTodoDialog.setContentPane(creaTodoDialog.getMainPanel());
        creaTodoDialog.setLocationRelativeTo(mainView.getMain());

        AtomicReference<Color> coloreScelto = new AtomicReference<>(Color.WHITE);
        AtomicReference<Calendar> dataScelto = new AtomicReference<>();
        AtomicReference<URL> immagineScelta = new AtomicReference<>();

        // Action listener per il pulsante Sfoglia per la selezione dell'immagine
        creaTodoDialog.getSfogliaButton().addActionListener( generaActionListenerSceltaImmagine(immagineScelta, creaTodoDialog) );


        /*Action listener per il selettore di colore
        * vengono usati array perchè durante il runtime della gui i metodi lambda hanno bisogno di variabili final
        */
        creaTodoDialog.getColorButton().addActionListener(generaActionListenerSceltaColore(coloreScelto, creaTodoDialog) );

        // Action listener per il selettore di data ho usato l'array di date per il solito discorso del colore
        creaTodoDialog.getDataScadenzaButton().addActionListener(generaActionListenerSceltaScadenza(dataScelto, creaTodoDialog) );

        ArrayList<JTextField> attivitaFields = new ArrayList<>();
        AtomicReference<JTextField> titoloCheckListRef = new AtomicReference<>();
        creaTodoDialog.getChecklistButton().addActionListener(generaActionListenerAggiungiCheckList(attivitaFields,titoloCheckListRef,creaTodoDialog));

        // Action listener per il pulsante Salva
        creaTodoDialog.getSalvaButton().addActionListener(generaActionListenerSalvataggio(mainView,creaTodoDialog,attivitaFields,dataScelto,coloreScelto,immagineScelta,titoloCheckListRef, utenteAttuale));

        creaTodoDialog.pack();
        creaTodoDialog.setLocationRelativeTo(view.getLogInView().getMainView().getMain());
        creaTodoDialog.setVisible(true);

    }

    // Metodo helper per estrarre le attività dai campi di testo
    private ArrayList<Attivita> estraiAttivitaDaFields(ArrayList<JTextField> attivitaFields) {
        ArrayList<Attivita> attivita = new ArrayList<>();
        // Se non ci sono campi attività, ritorna lista vuota
        if (attivitaFields == null || attivitaFields.isEmpty()) {
            return null;
        }

        // Processa solo i campi non null e non vuoti
        for (JTextField field : attivitaFields) {
            if (field != null) {
                String testo = field.getText();
                if (testo != null && !testo.trim().isEmpty()) {
                    attivita.add(new Attivita(testo.trim()));
                }
            }
        }
        return attivita;
    }

    private ActionListener generaActionListnerModificaDescrizione(Bacheca b, Main mainView){
        return e->{
            ModificaDescrizione modificaDescrizioneDialog = new ModificaDescrizione();
            modificaDescrizioneDialog.setTitle("Modifica descrizione bacheca: " + b.getTitolo());
            SetPlaceHolder.setTP(modificaDescrizioneDialog.getTextField(), "Descrizione", GestioneDarkMode.isDarkMode());
            modificaDescrizioneDialog.setDescrizione(b.getDescrizione());
            modificaDescrizioneDialog.getButtonOK().addActionListener(
                    ex -> {
                        b.setDescrizione(modificaDescrizioneDialog.getTextFieldText());
                        // Chiudi il dialog
                        modificaDescrizioneDialog.dispose();
                        // Ricarica l'interfaccia principale
                        aggiornaInterfacciaUtente(mainView);
                    }
            );
            modificaDescrizioneDialog.pack();
            modificaDescrizioneDialog.setLocationRelativeTo(view.getLogInView().getMainView().getMain());
            modificaDescrizioneDialog.setVisible(true);
        };
    }

    private ActionListener generaActionListnerModificaOrdine(Bacheca b, Main mainView){
        return _->{
            GestioneOrdine gestioneOrdineDialog = new GestioneOrdine();

            gestioneOrdineDialog.getButtonOK().addActionListener(
                    ex ->{
                        String selectedCommand = gestioneOrdineDialog.getGroup().getSelection() != null ? gestioneOrdineDialog.getGroup().getSelection().getActionCommand() : null;

                        if (selectedCommand != null) {
                            // Usa uno switch per gestire le opzioni selezionate
                            switch (selectedCommand) {
                                case "AZ":
                                    b.setOrdinamento(Ordinamento.AZ);
                                    break;
                                case "ZA":
                                    b.setOrdinamento(Ordinamento.ZA);
                                    break;
                                case "CreazioneCrescente":
                                    b.setOrdinamento(Ordinamento.CREAZIONE_ASC);
                                    break;
                                case "CreazioneDecrescente":
                                    b.setOrdinamento(Ordinamento.CREAZIONE_DESC);
                                    break;
                                case "ScadenzaCrescente":
                                    b.setOrdinamento(Ordinamento.SCADENZA_ASC);
                                    break;
                                case "ScadenzaDecrescente":
                                    b.setOrdinamento(Ordinamento.SCADENZA_DESC);
                                    break;
                                default:
                                    b.setOrdinamento(null);
                                    break;
                            }
                        }

                        gestioneOrdineDialog.dispose();
                        // Ricarica l'interfaccia principale
                        aggiornaInterfacciaUtente(mainView);
                    }
            );
            gestioneOrdineDialog.pack();
            gestioneOrdineDialog.setLocationRelativeTo(view.getLogInView().getMainView().getMain());
            gestioneOrdineDialog.setVisible(true);
        };
    }

    /**
     * Metodo per generare la bacheca tempo libero, mostra il pannello solo se sono presenti dei todo e richiama i metodi per generare gli action listener
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
                    generaActionListnerModificaDescrizione(tempoLibero,mainView)
            );


            mainView.getOrdineFreButton().addActionListener(
                    generaActionListnerModificaOrdine(tempoLibero,mainView)
            );
            if (tempoLibero.getOrdinamento() != null) {
                tempoLibero.setToDoList(ordinaToDoList(tempoLibero.getToDoList(), tempoLibero.getOrdinamento()));
            }
            for (ToDo todo : tempoLibero.getToDoList()) {
                if (mostraCompletati || !todo.isCompletato()) {
                    visualizzaToDo(todo, contenitoreToDoT,false);
                    haToDoTempoLibero = true;
                }
            }

        }
        mainView.getBaFre().setVisible(haToDoTempoLibero);
    }

    /**
     * Metodo per generare la bacheca lavoro, mostra il pannello solo se sono presenti dei todo e richiama i metodi per generare gli action listener
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
                    generaActionListnerModificaDescrizione(lavoro,mainView)
            );
            mainView.getOrdineLavButton().addActionListener(
                    generaActionListnerModificaOrdine(lavoro,mainView)
            );

            if (lavoro.getOrdinamento() != null) {
                lavoro.setToDoList(ordinaToDoList(lavoro.getToDoList(), lavoro.getOrdinamento()));
            }
            for (ToDo todo : lavoro.getToDoList()) {
                if (mostraCompletati || !todo.isCompletato()) {
                    visualizzaToDo(todo, contenitoreToDoL,false);
                    haToDoLavoro = true;
                }
            }
        }
        mainView.getBaLav().setVisible(haToDoLavoro);
    }

    /**
     * Metodo per generare la bacheca università, mostra il pannello solo se sono presenti dei todo e richiama i metodi per generare gli action listener
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
                    generaActionListnerModificaDescrizione(universita,mainView)
            );
            mainView.getOrdineUniButton().addActionListener(
                    generaActionListnerModificaOrdine(universita,mainView)
            );
            if (universita.getOrdinamento() != null) {
                universita.setToDoList(ordinaToDoList(universita.getToDoList(), universita.getOrdinamento()));
            }
            for (ToDo todo : universita.getToDoList()) {
                if (mostraCompletati || !todo.isCompletato()) {
                    visualizzaToDo(todo, contenitoreToDoU,false);
                    haToDoUniversita = true;
                }
            }
        }
        mainView.getBaUni().setVisible(haToDoUniversita);
    }


    /**
     * Metodo per rimuovere i vecchi action listner altrimenti i metodi per generare le bacheche alle loro pulsanti aprirebbero troppe volte i dialog
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
     * @param mainView MainView della GUI
     */
    private void aggiornaInterfacciaUtente(Main mainView) {
        // Verifica se l'utente attuale è valido
        if (utenteAttuale != null) {
            mainView.setNomeText(utenteAttuale.getEmail());



            generaTempoLibero(mainView);
            generaLavoro(mainView);
            generaUniversita(mainView);

        }
    }

    private void creazioneModificaDialog(@NotNull JButton modificaButton, ToDo todo){
        modificaButton.addActionListener(e -> {
            // Crea un'istanza del pannello CreaToDo per la modifica
            CreaToDo modificaTodoDialog = new CreaToDo();
            modificaTodoDialog.setContentPane(modificaTodoDialog.getMainPanel());
            modificaTodoDialog.setLocationRelativeTo(view.getLogInView().getMainView().getMain());

            // Popola i campi con i dati del todo corrente
            modificaTodoDialog.getTitolo().setText(todo.getTitolo());
            modificaTodoDialog.getDescrizioneField().setText(todo.getDescrizione());

            AtomicReference<Color> coloreSceltoNuovo = new AtomicReference<>(Color.WHITE);
            AtomicReference<Calendar> dataSceltoNuovo = new AtomicReference<>();
            AtomicReference<URL> immagineSceltaNuovo = new AtomicReference<>();

            // Action listener per il pulsante Sfoglia per la selezione dell'immagine
            modificaTodoDialog.getSfogliaButton().addActionListener(generaActionListenerSceltaImmagine(immagineSceltaNuovo, modificaTodoDialog));
            /*Action listener per il selettore di colore
             * vengono usati array perchè durante il runtime della gui i metodi lambda hanno bisogno di variabili final
             */
            modificaTodoDialog.getColorButton().addActionListener(generaActionListenerSceltaColore(coloreSceltoNuovo, modificaTodoDialog));

            // Action listener per il selettore di data ho usato l'array di date per il solito discorso del colore
            modificaTodoDialog.getDataScadenzaButton().addActionListener(generaActionListenerSceltaScadenza(dataSceltoNuovo, modificaTodoDialog));

            // Imposta il link se presente
            if (todo.getLink() != null) {
                modificaTodoDialog.getLinkField().setText(todo.getLink().toString());
            }

            // Imposta il colore se presente
            if (todo.getSfondo() != null) {
                coloreSceltoNuovo.set(todo.getSfondo());
            }

            // Imposta la data se presente
            if (todo.getScadenza() != null) {
                dataSceltoNuovo.set(todo.getScadenza());
            }

            // Carica l'immagine se presente
            if (todo.getImmagine() != null) {
                caricaImmagine(new ImageIcon(todo.getImmagine()), modificaTodoDialog.getPreviewPanel(), modificaTodoDialog);
            }

            if (todo.getSfondo() != null) {
                // Cambia il colore del bottone per mostrare il colore scelto
                modificaTodoDialog.getColorButton().setForeground(todo.getSfondo());
                modificaTodoDialog.getColorButton().repaint();
                modificaTodoDialog.getColorButton().revalidate();
            }

            if (todo.getScadenza() != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat(FORMATODATA);
                modificaTodoDialog.getDataScadenzaButton().setText(dateFormat.format(todo.getScadenza()));
            }

            ArrayList<JTextField> attivitaFieldsNuovo = new ArrayList<>();
            AtomicReference<JTextField> titoloCheckListRefNuovo = new AtomicReference<>();
            modificaTodoDialog.getChecklistButton().addActionListener(generaActionListenerAggiungiCheckList(attivitaFieldsNuovo,titoloCheckListRefNuovo,modificaTodoDialog));

            if (todo.getChecklist()!=null){
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

                for (Attivita attivitaGiaPresente: todo.getChecklist().getAttivita()){
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

            // Imposta la selezione della bacheca
            modificaTodoDialog.getBachecaBox().setSelectedItem(bachecaCorrente);



            modificaTodoDialog.getSalvaButton().addActionListener(saveEvent -> {
                // Validazione del titolo
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
                if (attivitaDaSalvareNuovo != null) {
                    todo.getChecklist().setNomeChecklist(titoloCheckListRefNuovo.get().getText());
                    todo.getChecklist().setAttivita(attivitaDaSalvareNuovo);
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


                todo.setSfondo(coloreSceltoNuovo.get());
                todo.setImmagine(immagineSceltaNuovo.get());
                todo.setScadenza(dataSceltoNuovo.get());


                // Gestisci il cambio di bacheca se necessario
                String nuovaBacheca = (String) modificaTodoDialog.getBachecaBox().getSelectedItem();
                if (!nuovaBacheca.equals(bachecaCorrente)) {
                    // Rimuovi dalla bacheca attuale
                    switch (bachecaCorrente) {
                        case TL -> utenteAttuale.getTempoLibero().getToDoList().remove(todo);
                        case LAV -> utenteAttuale.getLavoro().getToDoList().remove(todo);
                        case UNI -> utenteAttuale.getUniversita().getToDoList().remove(todo);
                        default -> throw new IllegalStateException("Unexpected value: " + bachecaCorrente);
                    }

                    // Aggiungi alla nuova bacheca
                    switch (nuovaBacheca) {
                        case TL-> utenteAttuale.getTempoLibero().getToDoList().add(todo);
                        case LAV-> utenteAttuale.getLavoro().getToDoList().add(todo);
                        case UNI-> utenteAttuale.getUniversita().getToDoList().add(todo);
                        default -> JOptionPane.showMessageDialog(modificaTodoDialog,
                                "Seleziona una bacheca",
                                ERRORMESSAGE, JOptionPane.ERROR_MESSAGE);
                    }
                }

                //salvataggio modifiche nel db
                try{
                    toDoDAO.modificaToDo(utenteAttuale.getEmail(), todo);
                } catch (Exception daoEx) {
                    JOptionPane.showMessageDialog(modificaTodoDialog,
                            "Errore durante il salvataggio nel database: "+ daoEx.getMessage(),
                            ERRORMESSAGE, JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Chiudi il dialog e aggiorna l'interfaccia
                modificaTodoDialog.dispose();
                aggiornaInterfacciaUtente(view.getLogInView().getMainView());
                view.revalidate();
                view.repaint();
            });

            // Mostra il dialog di modifica
            modificaTodoDialog.pack();
            modificaTodoDialog.setLocationRelativeTo(view.getLogInView().getMainView().getMain());
            modificaTodoDialog.setVisible(true);
        });
    }

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

    private JButton generaModificaButton(Color coloreTesto, ToDo todo){
        // Pulsante modifica con icona di modifica (Unicode per pencil)
        JButton modificaButton = new JButton("✏");
        modificaButton.setFont(new Font("Dialog", Font.PLAIN, 14));
        modificaButton.setToolTipText("Modifica");
        modificaButton.setFocusPainted(false);
        modificaButton.setBorderPainted(false);
        modificaButton.setContentAreaFilled(false);
        modificaButton.setForeground(coloreTesto);
        modificaButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        creazioneModificaDialog(modificaButton,todo);
        return modificaButton;
    }

    private JButton generaShareButton(Color coloreTesto, ToDo todo){
        //Pulsante share con icona della freccia(➢)
        JButton shareButton= new JButton("➢");
        shareButton.setFont(new Font("Dialog", Font.PLAIN, 14));
        shareButton.setToolTipText("Condividi");
        shareButton.setFocusPainted(false);
        shareButton.setBorderPainted(false);
        shareButton.setContentAreaFilled(false);
        shareButton.setForeground(coloreTesto);
        shareButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        shareButton.addActionListener(e -> {
            ModificaDescrizione condividiConDialog= new ModificaDescrizione();
            condividiConDialog.setVisible(true);
            condividiConDialog.setTitle("Condividi con");
            SetPlaceHolder.setTP(condividiConDialog.getTextField(),"Inserisci la email con cui vuoi condividere",GestioneDarkMode.isDarkMode());

            condividiConDialog.getButtonOK().addActionListener(_->{
                String emailCondivisa= condividiConDialog.getTextFieldText();
                //marta
                //aggiungere query che controlla che la stringa data dal risultato sia presente nel db
                Utente utenteCondiviso=null;
                Condivisione condivido = new Condivisione(utenteAttuale,todo, utenteCondiviso);
            });
        });
        return shareButton;
    }

    public JButton generaEliminaButton(Color coloreTesto, ToDo todo){
        // Pulsante elimina con icona del cestino (Unicode per trash)
        JButton eliminaButton = new JButton("🗑");
        eliminaButton.setFont(new Font("Dialog", Font.PLAIN, 14));
        eliminaButton.setToolTipText("Elimina");
        eliminaButton.setFocusPainted(false);
        eliminaButton.setBorderPainted(false);
        eliminaButton.setContentAreaFilled(false);
        eliminaButton.setForeground(coloreTesto);
        eliminaButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        eliminaButton.addActionListener(e -> {
            int conferma = JOptionPane.showConfirmDialog(null,
                    "Sei sicuro di voler eliminare questo ToDo?",
                    "Conferma eliminazione", JOptionPane.YES_NO_OPTION);
            if(conferma != JOptionPane.YES_OPTION) {
                return;
            }
            boolean rimosso = false;

            if(utenteAttuale.getTempoLibero().getToDoList().remove(todo)) rimosso = true;
            if(utenteAttuale.getLavoro().getToDoList().remove(todo)) rimosso = true;
            if(utenteAttuale.getUniversita().getToDoList().remove(todo)) rimosso = true;

            if(rimosso) {
                try{
                    toDoDAO.eliminaToDo(utenteAttuale.getEmail(), todo.getTitolo());

                    //aggiorna l'interfaccia utente
                    aggiornaInterfacciaUtente(view.getLogInView().getMainView());
                    view.revalidate();
                    view.repaint();

                } catch (Exception daoEx) {
                    JOptionPane.showMessageDialog(null,
                            "Errore durante l'eliminazione nel database: "+ daoEx.getMessage(),
                            ERRORMESSAGE, JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        });

        return eliminaButton;
    }

    /**
     * Metodo che genera il codice swing per la gui partendo dal todo in memoria
     * @param todo il todo da visualizzare
     * @param contenitoreToDo il contenitore a cui applicare il todo generato
     */
    private void visualizzaToDo(@NotNull ToDo todo, @NotNull JPanel contenitoreToDo, Boolean isCondiviso) {

        // Crea un nuovo JPanel per il singolo ToDo
        JPanel todoPanel = new JPanel();
        todoPanel.setLayout(new BoxLayout(todoPanel, BoxLayout.Y_AXIS));


        //Gestione del colore di sfondo
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
        buttonsPanel.add(generaModificaButton(coloreTesto,todo));
        buttonsPanel.add(generaEliminaButton(coloreTesto,todo));
        buttonsPanel.add(generaShareButton(coloreTesto,todo));
        
        // Aggiungi il pannello dei pulsanti al pannello del titolo
        titoloPanel.add(buttonsPanel, BorderLayout.EAST);

        // Aggiungi il pannello del titolo al pannello principale
        todoPanel.add(titoloPanel);

        // Aggiungi un bordo al pannello principale
        todoPanel.setBorder(BorderFactory.createLineBorder(coloreTesto));



        //panel con label e checkbox
        JPanel descrizionePanel = new JPanel();
        descrizionePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        descrizionePanel.setBackground(backgroundColor);

        // Crea e aggiungi una JLabel con il titolo del ToDo
        JLabel descrizioneLabel = new JLabel(todo.getDescrizione());
        descrizioneLabel.setForeground(coloreTesto);

        //creazione checkbox
        JCheckBox checkboxTodo = new JCheckBox();
        checkboxTodo.setSelected(todo.isCompletato());
        checkboxTodo.setBackground(backgroundColor);
        checkboxTodo.setForeground(coloreTesto);

        checkboxTodo.addItemListener(e -> {
            todo.setCompletato(checkboxTodo.isSelected());
            aggiornaInterfacciaUtente(view.getLogInView().getMainView());
            view.revalidate();
            view.repaint();
        });

        //aggiunta al panel descrizione
        descrizionePanel.add(checkboxTodo);
        descrizionePanel.add(descrizioneLabel);

        //aggiunta al panel todo
        todoPanel.add(descrizionePanel);

        //aggiunta checklist
        if (todo.getChecklist() != null) {
            JPanel checklistPanel = new JPanel();
            checklistPanel.setLayout(new BoxLayout(checklistPanel, BoxLayout.Y_AXIS));
            checklistPanel.setBorder(BorderFactory.createTitledBorder(todo.getChecklist().getNomeChecklist()));
            checklistPanel.setBackground(backgroundColor);

            //for each delle attività
            for (Attivita att : todo.getChecklist().getAttivita()) {
                //panel delle attività
                JPanel attivitaPanel = new JPanel();
                attivitaPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
                attivitaPanel.setBackground(backgroundColor);


                // Verifica se la checklist è già completata per cambiare colore del bordo
                if (Boolean.TRUE.equals(todo.getChecklist().getCompletata())) {
                    checklistPanel.setBackground(Color.GREEN);
                    TitledBorder titledBorder = BorderFactory.createTitledBorder(
                            BorderFactory.createLineBorder(Color.GREEN, 2),
                            todo.getChecklist().getNomeChecklist());
                    titledBorder.setTitleColor(Color.GREEN.darker());
                    checklistPanel.setBorder(titledBorder);
                } else {
                    checklistPanel.setBorder(BorderFactory.createTitledBorder(todo.getChecklist().getNomeChecklist()));
                    checklistPanel.setBackground(backgroundColor);
                }


                // Crea una checkbox e imposta lo stato in base allo stato dell'attività
                JCheckBox checkBoxAtt = new JCheckBox();
                checkBoxAtt.setSelected(att.isCompletata());
                checkBoxAtt.setBackground(backgroundColor);
                checkBoxAtt.setForeground(coloreTesto);
                checkBoxAtt.addItemListener(e -> {
                    att.setCompletata(checkBoxAtt.isSelected());
                    boolean controllo=false;
                    for (Attivita att2 : todo.getChecklist().getAttivita()) {
                        if (att2.isCompletata()) {
                            controllo = true;
                        } else {
                            controllo = false;
                            break;
                        }
                    }
                    if (controllo)
                        todo.getChecklist().setCompletata(true);

                    aggiornaInterfacciaUtente(view.getLogInView().getMainView());
                    view.revalidate();
                    view.repaint();
                });

                //label delle attività
                JLabel attLabel = new JLabel(att.getNome());
                attLabel.setForeground(coloreTesto);

                //aggiunta al panel attività
                attivitaPanel.add(checkBoxAtt);
                attivitaPanel.add(attLabel);

                //aggiunta al panel checklist
                checklistPanel.add(attivitaPanel);
            }

            checklistPanel.revalidate();
            checklistPanel.repaint();
            todoPanel.add(checklistPanel);
            todoPanel.revalidate();
            todoPanel.repaint();
        }
        //fine checklist


        // Dopo l'aggiunta della checklist, aggiungi il link se esiste
        if (todo.getLink() != null) {
            JPanel uriPanel = new JPanel();
            uriPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            uriPanel.setBackground(backgroundColor);

            JLabel uriLabel = new JLabel("Link: ");
            uriLabel.setForeground(coloreTesto);
            JLabel linkLabel = new JLabel("<html><a href='"+todo.getLink()+"'>" + todo.getLink() + "</a></html>");
            linkLabel.setForeground(Color.BLUE);
            linkLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

            linkLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (Desktop.isDesktopSupported()) {
                        try {
                            Desktop.getDesktop().browse(todo.getLink());
                        } catch (IOException ex) {
                            JOptionPane.showMessageDialog(todoPanel,
                                    "Impossibile aprire il link: " + ex.getMessage(),
                                    ERRORMESSAGE, JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            });

            uriPanel.add(uriLabel);
            uriPanel.add(linkLabel);
            todoPanel.add(uriPanel);
        }

        // Aggiungi la data di scadenza
        if (todo.getScadenza() != null) {
            todoPanel.add(generaPanelScadenza(todo,backgroundColor,coloreTesto));
        }

        if (todo.getImmagine() != null) {
            todoPanel.add(generaPanelImmagine(todo, backgroundColor));
        }

        if (isCondiviso){
            todoPanel.add(generaCondiviso(coloreTesto));
        }


        //aggiunta al contenitore
        contenitoreToDo.add(todoPanel);


        contenitoreToDo.revalidate();
        contenitoreToDo.repaint();
    }

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
        Image immagineRidimensionata = immagine.getScaledInstance(nuovaLarghezza, altezzaDesiderata, Image.SCALE_SMOOTH);
        ImageIcon iconaRidimensionata = new ImageIcon(immagineRidimensionata);


        JLabel labelImmagine = new JLabel(iconaRidimensionata);

        immaginePanel.add(labelImmagine);
        return immaginePanel;
    }

    private static @NotNull JPanel generaPanelScadenza(@NotNull ToDo todo,Color backgroundColor, Color coloreTesto){
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

    public JPanel generaCondiviso(Color coloreTesto){
        JPanel condivisionePanel = new JPanel();
        condivisionePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel condivisoDaLabel = new JLabel("Condiviso da: ");
        condivisoDaLabel.setForeground(coloreTesto);
        JLabel condivisoDa = new JLabel();
        condivisoDa.setForeground(Color.BLUE);
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
            this.mostraPanel(view.getLogInView().getMainLogIn());;

            LogIn loginView = view.getLogInView();
            loginView.getBack().addActionListener(ev -> mostraPanel(view.getScelta()));
            // Gestione click su "entra"
            loginView.getEntra().addActionListener(ev -> gestisciLogin(loginView));
        });

        view.getRegistratiButton().addActionListener(e -> {
            this.mostraPanel(view.getRegisterView().getMainRegistrazione());;

            Register registerView = view.getRegisterView();
            registerView.getBackButton().addActionListener(ev -> mostraPanel(view.getScelta()));
            // Listener per il pulsante "Registrati"
            registerView.getRegistratiButton().addActionListener(ev -> gestisciRegistrazione(registerView));
        });
    }

    /**
     *  Metodo per scegliere se usare il testo chiaro o scuro in base allo sfondo di personalizzato di un todo
     * @param background il colore di sfondo usato
     * @return Il colore da utilizzare nel testo
     */
    private Color getContrasto(@NotNull Color background) {
        // Formula per calcolare la luminosità del colore
        // Basata sulla percezione dell'occhio umano (0.299R + 0.587G + 0.114B)
        double luminance = (0.299 * background.getRed() +
                0.587 * background.getGreen() +
                0.114 * background.getBlue()) / 255;

        // Se la luminosità è superiore a 0.5, usare testo scuro, altrimenti testo chiaro
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
                
                this.mostraMain();
            } else {
                JOptionPane.showMessageDialog(loginView,
                        "Credenziali non valide.",
                        ATTENZIONE,
                        JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception _) {
            JOptionPane.showMessageDialog(loginView,
                    "Email o password non corretti.",
                    ERRORMESSAGE, JOptionPane.ERROR_MESSAGE);
        }

        //ho riusato il modifica descrizione con il modifica password
        loginView.getPassDime().addActionListener(
                e ->{
                    ModificaDescrizione modificaPass = new ModificaDescrizione();
                    modificaPass.setVisible(true);
                    modificaPass.setTitle("Modifica password");
                    SetPlaceHolder.setTP(modificaPass.getTextField(),"Inserisci la nuova password",GestioneDarkMode.isDarkMode());

                    modificaPass.getButtonOK().addActionListener(
                            ex ->{
                                utenteAttuale = new Utente(email, modificaPass.getTextFieldText(), new Bacheca(), new Bacheca(), new Bacheca());
                                this.mostraMain();
                            }
                    );
                }
        );
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
        try{
            if(utenteDAO.loginValido(email, password)) {
                JOptionPane.showMessageDialog(registerView,
                        "Utente già registrato!",
                        ATTENZIONE,
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            utenteDAO.registraUtente(email,password);

            JOptionPane.showMessageDialog(registerView,
                    "Registrazione avvenuta con successo! Effettua il LogIn",
                    "Successo",
                    JOptionPane.INFORMATION_MESSAGE);
            this.mostraPanel(view.getLogInView().getMainLogIn());;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(registerView,
                    "Errore durante la registrazione: "+ ex.getMessage(),
                    ERRORMESSAGE, JOptionPane.ERROR_MESSAGE);
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