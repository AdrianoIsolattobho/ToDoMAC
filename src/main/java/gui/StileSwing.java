package gui;

import javax.swing.*;
import java.awt.*;

/**
 * Classe per applicare uno stile personalizzato a Swing.
 * Questa classe definisce un metodo statico per applicare uno stile specifico
 * a componenti Swing come JButton, JLabel e JTextField.
 * Come se fosse un file CSS per le Swing.
 */
public class StileSwing extends JFrame {

    public static void applicaStile() {
        UIManager.put("Button.font", new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        UIManager.put("Button.foreground", java.awt.Color.BLUE);
        UIManager.put("Label.background", new java.awt.Color(50, 50, 50));
        UIManager.put("Label.font", new java.awt.Font("Arial", java.awt.Font.PLAIN, 16));
        UIManager.put("TextField.background", java.awt.Color.WHITE);
        UIManager.put("TextField.foreground", java.awt.Color.BLACK);
        UIManager.put("Label.foreground", new java.awt.Color(240, 240, 240));
        UIManager.put("Panel.background", new java.awt.Color(50, 50, 50));
    }
}