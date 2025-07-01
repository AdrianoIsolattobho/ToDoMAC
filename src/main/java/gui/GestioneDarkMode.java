package gui;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class GestioneDarkMode {

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
    public static boolean isDarkModeWindows() {
        try {
            Process process = Runtime.getRuntime().exec(
                    "reg query \"HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Themes\\Personalize\" /v AppsUseLightTheme"
            );
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("0x1")) {
                    return false; // false = light mode, true = dark mode
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true; // fallback
    }

    /**
     * Controlla se è attiva la dark mode dai AppleInterfaceStyle di MacOs
     * @return boolean
     *
     */
    public static boolean isDarkModeMac() {
        try {
            Process process = Runtime.getRuntime().exec("defaults read -g AppleInterfaceStyle");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String result = reader.readLine();
            return result != null && result.equalsIgnoreCase("Dark");
        } catch (Exception e) {
            // Se il comando fallisce o la chiave non esiste, assume modalità chiara
            return false;
        }
    }
}
