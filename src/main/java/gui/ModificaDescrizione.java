package gui;

import javax.swing.*;
import java.awt.event.*;

/**
 * Finestra di dialogo per modificare una descrizione di una bacheca.
 * Contiene un campo di testo, un pulsante OK e un pulsante Annulla.
 *
 */
public class ModificaDescrizione extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField textField;

    /**
     * Costruttore che imposta i listener e l'interfaccia
     */
    public ModificaDescrizione() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonCancel.addActionListener(e -> dispose());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(
                e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
        );
    }


    public void setDescrizione(String descrizione) {
        this.textField.setText(descrizione);
    }

    public JTextField getTextField() {
        return this.textField;
    }

    public String getTextFieldText() {
        return this.textField.getText();
    }

    public JButton getButtonOK() {
        return buttonOK;
    }
}