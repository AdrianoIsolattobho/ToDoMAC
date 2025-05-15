package gui;

import javax.swing.*;

public class Scelta extends JFrame {
    private JPanel Scelta;
    private JButton registratiButton;
    private JButton logInButton;
    private JLabel titolo;

    public Scelta() {
        setTitle("ToDoApp");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        titolo.setText(
                "<html><div style='color:white; font-size:16px; text-align:center;'>Ciao!<br>Benvenuto nella nostra applicazione Todo!</div></html>");

        setContentPane(Scelta); // Usa il pannello generato dal designer
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        StileSwing.applicaStile();
        SwingUtilities.invokeLater(Scelta::new);

    }
}
