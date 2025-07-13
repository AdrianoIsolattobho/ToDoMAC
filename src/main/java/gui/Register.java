package gui;

import javax.swing.*;


public class Register extends JPanel {
    private JButton registratiButton;
    private JPasswordField confermaPassword;
    private JPasswordField password;
    private JTextField email;
    private JButton back;
    private JPanel registerPanel;


    // Chiamare questo metodo dopo che il form è stato inizializzato
    public void setupComponents() {
        if (this.email != null && this.password != null && this.confermaPassword != null) {
            SetPlaceHolder.setTP(this.email, "Email", GestioneDarkMode.isDarkMode());
            SetPlaceHolder.setPP(this.password, "Password", GestioneDarkMode.isDarkMode());
            SetPlaceHolder.setPP(this.confermaPassword, "Conferma password", GestioneDarkMode.isDarkMode());

            this.email.setBorder(new RoundedBorder(15));
            this.password.setBorder(new RoundedBorder(15));
            this.confermaPassword.setBorder(new RoundedBorder(15));

            this.email.setOpaque(false);
            this.password.setOpaque(false);
            this.confermaPassword.setOpaque(false);
        }
    }

    public JPanel getMainRegistrazione() {
        return this.registerPanel;
    }

    public JButton getBackButton() {
        return this.back;
    }

    public JButton getRegistratiButton() {
        return this.registratiButton;
    }

    public String getEmailText() {
        return email.getText();
    }

    public String getPasswordText() {
        return new String(password.getPassword());
    }

    public String getConfermaPasswordText() {
        return new String(confermaPassword.getPassword());
    }

}
