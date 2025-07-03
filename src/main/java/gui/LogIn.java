package gui;

import javax.swing.*;


public class LogIn extends JPanel {
    private JTextField email;
    private JPasswordField password;
    private JButton passDime;
    private JButton entra;
    private JLabel titolo;
    private JButton back;
    private Main mainView;
    private JPanel logInPanel;

    public LogIn() {
        // Lascia vuoto il costruttore o sposta l'inizializzazione dopo la creazione del form
        mainView = new Main();
    }

    // Chiamare questo metodo dopo che il form è stato inizializzato
    public void setupComponents() {
        if (this.email != null && this.password != null) {
            SetPlaceHolder.setTP(this.email, "Email", GestioneDarkMode.isDarkMode());
            SetPlaceHolder.setPP(this.password, "Password", GestioneDarkMode.isDarkMode());

            this.email.setBorder(new RoundedBorder(15));
            this.password.setBorder(new RoundedBorder(15));

            this.email.setOpaque(false);
            this.password.setOpaque(false);

            // Chiama setupComponents sulla mainView se necessario
            mainView.setupComponents();
        }
    }


    public JTextField getEmail() {
        return email;
    }

    public JPasswordField getPassword() {
        return password;
    }

    public JButton getPassDime() {
        return passDime;
    }

    public JButton getEntra() {
        return entra;
    }

    public JLabel getTitolo() {
        return titolo;
    }

    public JButton getBack() {
        return back;
    }

    public String getEmailText() {
        return email.getText();
    }

    public String getPasswordText() {
        return new String(password.getPassword());
    }

    public Main getMainView() {
        return mainView;
    }

    public JPanel getMainLogIn() {
        return this.logInPanel;
    }

}
