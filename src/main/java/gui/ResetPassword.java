package gui;

import controller.Controller;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ResetPassword extends JDialog {
    private JPanel contentPanel;
    private JButton bottoneReset;
    private JTextField email;
    private JPasswordField password;
    private JPasswordField confermaPassword;
    private JButton buttonCancel;


    public ResetPassword() {
        setContentPane(contentPanel);
        setModal(true);
        setTitle("Reset Password");
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        bottoneReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String emailText = email.getText();
                String passwordText = new String(password.getPassword());
                String confermaPasswordText = new String(confermaPassword.getPassword());

                if (emailText.isEmpty() || passwordText.isEmpty() || confermaPasswordText.isEmpty()) {
                    JOptionPane.showMessageDialog(contentPanel, "Inserire tutti i campi", "Errore", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!passwordText.equals(confermaPasswordText)) {
                    JOptionPane.showMessageDialog(contentPanel, "Le password non corrispondono", "Errore", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Controller controller = new Controller("ResetPassword");

                Boolean successo = controller.resetPassword(emailText, passwordText);

                if (successo) {
                    JOptionPane.showMessageDialog(contentPanel, "Password resettata con successo", "Informazione", JOptionPane.INFORMATION_MESSAGE);
                    SwingUtilities.getWindowAncestor(contentPanel).dispose();
                } else {
                    JOptionPane.showMessageDialog(contentPanel, "Errore: utente non trovato", "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        //Più diretto e veloce rispetto a richiamare la finestra genitore con SwingUtilities, possibile farlo perchè questa classe estende JDialog
        buttonCancel.addActionListener(e -> dispose());
    }
}
