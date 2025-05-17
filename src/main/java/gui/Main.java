package gui;

import javax.swing.*;

public class Main extends JPanel {
    private JButton esci;
    private JLabel nome;
    private JPanel main;
    private JButton aggiungiBacheca;
    private JPanel baUni;
    private JPanel baLav;
    private JPanel baFre;
    private JButton aggiungiTodo;
    private JPanel navBar;

    public void setupComponents() {
        if (this.esci != null) {
            this.esci.setIcon(new ImageIcon("src/main/java/gui/img/logout.png"));
        }
    }

    public JPanel getMain() {
        return this.main;
    }
}
