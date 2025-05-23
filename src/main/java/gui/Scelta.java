package gui;

import javax.swing.*;
import java.awt.*;

public class Scelta extends JFrame {
    private JPanel Scelta;
    private JButton registratiButton;
    private JButton logInButton;
    private JLabel titolo;

    private LogIn loginView;
    private Register registerView;


    public Scelta() {
        setTitle("ToDoApp");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        loginView = new LogIn();
        registerView = new Register();

        // Non chiamare i metodi setupComponents qui, lascialo fare al Controller

        setContentPane(Scelta);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public JPanel getScelta() {
        return this.Scelta;
    }

    public JButton getRegistratiButton() {
        return this.registratiButton;
    }

    public JButton getLogInButton() {
        return this.logInButton;
    }

    public LogIn getLogInView() {
        return this.loginView;
    }

    public Register getRegisterView() {
        return this.registerView;
    }

}
