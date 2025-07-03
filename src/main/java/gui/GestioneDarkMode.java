package gui;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class GestioneDarkMode {

    private GestioneDarkMode() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Rileva il Sistema Operativo sul quale viene eseguita l'applicazione.
     * @return boolean
     */
    public static boolean isDarkMode() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return GestioneDarkMode.isDarkModeWindows();
        } else if (os.contains("mac")) {
            return GestioneDarkMode.isDarkModeMac();
        } else {
            throw new UnsupportedOperationException
                    ("Sistema operativo non supportato per il rilevamento della modalità scura.");
        }
    }

    /**
     * Controlla se è attiva la dark mode dai registri di windows
     * @return boolean
     */
    /**
     * Controlla se è attiva la dark mode dai registri di Windows.
     * @return boolean
     */
    public static boolean isDarkModeWindows() {
        try {
            // Creazione del comando tramite ProcessBuilder
            ProcessBuilder builder = new ProcessBuilder(
                    "reg",
                    "query",
                    "HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Themes\\Personalize",
                    "/v",
                    "AppsUseLightTheme"
            );
            builder.redirectErrorStream(true); // Assicura la gestione di errori nello stesso stream
            Process process = builder.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // Cerca "0x1", che corrisponde alla modalità chiara
                    if (line.contains("0x1")) {
                        return false; // Modalità chiara
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true; // Fallback su modalità scura
    }

    /**
     * Controlla se è attiva la dark mode su MacOS.
     * @return boolean
     */
    public static boolean isDarkModeMac() {
        try {
            // Creazione del comando tramite ProcessBuilder
            ProcessBuilder builder = new ProcessBuilder("defaults", "read", "-g", "AppleInterfaceStyle");
            builder.redirectErrorStream(true); // Assicura la gestione di errori nello stesso stream
            Process process = builder.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String result = reader.readLine();
                return result != null && result.equalsIgnoreCase("Dark");
            }
        } catch (Exception _) {
            // Se il comando fallisce o la chiave non esiste, assume modalità chiara
            return false;
        }
    }
}
