package gui;

import javax.swing.*;
import java.awt.*;

public class CreaToDo extends JDialog {
    private JPanel mainPanel;
    private JTextField titolo;
    private JButton attivitaButton;
    private JTextField linkField;
    private JButton colorButton;
    private JButton dataScadenzaButton;
    private JButton salvaButton;
    private JPanel savePanel;
    private JPanel inputPanel;
    private JPanel nomePanel;
    private JPanel attivitaPanel;
    private JPanel linkPanel;
    private JPanel scadenzaPanel;
    private JPanel colorePanel;
    private JPanel bachecaPanel;
    private JComboBox<String> bachecaBox;
    private JTextField descrizioneField;
    private JPanel descrizionePanel;
    private JButton sfogliaButton;
    private JPanel previewPanel;

    public void setupComponents() {
        this.setTitle("Crea nuovo ToDo");
        this.setModal(true);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setContentPane(mainPanel);
        this.pack();
    }

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

    public JButton getAttivitaButton() {
        return attivitaButton;
    }

    public JButton getColorButton() {
        return colorButton;
    }

    public JButton getDataScadenzaButton() {
        return dataScadenzaButton;
    }

}