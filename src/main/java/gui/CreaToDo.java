package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CreaToDo extends JDialog {
    private JPanel mainPanel;
    private JTextField titoloField;
    private JTextArea descrizioneArea;
    private JButton salvaButton;
    private JButton annullaButton;
    private JComboBox<String> prioritaCombo;
    private JSpinner dataScadenzaSpinner;

    public CreaToDo(Window parent) {
        super(parent, "Crea nuovo ToDo", ModalityType.APPLICATION_MODAL);

        // Inizializzazione componenti
        mainPanel = new JPanel(new BorderLayout(10, 10));
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 5, 5));

        titoloField = new JTextField();
        descrizioneArea = new JTextArea(5, 20);
        prioritaCombo = new JComboBox<>(new String[]{"Bassa", "Media", "Alta"});
        dataScadenzaSpinner = new JSpinner(new SpinnerDateModel());

        formPanel.add(new JLabel("Titolo:"));
        formPanel.add(titoloField);
        formPanel.add(new JLabel("Descrizione:"));
        formPanel.add(new JScrollPane(descrizioneArea));
        formPanel.add(new JLabel("Priorità:"));
        formPanel.add(prioritaCombo);
        formPanel.add(new JLabel("Data scadenza:"));
        formPanel.add(dataScadenzaSpinner);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        salvaButton = new JButton("Salva");
        annullaButton = new JButton("Annulla");

        buttonPanel.add(salvaButton);
        buttonPanel.add(annullaButton);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Configurazione pulsanti
        annullaButton.addActionListener(e -> dispose());

        // Configurazione dialog
        setContentPane(mainPanel);
        setSize(400, 300);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    public JButton getSalvaButton() {
        return salvaButton;
    }

    public String getTitolo() {
        return titoloField.getText();
    }

    public String getDescrizione() {
        return descrizioneArea.getText();
    }

    public String getPriorita() {
        return (String) prioritaCombo.getSelectedItem();
    }

    public Object getDataScadenza() {
        return dataScadenzaSpinner.getValue();
    }
}