package gui;

import javax.swing.*;

public class Register extends JFrame {
    private JButton registratiButton;
    private JPasswordField confermaPassword;
    private JPasswordField password;
    private JTextField email;
    private JLabel titolo;
    private JButton back;
    private JPanel Register;


    public Register() {
        SetPlaceHolder.setTP(this.email, "Email");
        SetPlaceHolder.setPP(this.password, "Password");
        SetPlaceHolder.setPP(this.confermaPassword, "Conferma password");
    }

    public JPanel getMainRegistrazione() {
        return this.Register;
    }

    public JButton getBackButton() {
        return this.back;
    }

}
