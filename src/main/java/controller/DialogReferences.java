package controller;

import java.awt.*;
import java.net.URL;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Classe helper per contenere i riferimenti atomici utilizzati nei dialog di creazione/modifica ToDo
 */
public class DialogReferences {
    private final AtomicReference<Color> coloreScelto;
    private final AtomicReference<Calendar> dataScelto;
    private final AtomicReference<URL> immagineScelta;

    public DialogReferences() {
        this.coloreScelto = new AtomicReference<>(Color.WHITE);
        this.dataScelto = new AtomicReference<>();
        this.immagineScelta = new AtomicReference<>();
    }

    public AtomicReference<Color> getColoreScelto() { return coloreScelto; }
    public AtomicReference<Calendar> getDataScelto() { return dataScelto; }
    public AtomicReference<URL> getImmagineScelta() { return immagineScelta; }
}
