package gui;

import javax.swing.*;
import java.awt.event.*;

/**
 * Finestra di dialogo per gestire l'ordinamento delle attività.
 * L'Utente può scegliere tra diversi criteri di ordinamento:
 * Alfabetico A-Z o Z-A
 * Data di creazione (crescente o decrescente)
 * Data di scadenza (crescente o decrescente)
 * La selezione viene fatta tramite radio button, raggruppati per assicurare una sola scelta alla volta.
 *
 */
public class GestioneOrdine extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JRadioButton ordineAlfabeticoAZRadioButton;
    private JRadioButton ordineAlfabeticoZARadioButton;
    private JRadioButton dataDiCreazioneCrescenteRadioButton;
    private JRadioButton dataDiCreazioneDecrescenteRadioButton;
    private JRadioButton dataScadenzaCrescenteRadioButton;
    private JRadioButton dataScadenzaDecrescenteRadioButton;
    private ButtonGroup group;

    /**
     * Costruttore della finestra di gestione ordine.
     * Inizializza componenti, imposta la finestra come modale,
     * gestisce gli eventi di chiusura e tasto ESC.
     *
     */
    public GestioneOrdine() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        this.setTitle("Gestione Ordine");


        buttonCancel.addActionListener(_->
                dispose()
        );

        // call onCancel() when cross is clicked

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        // Inizializza e raggruppa i RadioButton
        this.group = new ButtonGroup();
        group.add(ordineAlfabeticoAZRadioButton);
        group.add(ordineAlfabeticoZARadioButton);
        group.add(dataDiCreazioneCrescenteRadioButton);
        group.add(dataDiCreazioneDecrescenteRadioButton);
        group.add(dataScadenzaCrescenteRadioButton);
        group.add(dataScadenzaDecrescenteRadioButton);

        ordineAlfabeticoAZRadioButton.setActionCommand("AZ");
        ordineAlfabeticoZARadioButton.setActionCommand("ZA");
        dataDiCreazioneCrescenteRadioButton.setActionCommand("CreazioneCrescente");
        dataDiCreazioneDecrescenteRadioButton.setActionCommand("CreazioneDecrescente");
        dataScadenzaCrescenteRadioButton.setActionCommand("ScadenzaCrescente");
        dataScadenzaDecrescenteRadioButton.setActionCommand("ScadenzaDecrescente");


        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(_-> dispose(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    /* ------------ Getter per accedere ai componenti dall'esterno ------------ */
    public ButtonGroup getGroup() {
        return group;
    }

    public JButton getButtonOK() {
        return buttonOK;
    }

    public JRadioButton getOrdineAlfabeticoAZRadioButton() {
        return ordineAlfabeticoAZRadioButton;
    }

    public JRadioButton getOrdineAlfabeticoZARadioButton() {
        return ordineAlfabeticoZARadioButton;
    }

    public JRadioButton getDataDiCreazioneCrescenteRadioButton() {
        return dataDiCreazioneCrescenteRadioButton;
    }

    public JRadioButton getDataDiCreazioneDecrescenteRadioButton() {
        return dataDiCreazioneDecrescenteRadioButton;
    }

    public JRadioButton getDataScadenzaCrescenteRadioButton() {
        return dataScadenzaCrescenteRadioButton;
    }

    public JRadioButton getDataScadenzaDecrescenteRadioButton() {
        return dataScadenzaDecrescenteRadioButton;
    }


}
