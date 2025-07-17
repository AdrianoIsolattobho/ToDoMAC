package gui;

import model.Utente;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ListaUtenti extends JDialog {
    private JPanel condiviPanel;
    private JPanel utentiPanel;
    private JTextField cercaField;
    private JPanel cercaPanel;
    private JPanel salvaPanel;
    private JButton condivButton;

    private ArrayList<JCheckBox> utentiCb = new ArrayList<>();



    public ListaUtenti() {
        setContentPane(condiviPanel);
        setModal(true);
        setTitle("Condividi con");
        setSize(450, 350);
        setupComponents();
        setLocationRelativeTo(null);

        //Listener per la barra di ricerca
        cercaField.getDocument().addDocumentListener(new DocumentListener() {
            private void aggiornaFiltro(){
                String filtro = cercaField.getText().toLowerCase();
                for (JCheckBox utenteCb : utentiCb) {
                    utenteCb.setVisible(utenteCb.getText().toLowerCase().contains(filtro));
                }
                utentiPanel.revalidate();
                utentiPanel.repaint();
            }
            public void insertUpdate(DocumentEvent e) {aggiornaFiltro();}
            public void removeUpdate(DocumentEvent e) {aggiornaFiltro();}
            public void changedUpdate(DocumentEvent e) {aggiornaFiltro();}
        });

    }

    public void setupComponents(){
        //imposta il placeholder nei campi con dark mode o meno
        SetPlaceHolder.setTP(this.cercaField, "Cerca utente...", GestioneDarkMode.isDarkMode());
        //applica bordi arrotondati personalizzati ai campi
        this.cercaField.setBorder(new RoundedBorder(15));
        //rende i campi trasparenti per una visiva migliore con lo sfondo
        this.cercaField.setOpaque(false);
    }

    //metodo per mostrare utenti
  public void mostraUtenti(List<Utente> utenti, Utente utenteAttuale){
    utentiPanel.removeAll();
    utentiCb.clear();

    int rows = Math.max(utenti.size(), 1);
    utentiPanel.setLayout(new GridLayout(rows, 1,5,5));

    String emailUtenteAttuale = (utenteAttuale != null) ? utenteAttuale.getEmail() : null;

    for(Utente u : utenti){
        // Verifica che l'utente corrente non sia nullo e che entrambe le email siano valide
        if(u != null && u.getEmail() != null && emailUtenteAttuale != null 
           && !u.getEmail().equals(emailUtenteAttuale)){
            JCheckBox cb = new JCheckBox(u.getEmail());
            utentiCb.add(cb);
            utentiPanel.add(cb);
        }
    }

    utentiPanel.revalidate();
    utentiPanel.repaint();
}

    //metodo per ottenere gli utenti selezionati
    public List<String> getUtentiSelezionati(){
        List<String> selezionati = new ArrayList<>();
        for(JCheckBox cb : utentiCb){
            if(cb.isSelected()){
                selezionati.add(cb.getText());
            }
        }
        return selezionati;
    }

    public JPanel getCondiviPanel() {
        return condiviPanel;
    }

    public JButton getCondivButton() {
        return condivButton;
    }
}
