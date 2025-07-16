package gui;

import model.Condivisione;
import model.ToDo;
import model.Utente;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.BiConsumer;

public class PanelCondivisione extends JPanel {
    private JPanel mainPanel;
    private JButton buttonCancel;
    private JPanel listaCondivPanel;
    private JPanel intestazPanel;

    public PanelCondivisione(){
        listaCondivPanel.setLayout(new GridLayout(0, 3,5,5));

    }

    public void aggiornaCondivisioni(List<Condivisione> condivisioni, BiConsumer<ToDo, Utente> listener){
        listaCondivPanel.removeAll();

        //intestazioni
        listaCondivPanel.add(new JLabel("ToDo"));
        listaCondivPanel.add(new JLabel("Condivisi con"));
        listaCondivPanel.add(new JLabel("Rimuovi utente"));


        for(Condivisione c : condivisioni){
            ToDo todo = c.getToDoCondiviso();

            for(Utente u : c.getCondivisiCon()){
                System.out.println("email: "+u.getEmail());
                JLabel titoloLabel = new JLabel(todo.getTitolo());
                JLabel emailLabel = new JLabel(u.getEmail());
                JButton rimuoviButton = new JButton("X");

                rimuoviButton.addActionListener(e->
                        listener.accept(todo, u));

                listaCondivPanel.add(titoloLabel);
                listaCondivPanel.add(emailLabel);
                listaCondivPanel.add(rimuoviButton);
            }
        }
        listaCondivPanel.revalidate();
        listaCondivPanel.repaint();

    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public JButton getButtonCancel() {
        return buttonCancel;
    }
}

