package gui;

import javax.swing.*;

/**
 * Classe che rappresenta la schermata iniziale dell'applicazione.
 * Offre due opzioni all'utente: registrarsi come nuovo utente o accedere tramite il login.
 */
public class Scelta extends JFrame {
    private JPanel sceltaPanel;
    private JButton registratiButton;
    private JButton logInButton;

    private LogIn loginView;
    private Register registerView;

    /**
     * Costruttore: inizializza le viste secondarie (login e registrazione),
     * imposta la finestra e mostra il pannello principale.
     */
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

    /* ------------ Getter per accedere ai componenti dall'esterno ------------ */
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
