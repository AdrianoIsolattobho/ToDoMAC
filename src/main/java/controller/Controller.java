package controller;

import gui.*;

import implementazioniPostgresDAO.ToDoImplementazionePostgresDAO;
import implementazioniPostgresDAO.UtenteImplementazionePostgresDAO;
import model.*;
import org.jetbrains.annotations.NotNull;
import dao.ToDoDAO;
import dao.UtenteDAO;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.awt.Image;


/**
 *  Controller che gestisce tutte le interazioni tra modello e view
 *  come nel paradigma Model-View-Controller (MVC)
 *  o Boundary-Control-Entity (BCE)
 */
public class Controller {

    private Scelta view;
    private HashMap<String, Utente> utentiRegistrati;
    private Utente utenteAttuale;
    private boolean mostraCompletati=false;
    private ToDoDAO toDoDAO = new ToDoImplementazionePostgresDAO();
    private UtenteDAO utenteDAO = new UtenteImplementazionePostgresDAO();

    /**
    * Metodi per mostrare le varie view
     * mostra il primo pannello con la scelta tra login e registrazione
    */
    private void mostraScelta() {
        view.setContentPane(view.getScelta());
        view.pack();
        view.setLocationRelativeTo(null);
        view.revalidate();
        view.repaint();
    }

    /**
     * Metodi per mostrare le varie view
     * mostra il pannello con il login
     */
    private void mostraLogin() {
        view.setContentPane(view.getLogInView().getMainLogIn());
        view.pack();
        view.setLocationRelativeTo(null);
        view.revalidate();
        view.repaint();
    }

    /**
     * Metodi per mostrare le varie view
     * mostra il pannello con la registrazione
     */
    private void mostraRegistrazione() {
        view.setContentPane(view.getRegisterView().getMainRegistrazione());
        view.pack();
        view.setLocationRelativeTo(null);
        view.revalidate();
        view.repaint();
    }

    /**
     * Metodi per mostrare le varie view
     * mostra il pannello principale con i vari todo
     * aggiunge gli action listener per l'aggiunta di nuovi todo
     * per il logout dal programma
     * e per mostrare i todo già completati
     */
    private void mostraMain() {

        Main mainView = view.getLogInView().getMainView();

        aggiornaInterfacciaUtente(mainView);

        view.setContentPane(mainView.getMain());
        view.pack();
        view.setLocationRelativeTo(null);
        view.revalidate();
        view.repaint();
        mainView.getAggiungiToDo().addActionListener(e -> aggiuntaTodo());
        mainView.getMostraCompletati().addActionListener(
                e-> {this.mostraCompletati= !this.mostraCompletati;
                    mainView.getMostraCompletati().setText(mostraCompletati ? "Mostra senza completati" : "Mostra tutti");
                aggiornaInterfacciaUtente(mainView);}
        );
        mainView.getEsci().addActionListener(e->mostraScelta());

    }

    /**
    * Metodo per mostrare il pannello CreaToDo a modi pop up e gestione del salvataggio in memoria dei dati
    */
    private void aggiuntaTodo() {
        Main mainView = view.getLogInView().getMainView();
        CreaToDo creaTodoDialog = new CreaToDo();
        creaTodoDialog.setupComponents();
        creaTodoDialog.setContentPane(creaTodoDialog.getMainPanel());
        creaTodoDialog.setLocationRelativeTo(mainView.getMain());

        // Variabili per memorizzare i valori selezionati
        final Color[] coloreScelto = {null};
        final Calendar[] dataScelto = {null};
        final URL[] immagineScelta = {null};

        // Action listener per il pulsante Sfoglia per la selezione dell'immagine
        creaTodoDialog.getSfogliaButton().addActionListener(imageEvent -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Seleziona un'immagine");

            // Filtro per mostrare solo file di immagine
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "Immagini (JPG, PNG, GIF)", "jpg", "jpeg", "png", "gif");
            fileChooser.setFileFilter(filter);

            int result = fileChooser.showOpenDialog(creaTodoDialog);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                try {
                    // Converti il file selezionato in URL
                    immagineScelta[0] = selectedFile.toURI().toURL();

                    // Carica l'immagine per l'anteprima
                    ImageIcon originalIcon = new ImageIcon(immagineScelta[0]);
                    Image originalImage = originalIcon.getImage();

                    // Definisci le dimensioni per l'anteprima
                    int previewWidth = 200;
                    int previewHeight = 150;

                    // Calcola le dimensioni mantenendo le proporzioni
                    int originalWidth = originalIcon.getIconWidth();
                    int originalHeight = originalIcon.getIconHeight();
                    double ratio = (double) originalWidth / originalHeight;

                    int targetWidth, targetHeight;
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
                    creaTodoDialog.getPreviewPanel().removeAll();
                    JLabel previewLabel = new JLabel(resizedIcon);
                    creaTodoDialog.getPreviewPanel().add(previewLabel);

                    // Ridimensiona il form per mostrare l'immagine
                    Dimension currentSize = creaTodoDialog.getSize();

                    // Assicurati che il pannello di anteprima sia visibile
                    creaTodoDialog.getPreviewPanel().setVisible(true);

                    // Imposta un bordo per il pannello di anteprima
                    creaTodoDialog.getPreviewPanel().setBorder(BorderFactory.createTitledBorder("Anteprima immagine"));

                    // Fai il pack della finestra per adattarla ai nuovi contenuti
                    creaTodoDialog.pack();

                    // Se la dimensione attuale è maggiore di quella calcolata dal pack, mantienila
                    int newWidth = Math.max(currentSize.width, creaTodoDialog.getSize().width);
                    int newHeight = Math.max(currentSize.height, creaTodoDialog.getSize().height);

                    // Imposta la nuova dimensione
                    creaTodoDialog.setSize(newWidth, newHeight);

                    // Centra di nuovo la finestra
                    creaTodoDialog.setLocationRelativeTo(creaTodoDialog.getOwner());

                    // Aggiorna l'interfaccia
                    creaTodoDialog.getPreviewPanel().revalidate();
                    creaTodoDialog.getPreviewPanel().repaint();

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(creaTodoDialog,
                            "Errore nel caricamento dell'immagine: " + ex.getMessage(),
                            "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        /*Action listener per il selettore di colore
        * vengono usati array perchè durante il runtime della gui i metodi lambda hanno bisogno di variabili final
        */
        creaTodoDialog.getColorButton().addActionListener(colorEvent -> {
            Color coloreIniziale = coloreScelto[0] != null ? coloreScelto[0] : Color.WHITE;
            Color nuovoColore = JColorChooser.showDialog(
                    creaTodoDialog,
                    "Scegli un colore per il ToDo",
                    coloreIniziale);

            if (nuovoColore != null) {
                coloreScelto[0] = nuovoColore;
                // Cambia il colore del bottone per mostrare il colore scelto
                creaTodoDialog.getColorButton().setBackground(nuovoColore);
            }
        });

        // Action listener per il selettore di data ho usato l'array di date per il solito discorso del colore
        creaTodoDialog.getDataScadenzaButton().addActionListener(dateEvent -> {
            // Creiamo un JDialog personalizzato per il calendario
            JDialog dateDialog = new JDialog(creaTodoDialog, "Seleziona data di scadenza", true);
            dateDialog.setLayout(new BorderLayout());

            // Utilizziamo JSpinner per la selezione della data
            JPanel calendarPanel = new JPanel();

            // Creiamo un calendario con JSpinner
            SpinnerDateModel dateModel = new SpinnerDateModel();
            JSpinner dateSpinner = new JSpinner(dateModel);
            JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy");
            dateSpinner.setEditor(dateEditor);

            // Se c'è già una data selezionata, la impostiamo
            if (dataScelto[0] != null) {
                dateSpinner.setValue(dataScelto[0].getTime());
            }

            calendarPanel.add(new JLabel("Seleziona data: "));
            calendarPanel.add(dateSpinner);

            // Pulsanti di conferma e annulla
            JPanel buttonPanel = new JPanel();
            JButton confirmButton = new JButton("Conferma");
            JButton cancelButton = new JButton("Annulla");

            confirmButton.addActionListener(confirmEvent -> {
                // Salva la data selezionata
                java.util.Date selectedDate = (java.util.Date) dateSpinner.getValue();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(selectedDate);
                dataScelto[0] = calendar;

                // Cambia il testo del bottone per mostrare la data selezionata
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                creaTodoDialog.getDataScadenzaButton().setText(dateFormat.format(selectedDate));

                dateDialog.dispose();
            });

            cancelButton.addActionListener(cancelEvent -> {
                dateDialog.dispose();
            });

            buttonPanel.add(confirmButton);
            buttonPanel.add(cancelButton);

            dateDialog.add(calendarPanel, BorderLayout.CENTER);
            dateDialog.add(buttonPanel, BorderLayout.SOUTH);
            dateDialog.setSize(300, 150);
            dateDialog.setLocationRelativeTo(creaTodoDialog);
            dateDialog.setVisible(true);
        });

        // Action listener per il pulsante Salva
        creaTodoDialog.getSalvaButton().addActionListener(saveEvent -> {
            // Qui va il codice per salvare il nuovo ToDo
            String titoloTodo = creaTodoDialog.getTitoloField().getText();
            String linkTesto = creaTodoDialog.getLinkField().getText();
            String descrizioneTodo = creaTodoDialog.getDescrizioneField().getText();
            // Recupera la bacheca selezionata dal ComboBox
            String bachecaSelezionata = (String) creaTodoDialog.getBachecaBox().getSelectedItem();

            // Validazione basilare
            if (titoloTodo == null || titoloTodo.trim().isEmpty()) {
                JOptionPane.showMessageDialog(creaTodoDialog,
                        "Inserisci un titolo valido per il ToDo",
                        "Errore", JOptionPane.ERROR_MESSAGE);
                return; // Non chiude il dialogo se c'è un errore
            }

            //check del link
            try {
                URI link = null;
                if (linkTesto != null && !linkTesto.trim().isEmpty()) {
                    try {
                        link = new URI(linkTesto);
                    } catch (URISyntaxException ex) {
                        JOptionPane.showMessageDialog(creaTodoDialog,
                                "Il link inserito non è valido. Formato corretto: https://esempio.com",
                                "Errore", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                // Crea il nuovo ToDo con tutti i dati raccolti

                ToDo nuovoToDo = new ToDo(titoloTodo, descrizioneTodo, link, dataScelto[0], coloreScelto[0], immagineScelta[0], null);
                try{
                    toDoDAO.creaToDo(utenteAttuale.getEmail(), nuovoToDo);
                } catch (Exception daoEx) {
                JOptionPane.showMessageDialog(creaTodoDialog,
                        "Errore nel salvataggio del ToDo nel database: " + daoEx.getMessage(),
                        "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }
                //aggiorna bacheche in memoria
                switch (bachecaSelezionata) {
                case "Tempo Libero" -> utenteAttuale.getTempoLibero().getToDoList().add(nuovoToDo);
                case "Lavoro" -> utenteAttuale.getLavoro().getToDoList().add(nuovoToDo);
                case "Università" -> utenteAttuale.getUniversita().getToDoList().add(nuovoToDo);
                default -> {
                    JOptionPane.showMessageDialog(creaTodoDialog,
                            "Seleziona una bacheca",
                            "Errore", JOptionPane.ERROR_MESSAGE);
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
                        "Errore", JOptionPane.ERROR_MESSAGE);
            }
        });

        creaTodoDialog.setVisible(true);

    }

    /**
     * Aggiorna l'interfaccia utente con i dati dell'utente attuale
     * @param mainView MainView della GUI
     */
    private void aggiornaInterfacciaUtente(Main mainView) {
        // Verifica se l'utente attuale è valido
        if (utenteAttuale != null) {
            mainView.setNomeText(utenteAttuale.getEmail());

            // Aggiorna i contenitori per ciascuna bacheca

            // Bacheca Tempo Libero
            Bacheca tempoLibero = utenteAttuale.getTempoLibero();
            if (tempoLibero != null && tempoLibero.getToDoList() != null) {
                JPanel contenitoreToDoT = mainView.getContenitoreToDoT();
                contenitoreToDoT.removeAll(); // Pulisce il contenitore prima di aggiungere nuovi elementi
                for (ToDo todo : tempoLibero.getToDoList()) {
                    if (mostraCompletati || !todo.isCompletato()) {
                        visualizzaToDo(todo, contenitoreToDoT);
                    }
                }
            }

            // Bacheca Università
            Bacheca universita = utenteAttuale.getUniversita();
            if (universita != null && universita.getToDoList() != null) {
                JPanel contenitoreToDoU = mainView.getContenitoreToDoU();
                contenitoreToDoU.removeAll(); // Pulisce il contenitore
                for (ToDo todo : universita.getToDoList()) {
                    if (mostraCompletati || !todo.isCompletato()) {
                        visualizzaToDo(todo, contenitoreToDoU);
                    }
                }
            }

            // Bacheca Lavoro
            Bacheca lavoro = utenteAttuale.getLavoro();
            if (lavoro != null && lavoro.getToDoList() != null) {
                JPanel contenitoreToDoL = mainView.getContenitoreToDoL();
                contenitoreToDoL.removeAll(); // Pulisce il contenitore
                for (ToDo todo : lavoro.getToDoList()) {
                    if (mostraCompletati || !todo.isCompletato()) {
                        visualizzaToDo(todo, contenitoreToDoL);
                    }
                }
            }
        }
    }

    /**
     * Metodo che genera il codice swing per la gui partendo dal todo in memoria
     * @param todo il todo da visualizzare
     * @param contenitoreToDo il contenitore a cui applicare il todo generato
     */
    private void visualizzaToDo(@NotNull ToDo todo, @NotNull JPanel contenitoreToDo) {

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
        
        // Pulsante modifica con icona di modifica (Unicode per pencil)
        JButton modificaButton = new JButton("✏");
        modificaButton.setFont(new Font("Dialog", Font.PLAIN, 14));
        modificaButton.setToolTipText("Modifica");
        modificaButton.setFocusPainted(false);
        modificaButton.setBorderPainted(false);
        modificaButton.setContentAreaFilled(false);
        modificaButton.setForeground(coloreTesto);
        modificaButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        modificaButton.addActionListener(e -> {
            // Crea un'istanza del pannello CreaToDo per la modifica
            CreaToDo modificaTodoDialog = new CreaToDo();
            modificaTodoDialog.setupComponents();
            modificaTodoDialog.setContentPane(modificaTodoDialog.getMainPanel());
            modificaTodoDialog.setLocationRelativeTo(view.getLogInView().getMainView().getMain());

            // Popola i campi con i dati del todo corrente
            modificaTodoDialog.getTitoloField().setText(todo.getTitolo());
            modificaTodoDialog.getDescrizioneField().setText(todo.getDescrizione());

            // Imposta il link se presente
            if (todo.getLink() != null) {
                modificaTodoDialog.getLinkField().setText(todo.getLink().toString());
            }

            // Imposta il colore se presente
            if (todo.getSfondo() != null) {
                modificaTodoDialog.getColorButton().setBackground(todo.getSfondo());
            }

            // Imposta la data se presente
            if (todo.getScadenza() != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                modificaTodoDialog.getDataScadenzaButton().setText(dateFormat.format(todo.getScadenza().getTime()));
            }

            // Carica l'immagine se presente
            if (todo.getImmagine() != null) {
                try {
                    // Carica l'immagine per l'anteprima
                    ImageIcon originalIcon = new ImageIcon(todo.getImmagine());
                    Image originalImage = originalIcon.getImage();

                    // Definisci le dimensioni per l'anteprima
                    int previewWidth = 200;
                    int previewHeight = 150;

                    // Calcola le dimensioni mantenendo le proporzioni
                    int originalWidth = originalIcon.getIconWidth();
                    int originalHeight = originalIcon.getIconHeight();
                    double ratio = (double) originalWidth / originalHeight;

                    int targetWidth, targetHeight;
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
                    modificaTodoDialog.getPreviewPanel().removeAll();
                    JLabel previewLabel = new JLabel(resizedIcon);
                    modificaTodoDialog.getPreviewPanel().add(previewLabel);
                    modificaTodoDialog.getPreviewPanel().setVisible(true);
                    modificaTodoDialog.getPreviewPanel().setBorder(BorderFactory.createTitledBorder("Anteprima immagine"));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(modificaTodoDialog,
                            "Errore nel caricamento dell'anteprima: " + ex.getMessage(),
                            "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }

            // Determina la bacheca corrente del todo
            final String bachecaCorrente;
            if (utenteAttuale.getTempoLibero().getToDoList().contains(todo)) {
                bachecaCorrente = "Tempo libero";
            } else if (utenteAttuale.getLavoro().getToDoList().contains(todo)) {
                bachecaCorrente = "Lavoro";
            } else if (utenteAttuale.getUniversita().getToDoList().contains(todo)) {
                bachecaCorrente = "Università";
            } else {
                bachecaCorrente = null;
            }

            // Imposta la selezione della bacheca
            modificaTodoDialog.getBachecaBox().setSelectedItem(bachecaCorrente);


            // Modifica il comportamento del pulsante Salva per aggiornare il todo esistente
            modificaTodoDialog.getSalvaButton().addActionListener(saveEvent -> {
                // Validazione del titolo
                String nuovoTitolo = modificaTodoDialog.getTitoloField().getText();
                if (nuovoTitolo == null || nuovoTitolo.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(modificaTodoDialog,
                            "Inserisci un titolo valido per il ToDo",
                            "Errore", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Aggiorna i dati del todo
                todo.setTitolo(nuovoTitolo);
                todo.setDescrizione(modificaTodoDialog.getDescrizioneField().getText());

                // Aggiorna il link se necessario
                String nuovoLink = modificaTodoDialog.getLinkField().getText();
                if (nuovoLink != null && !nuovoLink.trim().isEmpty()) {
                    try {
                        todo.setLink(new URI(nuovoLink));
                    } catch (URISyntaxException ex) {
                        JOptionPane.showMessageDialog(modificaTodoDialog,
                                "Il link inserito non è valido. Formato corretto: https://esempio.com",
                                "Errore", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } else {
                    todo.setLink(null);
                }

                // Aggiorna il colore
                Color nuovoColore = modificaTodoDialog.getColorButton().getBackground();
                todo.setSfondo(nuovoColore);

                // Aggiorna l'immagine e altri attributi se necessario
                // ...

                // Gestisci il cambio di bacheca se necessario
                String nuovaBacheca = (String) modificaTodoDialog.getBachecaBox().getSelectedItem();
                if (!nuovaBacheca.equals(bachecaCorrente)) {
                    // Rimuovi dalla bacheca attuale
                    if (bachecaCorrente.equals("Tempo libero")) {
                        utenteAttuale.getTempoLibero().getToDoList().remove(todo);
                    } else if (bachecaCorrente.equals("Lavoro")) {
                        utenteAttuale.getLavoro().getToDoList().remove(todo);
                    } else if (bachecaCorrente.equals("Università")) {
                        utenteAttuale.getUniversita().getToDoList().remove(todo);
                    }

                    // Aggiungi alla nuova bacheca
                    switch (nuovaBacheca) {
                        case "Tempo libero":
                            utenteAttuale.getTempoLibero().getToDoList().add(todo);
                            break;
                        case "Lavoro":
                            utenteAttuale.getLavoro().getToDoList().add(todo);
                            break;
                        case "Università":
                            utenteAttuale.getUniversita().getToDoList().add(todo);
                            break;
                    }
                }

                //salvataggio modifiche nel db
                try{
                    toDoDAO.modificaToDo(utenteAttuale.getEmail(), todo);
                } catch (Exception daoEx) {
                    JOptionPane.showMessageDialog(modificaTodoDialog,
                            "Errore durante il salvataggio nel database: "+ daoEx.getMessage(),
                            "Errore", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Chiudi il dialog e aggiorna l'interfaccia
                modificaTodoDialog.dispose();
                aggiornaInterfacciaUtente(view.getLogInView().getMainView());
                view.revalidate();
                view.repaint();
            });

            // Mostra il dialog di modifica
            modificaTodoDialog.setVisible(true);
        });
        
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
                                "Errore", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
        });
        
        // Aggiungi i pulsanti al pannello dei pulsanti
        buttonsPanel.add(modificaButton);
        buttonsPanel.add(eliminaButton);
        
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
                if (todo.getChecklist().getCompletata()) {
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
            JLabel linkLabel = new JLabel("<html><a href=''>" + todo.getLink() + "</a></html>");
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
                                    "Errore", JOptionPane.ERROR_MESSAGE);
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
            JPanel scadenzaPanel = new JPanel();
            scadenzaPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            scadenzaPanel.setBackground(backgroundColor);

            // Formatta la data
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
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
            todoPanel.add(scadenzaPanel);
        }

        if (todo.getImmagine() != null) {
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
            todoPanel.add(immaginePanel);

        }


        //aggiunta al contenitore
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
            JOptionPane.showMessageDialog(loginView, "Compila tutti i campi.", "Attenzione",
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
                        "Attenzione",
                        JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(loginView,
                    "Email o password non corretti.",
                    "Errore", JOptionPane.ERROR_MESSAGE);
        }
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

        // Registrazione del nuovo utente con bacheche vuote
        try{
            if(utenteDAO.loginValido(email, password)) {
                JOptionPane.showMessageDialog(registerView,
                        "Utente già registrato!",
                        "Attenzione",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            utenteDAO.registraUtente(email,password);

            JOptionPane.showMessageDialog(registerView,
                    "Registrazione avvenuta con successo! Effettua il LogIn",
                    "Successo",
                    JOptionPane.INFORMATION_MESSAGE);
            this.mostraLogin();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(registerView,
                    "Errore durante la registrazione: "+ ex.getMessage(),
                    "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }



    public static void main(String[] args) {
        StileSwing.applicaStile(GestioneDarkMode.isDarkMode());
        SwingUtilities.invokeLater(() -> {
            Scelta view = new Scelta();
            new Controller(view);
        });
    }
}