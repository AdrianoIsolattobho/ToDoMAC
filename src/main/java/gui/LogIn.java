package gui;

import javax.swing.*;

public class LogIn extends JFrame {
    private JPanel LogIn;
    private JTextField email;
    private JPasswordField password;
    private JButton entra;
    private JButton passDime;
    private JLabel titolo;
    private JButton back;

    public LogIn() {
        SetPlaceHolder.setTP(this.email, "Email");
        SetPlaceHolder.setPP(this.password, "Password");
    }


    public JPanel getMainLogIn() {
        return this.LogIn;
    }

    public JButton getBackButton() {
        return this.back;
    }

}
