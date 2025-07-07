package gui;


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
    private static final String FONT_SCELTO = "Arial";


        public static void applicaStile() {
            //soluzione universale per cambiare icona sul dock (mac) e toolbar (windows)
            // Versione più sicura del tuo codice
            //soluzione universale per cambiare icona sul dock (mac) e toolbar (windows)
            final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
            final URL imageResource = Main.class.getClassLoader().getResource("img/check.png");
            final Image image = defaultToolkit.getImage(imageResource);

            try {
                // Verifica prima se la Taskbar è supportata sulla piattaforma
                if (Taskbar.isTaskbarSupported()) {
                    final Taskbar taskbar = Taskbar.getTaskbar();

                    // Verifica se l'impostazione dell'icona è supportata
                    if (taskbar.isSupported(Taskbar.Feature.ICON_IMAGE)) {
                        taskbar.setIconImage(image);
                    } else {
                        System.out.println("La funzionalità ICON_IMAGE non è supportata su questa piattaforma");
                    }
                } else {
                    System.out.println("Taskbar non supportata su questa piattaforma");
                }
            } catch (UnsupportedOperationException e) {
                // La funzionalità Taskbar non è supportata su questa piattaforma
                System.err.println("Taskbar non supportata su questa piattaforma: " + e.getMessage());
            } catch (Exception e) {
                // Gestisce altri possibili errori
                System.err.println("Errore durante l'impostazione dell'icona: " + e.getMessage());
            }





            // Imposta lo stile per i componenti Swing
            if (GestioneDarkMode.isDarkMode()) {
                //button
                UIManager.put("Button.font", new java.awt.Font(FONT_SCELTO, java.awt.Font.BOLD, 14));
                UIManager.put("Button.foreground", new java.awt.Color(1, 167, 225));

                //label
                UIManager.put("Label.foreground",new java.awt.Color(1, 167, 225) );
                UIManager.put("Label.background", new java.awt.Color(18, 41, 75));
                UIManager.put("Label.font", new java.awt.Font(FONT_SCELTO, java.awt.Font.PLAIN, 16));

                UIManager.put("TextField.foreground", new java.awt.Color(1, 167, 225));

                //Password
                UIManager.put("PasswordField.foreground", new java.awt.Color(1, 167, 225));


                //Panel
                UIManager.put("Panel.background", new java.awt.Color(18, 41, 75));
            } else {
                UIManager.put("Button.font", new java.awt.Font(FONT_SCELTO, java.awt.Font.BOLD, 14));
                UIManager.put("Button.foreground", java.awt.Color.BLACK);

                //label
                UIManager.put("Label.background", new java.awt.Color(245, 245, 245));
                UIManager.put("Label.font", new java.awt.Font(FONT_SCELTO, java.awt.Font.PLAIN, 16));
                UIManager.put("Label.foreground", new java.awt.Color(18, 41, 75));


                UIManager.put("TextField.foreground", new java.awt.Color(18, 149, 216));
                // TextField
                UIManager.put("TextField.foreground", java.awt.Color.BLACK);

                //Password
                UIManager.put("PasswordField.foreground", java.awt.Color.BLACK);

                //Panel
                UIManager.put("Panel.background", new java.awt.Color(245, 245, 245));
            }
        }

}