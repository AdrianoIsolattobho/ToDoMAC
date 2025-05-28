package gui;

import controller.Controller;
import javax.swing.*;
import java.awt.*;
import java.net.URL;

/**
 * Classe per applicare uno stile personalizzato a Swing.
 * Questa classe definisce un metodo statico per applicare uno stile specifico
 * a componenti Swing come JButton, JLabel e JTextField.
 * Come se fosse un file CSS per le Swing.
 */
public class StileSwing extends JFrame {


        public static void applicaStile(Boolean getDarkMode) {
            //soluzione universale per cambiare icona sul dock (mac) e toolbar (windows)
            final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
            final URL imageResource = Main.class.getClassLoader().getResource("img/check.png");
            final Image image = defaultToolkit.getImage(imageResource);
            final Taskbar taskbar = Taskbar.getTaskbar();

            try {
                taskbar.setIconImage(image);
            } catch (final UnsupportedOperationException e) {
                System.out.println("The os does not support: 'taskbar.setIconImage'");
            } catch (final SecurityException e) {
                System.out.println("There was a security exception for: 'taskbar.setIconImage'");
            }

            // Imposta lo stile per i componenti Swing
            if (getDarkMode) {
                //button
                UIManager.put("Button.font", new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
                UIManager.put("Button.foreground", java.awt.Color.BLACK);

                //label
                UIManager.put("Label.background", new java.awt.Color(50, 50, 50));
                UIManager.put("Label.font", new java.awt.Font("Arial", java.awt.Font.PLAIN, 16));
                UIManager.put("Label.foreground", new java.awt.Color(240, 240, 240));


                // TextField
                UIManager.put("TextField.foreground", java.awt.Color.WHITE);


                //Panel
                UIManager.put("Panel.background", new java.awt.Color(50, 50, 50));
            } else {
                UIManager.put("Button.font", new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
                UIManager.put("Button.foreground", java.awt.Color.BLACK);

                //label
                UIManager.put("Label.background", new java.awt.Color(245, 245, 245));
                UIManager.put("Label.font", new java.awt.Font("Arial", java.awt.Font.PLAIN, 16));
                UIManager.put("Label.foreground", new java.awt.Color(30,30,30));

                // TextField
                UIManager.put("TextField.foreground", java.awt.Color.BLACK);

                //Password
                UIManager.put("PasswordField.foreground", java.awt.Color.BLACK);

                //Panel
                UIManager.put("Panel.background", new java.awt.Color(245, 245, 245));
            }
        }

}