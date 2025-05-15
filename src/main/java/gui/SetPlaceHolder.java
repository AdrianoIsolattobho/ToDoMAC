package gui;

import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class SetPlaceHolder {
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
                    passwordField.setForeground(java.awt.Color.BLACK);
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

    public static void setTP(JTextField textField, String placeholder) {
        textField.setText(placeholder);
        textField.setForeground(java.awt.Color.GRAY);

        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(java.awt.Color.BLACK);
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
