package gui;

import javax.swing.*;

/**
 * Finestra di dialogo per la creazione o modifica di un ToDo.
 * La finestra è strutturata tramite più pannelli Swing per separare logicamente
 * i campi di input: titolo, descrizione, link, scadenza, colore. ecc.
 *
 */
public class CreaToDo extends JDialog {

    //Componenti principali della UI
    private JPanel mainPanel;
    private JTextField titolo;
    private JButton checklistButton;
    private JTextField linkField;
    private JButton colorButton;
    private JButton dataScadenzaButton;
    private JButton salvaButton;

    //Pannelli di layout
    private JPanel savePanel;
    private JPanel inputPanel;
    private JPanel nomePanel;
    private JPanel attivitaPanel;
    private JPanel linkPanel;
    private JPanel scadenzaPanel;
    private JPanel colorePanel;
    private JPanel bachecaPanel;
    private JPanel descrizionePanel;
    private JPanel previewPanel;
    private JPanel pannelloAggiungibile;

    //Campi di input aggiuntivi
    private JComboBox<String> bachecaBox;
    private JTextField descrizioneField;
    private JButton sfogliaButton;


    /**
     * Costruttore: inizializza il contenuto del dialog e imposta le proprietà base.
     */
    public CreaToDo() {
        setContentPane(mainPanel);
        setModal(true);
        getRootPane().setDefaultButton(salvaButton);
        this.setTitle("Modifica Descrizione");
    }

    /* ----------- Metodi di accesso (getters) per i pannelli e componenti ----------- */

    public JPanel getSavePanel() {
        return savePanel;
    }

    public JPanel getInputPanel() {
        return inputPanel;
    }

    public JPanel getNomePanel() {
        return nomePanel;
    }

    public JPanel getAttivitaPanel() {
        return attivitaPanel;
    }

    public JPanel getLinkPanel() {
        return linkPanel;
    }

    public JPanel getScadenzaPanel() {
        return scadenzaPanel;
    }

    public JPanel getColorePanel() {
        return colorePanel;
    }

    public JPanel getBachecaPanel() {
        return bachecaPanel;
    }

    public JComboBox<String> getBachecaBox() {
        return bachecaBox;
    }

    public JTextField getDescrizioneField() {
        return descrizioneField;
    }

    public JPanel getDescrizionePanel() {
        return descrizionePanel;
    }

    public JButton getSfogliaButton() {
        return sfogliaButton;
    }

    public JPanel getPreviewPanel() {
        return previewPanel;
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public JButton getSalvaButton() {
        return salvaButton;
    }

    public JTextField getTitolo() {
        return titolo;
    }

    public JTextField getLinkField() {
        return linkField;
    }

    public JButton getChecklistButton() {
        return checklistButton;
    }

    public JButton getColorButton() {
        return colorButton;
    }

    public JButton getDataScadenzaButton() {
        return dataScadenzaButton;
    }
    public JPanel getPannelloAggiungibile() {
        return pannelloAggiungibile;
    }
}