package gui;

import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * Classe per gestire i placeholder nei JTextField e JPasswordField.
 * Questa classe fornisce metodi per impostare un placeholder in un campo di testo
 * e gestire il comportamento del campo quando guadagna o perde il focus.
 */
public class SetPlaceHolder {

    /**
     * Inserisce un placeholder fittizio in un JTextField.
     * Funzionamento:
     * - Quando il campo di testo guadagna il focus, se il testo è uguale al placeholder,
     *  il testo viene cancellato e il colore del testo diventa nero.
     * - Quando il campo di testo perde il focus, se il testo è vuoto, il placeholder viene
     * ripristinato e il colore del testo diventa grigio.
     *
     * @param passwordField la password field in cui inserire il placeholder
     * @param placeholder   il testo del placeholder
     */
    public static void setPP(JPasswordField passwordField, String placeholder) {
        passwordField.setEchoChar((char) 0);
        passwordField.setText(placeholder);
        passwordField.setForeground(java.awt.Color.GRAY);

        passwordField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (String.valueOf(passwordField.getPassword()).equals(placeholder)) {
                    passwordField.setText("");
                    passwordField.setEchoChar('•'); // o '*'
                    passwordField.setForeground(java.awt.Color.WHITE);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (String.valueOf(passwordField.getPassword()).isEmpty()) {
                    passwordField.setEchoChar((char) 0);
                    passwordField.setText(placeholder);
                    passwordField.setForeground(java.awt.Color.GRAY);
                }
            }
        });
    }

    /**
     * Inserisce un placeholder fittizio in un JTextField.
     * Funzionamento:
     * - Quando il campo di testo guadagna il focus, se il testo è uguale al placeholder,
     *  il testo viene cancellato e il colore del testo diventa nero.
     * - Quando il campo di testo perde il focus, se il testo è vuoto, il placeholder viene
     * ripristinato e il colore del testo diventa grigio.
     *
     * @param textField   la text field in cui inserire il placeholder
     * @param placeholder il testo del placeholder
     */
    public static void setTP(JTextField textField, String placeholder) {
        textField.setText(placeholder);
        textField.setForeground(java.awt.Color.GRAY);

        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(java.awt.Color.WHITE);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                    textField.setForeground(java.awt.Color.GRAY);
                }
            }
        });
    }
}
