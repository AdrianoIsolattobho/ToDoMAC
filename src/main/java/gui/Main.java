package gui;

import javax.swing.*;
import java.net.URL;


public class Main extends JPanel {
    private JButton esci;
    private JLabel nome;
    private JPanel main;
    private JButton aggiungiToDo;
    private JPanel baUni;
    private JPanel baLav;
    private JPanel baFre;
    private JPanel navBar;
    private JPanel contenitoreToDoU;
    private JPanel contenitoreToDoL;
    private JPanel contenitoreToDoT;


    /*
     * Setup dei componenti grafici utile da codice per non passare da IntelliJ
     */
    public void setupComponents() {
        esci.setBounds(0, 0, 30, 25);
        URL imageUrl = getClass().getResource("/img/logout.png");
        esci.setIcon(new ImageIcon(imageUrl));
        contenitoreToDoU.setOpaque(false);
        contenitoreToDoL.setOpaque(false);
        contenitoreToDoT.setOpaque(false);
        contenitoreToDoU.setLayout(new BoxLayout(contenitoreToDoU, BoxLayout.Y_AXIS));
        contenitoreToDoL.setLayout(new BoxLayout(contenitoreToDoL, BoxLayout.Y_AXIS));
        contenitoreToDoT.setLayout(new BoxLayout(contenitoreToDoT, BoxLayout.Y_AXIS));

    }



    public JButton getAggiungiToDo() {
        return aggiungiToDo;
    }

    public void setNomeText(String nome) {
        this.nome.setText(nome);
    }


    public JPanel getBaFre() {
        return baFre;
    }

    public JPanel getBaLav() {
        return baLav;
    }

    public JPanel getBaUni() {
        return baUni;
    }

    public JPanel getMain() {
        return this.main;
    }

    public JPanel getContenitoreToDoU() {
        return contenitoreToDoU;
    }

    public JPanel getContenitoreToDoL() {
        return contenitoreToDoL;
    }

    public JPanel getContenitoreToDoT() {
        return contenitoreToDoT;
    }


}