package controller;

import gui.*;

import model.*;
import org.jetbrains.annotations.NotNull;

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
import java.util.Objects;


/**
 * Controller che gestisce tutte le interazioni tra modello e view
 * come nel paradigma Model-View-Controller (MVC)
 * o Boundary-Control-Entity (BCE)
 */
public class Controller {

    private Scelta view;
    private HashMap<String, Utente> utentiRegistrati;
    private Utente utenteAttuale;
    private boolean mostraCompletati = false;

    //creazioni costanti stringhe seguendo il consiglio di SonarQube
    private static final String UNIVERSITA = "Università";
    private static final String TEMPOLIBERO = "Tempo libero";
    private static final String LAVORO = "Lavoro";
    private static final String DATE_PATTERN = "dd/MM/yyyy";
    private static final String ERRORE = "Errore";

    // Costanti per dimensioni e stringhe
    private static final int ANTEPRIMA_LARGHEZZA = 200;
    private static final int ANTEPRIMA_ALTEZZA = 150;
    private static final String SELEZIONA_IMMAGINE_TITLE = "Seleziona un'immagine";
    private static final String FILTRO_IMMAGINI_DESC = "Immagini (JPG, PNG, GIF)";
    private static final String ANTEPRIMA_IMMAGINE_TITLE = "Anteprima immagine";

    /**
     * Cambia il pannello corrente nella vista principale.
     * Si occupa di impostare il nuovo pannello, ridimensionare la finestra e
     * aggiornare la visualizzazione.
     *
     * @param nuovoPanel il nuovo pannello da visualizzare
     */
    private void cambiaPanel(JPanel nuovoPanel) {
        view.setContentPane(nuovoPanel);
        view.pack();
        view.setLocationRelativeTo(null);
        view.revalidate();
        view.repaint();
    }

    /**
     * Mostra il primo pannello con la scelta tra login e registrazione
     */
    private void mostraScelta() {
        cambiaPanel(view.getScelta());
    }

    /**
     * Mostra il pannello con il login
     */
    private void mostraLogin() {
        cambiaPanel(view.getLogInView().getMainLogIn());
    }

    /**
     * Mostra il pannello con la registrazione
     */
    private void mostraRegistrazione() {
        cambiaPanel(view.getRegisterView().getMainRegistrazione());
    }

    /**
     * Mostra il pannello principale
     */
    private void mostraMain() {
        Main mainView = view.getLogInView().getMainView();
        aggiornaInterfacciaUtente(mainView);
        cambiaPanel(mainView.getMain());
        mainView.getAggiungiToDo().addActionListener(e -> aggiuntaTodo());
        mainView.getMostraCompletati().addActionListener(
                e -> {
                    this.mostraCompletati = !this.mostraCompletati;
                    mainView.getMostraCompletati().setText(mostraCompletati ? "Mostra senza completati" : "Mostra tutti");
                    aggiornaInterfacciaUtente(mainView);
                }
        );
        mainView.getEsci().addActionListener(_ -> mostraScelta());
    }

    /**
     * Ridimensiona un'immagine mantenendo le proporzioni
     *
     * @param immagineOriginale L'immagine da ridimensionare
     * @param larghezzaMax      Larghezza massima
     * @param altezzaMax        Altezza massima
     * @return L'immagine ridimensionata
     */
    private Image ridimensionaImmagine(Image immagineOriginale, int larghezzaMax, int altezzaMax) {
        // Verifica che l'immagine originale non sia null
        if (immagineOriginale == null) {
            return null;
        }

        // Ottieni le dimensioni originali
        int larghezzaOriginale = immagineOriginale.getWidth(null);
        int altezzaOriginale = immagineOriginale.getHeight(null);

        // Verifica che le dimensioni originali siano valide
        if (larghezzaOriginale <= 0 || altezzaOriginale <= 0) {
            return immagineOriginale; // Ritorna l'immagine originale senza ridimensionarla
        }

        // Calcola il rapporto di aspetto
        double rapporto = (double) larghezzaOriginale / altezzaOriginale;

        // Imposta valori predefiniti se i massimi sono 0
        if (larghezzaMax <= 0 && altezzaMax <= 0) {
            // Se entrambi i valori sono nulli o negativi, usa dimensioni di default
            larghezzaMax = 100;
            altezzaMax = 100;
        } else if (larghezzaMax <= 0) {
            // Se solo la larghezza è nulla, calcolala in base all'altezza
            larghezzaMax = (int) (altezzaMax * rapporto);
        } else if (altezzaMax <= 0) {
            // Se solo l'altezza è nulla, calcolala in base alla larghezza
            altezzaMax = (int) (larghezzaMax / rapporto);
        }

        // Calcola le nuove dimensioni mantenendo le proporzioni
        int nuovaLarghezza;
        int nuovaAltezza;
        if (rapporto > 1) {
            // Immagine più larga che alta
            nuovaLarghezza = larghezzaMax;
            nuovaAltezza = (int) Math.max(1, larghezzaMax / rapporto); // Assicura che non sia zero
        } else {
            // Immagine più alta che larga
            nuovaAltezza = altezzaMax;
            nuovaLarghezza = (int) Math.max(1, altezzaMax * rapporto); // Assicura che non sia zero
        }

        return immagineOriginale.getScaledInstance(nuovaLarghezza, nuovaAltezza, Image.SCALE_SMOOTH);
    }

    /**
     * Mostra l'anteprima di un'immagine in un pannello
     *
     * @param dialog            Il dialogo che contiene il pannello
     * @param pannelloAnteprima Il pannello dove mostrare l'anteprima
     * @param urlImmagine       L'URL dell'immagine da mostrare
     */
    private void mostraAnteprimaImmagine(JDialog dialog, JPanel pannelloAnteprima, URL urlImmagine) {
        if (urlImmagine == null || pannelloAnteprima == null) {
            return;
        }

        try {
            // Carica l'immagine
            ImageIcon icona = new ImageIcon(urlImmagine);
            Image immagine = icona.getImage();

            // Verifica che l'immagine sia valida
            if (immagine.getWidth(null) <= 0 || immagine.getHeight(null) <= 0) {
                pannelloAnteprima.removeAll();
                pannelloAnteprima.add(new JLabel("Immagine non valida"));
                pannelloAnteprima.revalidate();
                pannelloAnteprima.repaint();
                return;
            }

            // Ridimensiona l'immagine per l'anteprima
            Image immagineRidimensionata = ridimensionaImmagine(immagine, ANTEPRIMA_LARGHEZZA, ANTEPRIMA_ALTEZZA);

            // Aggiorna il pannello di anteprima
            pannelloAnteprima.removeAll();
            pannelloAnteprima.add(new JLabel(new ImageIcon(immagineRidimensionata)));
            pannelloAnteprima.setBorder(BorderFactory.createTitledBorder(ANTEPRIMA_IMMAGINE_TITLE));
            pannelloAnteprima.revalidate();
            pannelloAnteprima.repaint();

            // Adatta la finestra alle nuove dimensioni
            if (dialog != null) {
                adattaFinestra(dialog);
            }
        } catch (Exception e) {
            // Gestisce errori durante il caricamento
            pannelloAnteprima.removeAll();
            pannelloAnteprima.add(new JLabel("Errore nel caricamento dell'immagine: " + e.getMessage()));
            pannelloAnteprima.revalidate();
            pannelloAnteprima.repaint();
        }
        try {
            // Carica l'immagine e crea l'anteprima
            ImageIcon iconaOriginale = new ImageIcon(urlImmagine);
            Image immagineRidimensionata = ridimensionaImmagine(
                    iconaOriginale.getImage(), ANTEPRIMA_LARGHEZZA, ANTEPRIMA_ALTEZZA);

            // Configura il pannello di anteprima
            pannelloAnteprima.removeAll();
            pannelloAnteprima.add(new JLabel(new ImageIcon(immagineRidimensionata)));
            pannelloAnteprima.setBorder(BorderFactory.createTitledBorder(ANTEPRIMA_IMMAGINE_TITLE));
            pannelloAnteprima.setVisible(true);

            if (dialog != null) {
                adattaFinestra(dialog);
            } else {
                // Gestione dell'errore: può essere utile loggare o sollevare un'eccezione
                throw new IllegalArgumentException("Il parametro 'dialog' non può essere null");
            }

        } catch (Exception ex) {
            mostraErrore(dialog, "Errore nel caricamento dell'anteprima: " + ex.getMessage());
        }
    }

    /**
     * Adatta la dimensione della finestra e la centra
     *
     * @param dialog Il dialogo da adattare
     */
    private void adattaFinestra(@NotNull JDialog dialog) {
        Dimension dimensioneAttuale = dialog.getSize();
        dialog.pack();

        // Mantieni la dimensione più grande tra quella attuale e quella calcolata
        int nuovaLarghezza = Math.max(dimensioneAttuale.width, dialog.getSize().width);
        int nuovaAltezza = Math.max(dimensioneAttuale.height, dialog.getSize().height);
        dialog.setSize(nuovaLarghezza, nuovaAltezza);

        // Centra la finestra
        dialog.setLocationRelativeTo(dialog.getOwner());
        dialog.revalidate();
        dialog.repaint();
    }

    /**
     * Mostra un messaggio di errore
     *
     * @param componente Il componente padre
     * @param messaggio  Il messaggio da mostrare
     */
    private void mostraErrore(Component componente, String messaggio) {
        JOptionPane.showMessageDialog(componente, messaggio, ERRORE, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Metodo che crea il pannello todo per aggiungerlo ciclicamente nella ui
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
            fileChooser.setDialogTitle(SELEZIONA_IMMAGINE_TITLE);
            fileChooser.setFileFilter(new FileNameExtensionFilter(
                    FILTRO_IMMAGINI_DESC, "jpg", "jpeg", "png", "gif"));

            if (fileChooser.showOpenDialog(creaTodoDialog) == JFileChooser.APPROVE_OPTION) {
                try {
                    File selectedFile = fileChooser.getSelectedFile();
                    immagineScelta[0] = selectedFile.toURI().toURL();
                    mostraAnteprimaImmagine(creaTodoDialog, creaTodoDialog.getPreviewPanel(), immagineScelta[0]);
                } catch (Exception ex) {
                    mostraErrore(creaTodoDialog, "Errore nel caricamento dell'immagine: " + ex.getMessage());
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
            JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, DATE_PATTERN);
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
                SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);
                creaTodoDialog.getDataScadenzaButton().setText(dateFormat.format(selectedDate));

                dateDialog.dispose();
            });

            cancelButton.addActionListener(cancelEvent -> dateDialog.dispose());

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
            String titoloTodo = creaTodoDialog.getTitolo().getText();
            String linkTesto = creaTodoDialog.getLinkField().getText();
            String descrizioneTodo = creaTodoDialog.getDescrizioneField().getText();
            // Recupera la bacheca selezionata dal ComboBox
            String bachecaSelezionata = (String) creaTodoDialog.getBachecaBox().getSelectedItem();

            // Validazione basilare
            if (titoloTodo == null || titoloTodo.trim().isEmpty()) {
                JOptionPane.showMessageDialog(creaTodoDialog,
                        "Inserisci un titolo valido per il ToDo",
                        "ERRORE", JOptionPane.ERROR_MESSAGE);
                return; // Non chiude il dialogo se c'è un errore
            }

            // Crea un nuovo ToDo con i dati raccolti
            ToDo nuovoTodo;

            //check del link
            try {
                URI link = creaURI(linkTesto, creaTodoDialog);
                if (linkTesto != null && !linkTesto.trim().isEmpty() && link == null) {
                    return; // Uscita anticipata se la creazione dell'URI è fallita
                }

                // Crea il nuovo ToDo con tutti i dati raccolti
                nuovoTodo = new ToDo(titoloTodo, descrizioneTodo, link, dataScelto[0], coloreScelto[0], immagineScelta[0], null);

                // Aggiungi il ToDo alla bacheca appropriata
                switch (bachecaSelezionata) {
                    case TEMPOLIBERO:
                        utenteAttuale.getTempoLibero().getToDoList().add(nuovoTodo);
                        break;
                    case LAVORO:
                        utenteAttuale.getLavoro().getToDoList().add(nuovoTodo);
                        break;
                    case UNIVERSITA:
                        utenteAttuale.getUniversita().getToDoList().add(nuovoTodo);
                        break;
                    default:
                        //sonarqube richiede un default case ma in questo caso può essere solo uno di questi tre elementi
                        break;
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(creaTodoDialog,
                        "Errore durante la creazione del ToDo: " + ex.getMessage(),
                        ERRORE, JOptionPane.ERROR_MESSAGE);
                return;
            }

            creaTodoDialog.dispose();
            // Aggiorna l'interfaccia
            aggiornaInterfacciaUtente(mainView);
            view.pack();
            view.revalidate();
            view.repaint();
        });

        creaTodoDialog.setVisible(true);

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
     *
     * @param todo            il todo da visualizzare
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
            modificaTodoDialog.getTitolo().setText(todo.getTitolo());
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
                SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);
                modificaTodoDialog.getDataScadenzaButton().setText(dateFormat.format(todo.getScadenza().getTime()));
            }

            // Carica l'immagine se presente
            if (todo.getImmagine() != null) {
                mostraAnteprimaImmagine(modificaTodoDialog, modificaTodoDialog.getPreviewPanel(), todo.getImmagine());
            }

            // Determina la bacheca corrente del todo
            final String bachecaCorrente;
            if (utenteAttuale.getTempoLibero().getToDoList().contains(todo)) {
                bachecaCorrente = TEMPOLIBERO;
            } else if (utenteAttuale.getLavoro().getToDoList().contains(todo)) {
                bachecaCorrente = LAVORO;
            } else if (utenteAttuale.getUniversita().getToDoList().contains(todo)) {
                bachecaCorrente = UNIVERSITA;
            } else {
                bachecaCorrente = null;
            }

            // Imposta la selezione della bacheca
            modificaTodoDialog.getBachecaBox().setSelectedItem(bachecaCorrente);


            // Modifica il comportamento del pulsante Salva per aggiornare il todo esistente
            modificaTodoDialog.getSalvaButton().addActionListener(saveEvent -> {
                // Validazione del titolo
                String nuovoTitolo = modificaTodoDialog.getTitolo().getText();
                if (nuovoTitolo == null || nuovoTitolo.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(modificaTodoDialog,
                            "Inserisci un titolo valido per il ToDo",
                            ERRORE, JOptionPane.ERROR_MESSAGE);
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
                    } catch (URISyntaxException _) {
                        JOptionPane.showMessageDialog(modificaTodoDialog,
                                "Il link inserito non è valido. Formato corretto: https://esempio.com",
                                ERRORE, JOptionPane.ERROR_MESSAGE);
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
                if (!Objects.equals(bachecaCorrente, nuovaBacheca)) {
                    // Rimuovi dalla bacheca attuale
                    if (Objects.equals(bachecaCorrente, TEMPOLIBERO)) {
                        utenteAttuale.getTempoLibero().getToDoList().remove(todo);
                    } else if (Objects.equals(bachecaCorrente, LAVORO)) {
                        utenteAttuale.getLavoro().getToDoList().remove(todo);
                    } else if (Objects.equals(bachecaCorrente, UNIVERSITA)) {
                        utenteAttuale.getUniversita().getToDoList().remove(todo);
                    }

                    // Aggiungi alla nuova bacheca
                    switch (nuovaBacheca) {
                        case TEMPOLIBERO:
                            utenteAttuale.getTempoLibero().getToDoList().add(todo);
                            break;
                        case LAVORO:
                            utenteAttuale.getLavoro().getToDoList().add(todo);
                            break;
                        case UNIVERSITA:
                            utenteAttuale.getUniversita().getToDoList().add(todo);
                            break;
                        default:
                            break;
                    }
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
            // Cerca il ToDo in tutte le bacheche e rimuovilo
            boolean rimosso = false;

            if (utenteAttuale.getTempoLibero().getToDoList().contains(todo)) {
                utenteAttuale.getTempoLibero().getToDoList().remove(todo);
                rimosso = true;
            } else if (utenteAttuale.getLavoro().getToDoList().contains(todo)) {
                utenteAttuale.getLavoro().getToDoList().remove(todo);
                rimosso = true;
            } else if (utenteAttuale.getUniversita().getToDoList().contains(todo)) {
                utenteAttuale.getUniversita().getToDoList().remove(todo);
                rimosso = true;
            }

            if (rimosso) {
                // Aggiorna l'interfaccia
                aggiornaInterfacciaUtente(view.getLogInView().getMainView());
                view.revalidate();
                view.repaint();
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
                    boolean controllo = false;
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
                                    ERRORE, JOptionPane.ERROR_MESSAGE);
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
            SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);
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

            try {
                // Carica l'immagine originale
                Image immagineOriginale = new ImageIcon(todo.getImmagine()).getImage();

                // Verifica che l'immagine sia valida
                if (immagineOriginale.getWidth(null) > 0 && immagineOriginale.getHeight(null) > 0) {
                    // Carica e ridimensiona l'immagine usando il metodo comune
                    final int ALTEZZA_TODO_IMMAGINE = 100;
                    Image immagineRidimensionata = ridimensionaImmagine(
                            immagineOriginale,
                            0, // Larghezza calcolata automaticamente
                            ALTEZZA_TODO_IMMAGINE
                    );

                    if (immagineRidimensionata != null) {
                        immaginePanel.add(new JLabel(new ImageIcon(immagineRidimensionata)));
                        todoPanel.add(immaginePanel);
                    }
                } else {
                    // Messaggio per immagine non valida
                    JLabel erroreLabel = new JLabel("Immagine non valida");
                    erroreLabel.setForeground(coloreTesto);
                    immaginePanel.add(erroreLabel);
                    todoPanel.add(immaginePanel);
                }
            } catch (Exception _) {
                // Gestisce qualsiasi errore durante il caricamento dell'immagine
                JLabel erroreLabel = new JLabel("Errore caricamento immagine");
                erroreLabel.setForeground(Color.RED);
                immaginePanel.add(erroreLabel);
                todoPanel.add(immaginePanel);
            }
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
        this.utentiRegistrati = DatiEsempio.inizializzaDatiEsempio();


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
     * Metodo per scegliere se usare il testo chiaro o scuro in base allo sfondo di personalizzato di un todo
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
        Utente utente = utentiRegistrati.get(email);
        if (utente != null && utente.getPassword().equals(password)) {
            utenteAttuale = utente;
            this.mostraMain();
        } else {
            JOptionPane.showMessageDialog(loginView, "Email o password non corretti.", ERRORE,
                    JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(registerView, "Le password non corrispondono.", ERRORE,
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (utentiRegistrati.containsKey(email)) {
            JOptionPane.showMessageDialog(registerView, "Email già registrata.", ERRORE,
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Registrazione del nuovo utente con bacheche vuote
        Utente nuovoUtente = new Utente(email, password, null, null, null);
        utentiRegistrati.put(email, nuovoUtente);

        JOptionPane.showMessageDialog(registerView, "Registrazione avvenuta con successo! Effettua il login.",
                "Successo", JOptionPane.INFORMATION_MESSAGE);
        this.mostraLogin();
    }

    /**
     * Rileva il Sistema Operativo sul quale viene eseguita l'applicazione.
     *
     * @return boolean
     */
    public static boolean isDarkMode() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return Controller.isDarkModeWindows();
        } else if (os.contains("mac")) {
            return Controller.isDarkModeMac();
        } else {
            throw new UnsupportedOperationException
                    ("Sistema operativo non supportato per il rilevamento della modalità scura.");
        }
    }

    /**
     * Controlla se è attiva la dark mode dai reggistri di windows
     *
     * @return boolean
     */
    public static boolean isDarkModeWindows() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "reg", "query",
                    "HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Themes\\Personalize",
                    "/v", "AppsUseLightTheme"
            );
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("0x1")) {
                    return false; // false = light mode, true = dark mode
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true; // fallback
    }

    /**
     * Controlla se è attiva la dark mode dai AppleInterfaceStyle di MacOS
     *
     * @return boolean
     */
    public static boolean isDarkModeMac() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("defaults", "read", "-g", "AppleInterfaceStyle");
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String result = reader.readLine();
            return result != null && result.equalsIgnoreCase("Dark");
        } catch (Exception _) {
            // Se il comando fallisce o la chiave non esiste, assume modalità chiara
            return false;
        }
    }


    /**
     * Crea il label con il link
     * creata una funzione a parte per non innestare i try come consigliato da SonarQube
     */
    private URI creaURI(String linkTesto, JDialog dialogParent) {
        if (linkTesto != null && !linkTesto.trim().isEmpty()) {
            try {
                return new URI(linkTesto);
            } catch (URISyntaxException _) {
                JOptionPane.showMessageDialog(dialogParent,
                        "Il link inserito non è valido. Formato corretto: https://esempio.com",
                        ERRORE, JOptionPane.ERROR_MESSAGE);
            }
        }
        return null;
    }


    public static void main(String[] args) {
        boolean isDarkMode = Controller.isDarkMode();
        StileSwing.applicaStile(isDarkMode);
        SwingUtilities.invokeLater(() -> {
            Scelta view = new Scelta();
            new Controller(view);
        });
    }
}