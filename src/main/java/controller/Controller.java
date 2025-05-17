package controller;

import gui.Scelta;
import gui.StileSwing;
import gui.LogIn;
import gui.Register;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Controller {

    private Scelta view;

    private void mostraScelta() {
        view.setContentPane(view.getScelta());
        view.pack();
        view.setLocationRelativeTo(null);
        view.revalidate();
        view.repaint();
    }

    private void mostraLogin() {
        view.setContentPane(view.getLogInView().getMainLogIn());
        view.pack();
        view.setLocationRelativeTo(null);
        view.revalidate();
        view.repaint();
    }

    private void mostraRegistrazione() {
        view.setContentPane(view.getRegisterView().getMainRegistrazione());
        view.pack();
        view.setLocationRelativeTo(null);
        view.revalidate();
        view.repaint();
    }

    private void mostraMain() {
        view.setContentPane(view.getLogInView().getMainView().getMain());
        view.pack();
        view.setLocationRelativeTo(null);
        view.revalidate();
        view.repaint();
    }

    /**
     * Costruttore del controller.
     * Inizializza la vista e aggiunge i listener per i pulsanti di login e
     * registrazione.
     *
     * @param view la vista principale
     */
    public Controller(Scelta view) {
        this.view = view;

        // Configurazione dei componenti dopo l'inizializzazione
        view.getLogInView().setupComponents();
        view.getRegisterView().setupComponents();

        view.getLogInButton().addActionListener(e -> {
            this.mostraLogin();

            LogIn loginView = view.getLogInView();
            loginView.getBack().addActionListener(ev -> this.mostraScelta());
            // Gestione click su "entra"
            loginView.getEntra().addActionListener(ev -> gestisciLogin(loginView));
        });

        view.getRegistratiButton().addActionListener(e -> {
            this.mostraRegistrazione();

            Register registerView = view.getRegisterView();
            registerView.getBackButton().addActionListener(ev -> this.mostraScelta());
            // Listener per il pulsante "Registrati"
            registerView.getRegistratiButton().addActionListener(ev -> gestisciRegistrazione(registerView));
        });

    }

    /**
     * Gestisce il login dell'utente.
     * Controlla se i campi sono compilati e mostra un messaggio di errore se non lo
     * sono.
     *
     * @param loginView la vista di login
     */
    private void gestisciLogin(LogIn loginView) {
        String email = loginView.getEmailText();
        String password = loginView.getPasswordText();

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(loginView, "Compila tutti i campi.", "Attenzione",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Qui dovresti mettere la tua logica di verifica
        if (!email.equals("test@email.com") || !password.equals("password")) {
            JOptionPane.showMessageDialog(loginView, "Email o password non corretti.", "Errore",
                    JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(loginView, "Login effettuato!", "Successo", JOptionPane.INFORMATION_MESSAGE);
            this.mostraMain();
        }
    }

    /**
     * Gestisce la registrazione dell'utente.
     * Controlla se i campi sono compilati e mostra un messaggio di successo.
     *
     * @param registerView la vista di registrazione
     */
    private void gestisciRegistrazione(Register registerView) {
        String email = registerView.getEmailText();
        String password = registerView.getPasswordText();
        // Puoi aggiungere altri controlli qui

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(registerView, "Compila tutti i campi.", "Attenzione",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!password.equals(registerView.getConfermaPasswordText())) {
            JOptionPane.showMessageDialog(registerView, "Le password non corrispondono.", "Errore",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Qui puoi aggiungere la logica di salvataggio dell'utente

        JOptionPane.showMessageDialog(registerView, "Registrazione avvenuta con successo! Effettua il login.",
                "Successo", JOptionPane.INFORMATION_MESSAGE);
        this.mostraLogin();
    }

    public static void main(String[] args) {
        StileSwing.applicaStile();
        SwingUtilities.invokeLater(() -> {
            Scelta view = new Scelta();
            new controller.Controller(view);
        });

    }
}
