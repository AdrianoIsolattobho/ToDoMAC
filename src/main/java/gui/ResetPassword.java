package gui;


import javax.swing.*;

/**
 * Finestra di dialogo per il reset della password.
 * Consente di inserire email, nuova password e confermare quest'ultima.
 */
public class ResetPassword extends JDialog {
    private JPanel contentPanel;
    private JButton bottoneReset;
    private JTextField email;
    private JPasswordField password;
    private JPasswordField confermaPassword;
    private JButton buttonCancel;

    /**
     * Costruttore che imposta finestra, dimensioni e comportamento iniziale.
     */
    public ResetPassword() {
        setContentPane(contentPanel);
        setModal(true);
        setTitle("Reset Password");
        setSize(450, 350);
        setupComponents();
        setLocationRelativeTo(null);


        //Più diretto e veloce rispetto a richiamare la finestra genitore con SwingUtilities
        // possibile farlo perchè questa classe estende JDialog

        buttonCancel.addActionListener(_ -> dispose());
    }

    /**
     * Inizializza componenti grafici e stile (placeholder, bordi, trasparenza).
     * Da chiamare dopo l'inizializzazione dei componenti.
     */
    public void setupComponents() {
        if (this.email != null && this.password != null) {
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

    /* ------------ Getter per accedere ai componenti dall'esterno ------------ */
    public JButton getBottoneReset() {
        return bottoneReset;
    }

    public JTextField getEmail() {
        return email;
    }

    public JPasswordField getPassword() {
        return password;
    }

    public JPasswordField getConfermaPassword() {
        return confermaPassword;
    }

    public String getConfermaPasswordText() {
        return new String(confermaPassword.getPassword());
    }

}
