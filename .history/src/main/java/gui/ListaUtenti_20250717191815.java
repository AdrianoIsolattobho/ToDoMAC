package gui;


import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.ArrayList;

/**
 * Finestra di dialogo per la condivisione di contenuti con altri utenti.
 * Permette la selezione di utenti tramite checkbox e la ricerca filtrata per nome.
 *
 * Componenti principali:
 * -Campo di ricerca per filtrare utenti
 * -Lista dinamica di JCheckBox rappresentanti utenti
 * -Pulsante per confermare la condivisione
 */
public class ListaUtenti extends JDialog {
    private JPanel condiviPanel;
    private JPanel utentiPanel;
    private JTextField cercaField;
    private JPanel cercaPanel;
    private JPanel salvaPanel;
    private JButton condivButton;


    /**
     * Costruttore che inizializza la finestra, imposta dimensioni, stile e listener di ricerca.
     */
    public ListaUtenti() {
        setContentPane(condiviPanel);
        setModal(true);
        setTitle("Condividi con");
        setSize(450, 350);
        setupComponents();
        setLocationRelativeTo(null);

    }



    /**
     * Configura lo stile del campo di ricerca:
     * -Placeholder visivo
     * -Bordi arrotondati
     * -Sfondo trasparente
     */
    public void setupComponents(){
        //imposta il placeholder nei campi con dark mode o meno
        SetPlaceHolder.setTP(this.cercaField, "Cerca utente...", GestioneDarkMode.isDarkMode());
        //applica bordi arrotondati personalizzati ai campi
        this.cercaField.setBorder(new RoundedBorder(15));
        //rende i campi trasparenti per una visiva migliore con lo sfondo
        this.cercaField.setOpaque(false);
    }

    /* ------------ Getter per accedere ai componenti dall'esterno ------------ */
    public JTextField getCercaField() {
        return cercaField;
    }

    public JPanel getUtentiPanel() {
        return utentiPanel;
    }

    public JPanel getCondiviPanel() {
        return condiviPanel;
    }

    public JButton getCondivButton() {
        return condivButton;
    }


}
