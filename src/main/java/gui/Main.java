package gui;

import javax.swing.*;


public class Main extends JPanel {
    private JButton esci;
    private JLabel nome;
    private JPanel mainPanel;
    private JButton aggiungiToDo;
    private JPanel baUni;
    private JPanel baLav;
    private JPanel baFre;
    private JPanel contenitoreToDoU;
    private JPanel contenitoreToDoL;
    private JPanel contenitoreToDoT;
    private JButton mostraCompletati;
    private JLabel descrizioneUni;
    private JLabel descrizioneLav;
    private JLabel descrizioneFre;
    private JButton modificaDescrizioneUni;
    private JButton modificaDescrizioneLav;
    private JButton modificaDescrizioneFre;
    private JButton ordineUniButton;
    private JButton ordineLavButton;
    private JButton ordineFreButton;
    private JPanel baSca;
    private JButton mostraInScadenza;
    private JPanel contenitoreTodoSca;
    private JTextField campoRicerca;
    private JButton buttonRicerca;
    private JPanel baRic;
    private JPanel contenitoreToDoRIc;
    private JButton buttonAzzera;
    private JButton buttonCondivisioni;

    public JButton getButtonCondivisioni() {
        return buttonCondivisioni;
    }

    public JButton getButtonAzzera() {
        return buttonAzzera;
    }

    public JPanel getContenitoreToDoRIc() {
        return contenitoreToDoRIc;
    }

    public JPanel getBaRic() {
        return baRic;
    }

    public JButton getButtonRicerca() {
        return buttonRicerca;
    }

    public JTextField getCampoRicerca() {
        return campoRicerca;
    }

    public JPanel getContenitoreTodoSca() {
        return contenitoreTodoSca;
    }

    public JPanel getBaSca() {
        return baSca;
    }

    public JButton getMostraInScadenza() {
        return mostraInScadenza;
    }

    public JButton getOrdineUniButton() {
        return ordineUniButton;
    }

    public JButton getOrdineLavButton() {
        return ordineLavButton;
    }

    public JButton getOrdineFreButton() {
        return ordineFreButton;
    }

    /*
     * Setup dei componenti grafici utile da codice per non passare da IntelliJ
     */
    public void setupComponents() {
        esci.setBounds(0, 0, 30, 25);

        try {
            if ( GestioneDarkMode.isDarkMode()){
                esci.setIcon(new ImageIcon(getClass().getResource("/img/logout_dark.png")));
            } else {
                esci.setIcon(new ImageIcon(getClass().getResource("/img/logout.png")));
            }
        } catch (Exception _) {
            // Se l'immagine non viene trovata, utilizza del testo al posto dell'icona
            esci.setText("Esci");
            esci.setToolTipText("Esci dall'applicazione");
        }
        contenitoreToDoU.setLayout(new BoxLayout(contenitoreToDoU, BoxLayout.Y_AXIS));
        contenitoreToDoL.setLayout(new BoxLayout(contenitoreToDoL, BoxLayout.Y_AXIS));
        contenitoreToDoT.setLayout(new BoxLayout(contenitoreToDoT, BoxLayout.Y_AXIS));
        contenitoreTodoSca.setLayout(new BoxLayout(contenitoreTodoSca, BoxLayout.Y_AXIS));
        contenitoreToDoRIc.setLayout(new BoxLayout(contenitoreToDoRIc, BoxLayout.Y_AXIS));

        //bacheche visibili solo nel caso in cui sia presente un todo
        baUni.setVisible(false);
        baLav.setVisible(false);
        baFre.setVisible(false);
        baSca.setVisible(false);
        baRic.setVisible(false);

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
        return this.mainPanel;
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

    public JPanel getBaUni() {
        return baUni;
    }

    public JPanel getBaLav() {
        return baLav;
    }

    public JPanel getBaFre() {
        return baFre;
    }

    public JLabel getDescrizioneUni() {
        return descrizioneUni;
    }

    public void setDescrizioneUniText(String descrizioneUniText) {
        this.descrizioneUni.setText(descrizioneUniText);
    }

    public JLabel getDescrizioneLav() {
        return descrizioneLav;
    }

    public void setDescrizioneLavText(String descrizioneLavText) {
        this.descrizioneLav.setText(descrizioneLavText);
    }

    public JLabel getDescrizioneFre() {
        return descrizioneFre;
    }

    public void setDescrizioneFreText(String descrizioneFreText) {
        this.descrizioneFre.setText(descrizioneFreText);
    }
    public JButton getModificaDescrizioneUni() {
        return modificaDescrizioneUni;
    }

    public JButton getModificaDescrizioneLav() {
        return modificaDescrizioneLav;
    }

    public JButton getModificaDescrizioneFre() {
        return modificaDescrizioneFre;
    }
}