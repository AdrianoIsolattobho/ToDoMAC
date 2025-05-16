package gui;

import javax.swing.*;

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
        setContentPane(Scelta);
        pack();
        setLocationRelativeTo(null);
        setVisible(true); 
    }

    public JPanel getScelta() {
        return Scelta;
    }

    public JButton getRegistratiButton() {
        return registratiButton;
    }

    public JButton getLogInButton() {
        return logInButton;
    }


   public void mostraLogin() {
        setContentPane(loginView.getMainLogIn());
        pack(); // Adatta la finestra al nuovo pannello
        setLocationRelativeTo(null); // Centra la finestra
        revalidate();
        repaint();
    }

    public void mostraRegistrazione() {
        setContentPane(registerView.getMainRegistrazione());
        pack(); // Adatta la finestra al nuovo pannello
        setLocationRelativeTo(null); // Centra la finestra
        revalidate();
        repaint();
    }

    public LogIn getLogInView() {
        return loginView;
    }

    public Register getRegisterView() {
        return registerView;
    }

    public void mostraScelta() {
        setContentPane(Scelta); // "Scelta" è il pannello principale
        pack(); // Adatta la finestra al nuovo pannello
        setLocationRelativeTo(null); // Centra la finestra
        revalidate();
        repaint();
    }
    public static void main(String[] args) {
        StileSwing.applicaStile();
        SwingUtilities.invokeLater(() -> {
            Scelta view = new Scelta();
            new controller.Controller(view);

        });

    }
}
