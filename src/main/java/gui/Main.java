package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.Image;
import java.net.URL;


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


    /*
     * Setup dei componenti grafici utile da codice per non passare da IntelliJ
     */
    public void setupComponents() {
        esci.setBounds(0, 0, 30, 25);
        URL imageUrl = getClass().getResource("/img/logout.png");
        esci.setIcon(new ImageIcon(imageUrl));


    }

    public JPanel getMain() {
        return this.main;
    }

}