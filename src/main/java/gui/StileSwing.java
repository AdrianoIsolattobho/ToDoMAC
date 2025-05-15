package gui;

import javax.swing.*;

public class StileSwing extends JFrame {
    public static void applicaStile() {
        UIManager.put("Button.font", new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        UIManager.put("Button.foreground", java.awt.Color.BLUE);
        UIManager.put("lab.background", new java.awt.Color(50, 50, 50));
        UIManager.put("Label.font", new java.awt.Font("Arial", java.awt.Font.PLAIN, 16));
        UIManager.put("Label.foreground", new java.awt.Color(240, 240, 240));
        UIManager.put("Panel.background", new java.awt.Color(50, 50, 50));
    }
}