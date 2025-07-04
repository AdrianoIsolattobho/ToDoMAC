package gui;

import model.Ordinamento;

import javax.swing.*;
import java.awt.event.*;

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

    public GestioneOrdine() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        this.setTitle("Gestione Ordine");


        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
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
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }
}
