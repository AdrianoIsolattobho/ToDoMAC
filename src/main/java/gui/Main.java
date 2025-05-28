package gui;

import javax.swing.*;


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
    private JButton mostraCompletati;


    /*
     * Setup dei componenti grafici utile da codice per non passare da IntelliJ
     */
    public void setupComponents() {
        esci.setBounds(0, 0, 30, 25);

        try {
            esci.setIcon(new ImageIcon(getClass().getResource("/img/logout.png")));
        } catch (Exception e) {
            // Se l'immagine non viene trovata, utilizza del testo al posto dell'icona
            esci.setText("Esci");
            esci.setToolTipText("Esci dall'applicazione");
        }
        contenitoreToDoU.setLayout(new BoxLayout(contenitoreToDoU, BoxLayout.Y_AXIS));
        contenitoreToDoL.setLayout(new BoxLayout(contenitoreToDoL, BoxLayout.Y_AXIS));
        contenitoreToDoT.setLayout(new BoxLayout(contenitoreToDoT, BoxLayout.Y_AXIS));

    }

    public JButton getEsci() {
        return esci;
    }

    public JButton getMostraCompletati() {
        return mostraCompletati;
    }

    public JButton getAggiungiToDo() {
        return aggiungiToDo;
    }

    public void setNomeText(String nome) {
        this.nome.setText(nome);
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