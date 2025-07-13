package gui;

import javax.swing.*;

public class Scelta extends JFrame {
    private JPanel sceltaPanel;
    private JButton registratiButton;
    private JButton logInButton;

    private LogIn loginView;
    private Register registerView;


    public Scelta() {
        setTitle("ToDoApp");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        loginView = new LogIn();
        registerView = new Register();

        // Non chiamare i metodi setupComponents qui, lascialo fare al Controller

        setContentPane(sceltaPanel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public JPanel getScelta() {
        return this.sceltaPanel;
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
