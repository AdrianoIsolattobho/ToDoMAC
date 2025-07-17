package controller;

import java.awt.*;
import java.net.URL;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Classe helper per contenere i riferimenti mutabili e condivisi tra componenti
 * di dialogo per la creazione o modifica di oggetti ToDo.
 * Utilizza {@link AtomicReference} per consentire la modifica sicura e condivisa
 * tra componenti grafici senza bisogno di definire variabili globali o wrapper.
 */
public class DialogReferences {
    private final AtomicReference<Color> coloreScelto;
    private final AtomicReference<Calendar> dataScelto;
    private final AtomicReference<URL> immagineScelta;

    /**
     * Costruttore che inizializza i riferimenti con valori di default/null.
     * -Colore: bianco
     * -Data: null (non selezionato)
     * -Immagine: null (nessuna)
     */
    public DialogReferences() {
        this.coloreScelto = new AtomicReference<>(Color.WHITE);
        this.dataScelto = new AtomicReference<>();
        this.immagineScelta = new AtomicReference<>();
    }

    /* -----Getters----- */
    public AtomicReference<Color> getColoreScelto() { return coloreScelto; }

    public AtomicReference<Calendar> getDataScelto() { return dataScelto; }

    public AtomicReference<URL> getImmagineScelta() { return immagineScelta; }

}
