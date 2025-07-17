package gui;

import javax.swing.border.Border;
import java.awt.*;

/**
 * Implementazione personalizzata di {@link Border} che disegna un bordo con angoli arrotondati.
 * Utile per creare componenti Swing con uno stile più moderno e morbido.
 *
 */
public class RoundedBorder implements Border {
    private final int radius;

    /**
     * Costruttore che accetta il raggio di arrotondamento.
     *
     * @param radius il raggio degli angoli del bordo
     */
    public RoundedBorder(int radius) {
        this.radius = radius;
    }

    /**
     * Restituisce i margini del bordo, cioè lo spazio che il bordo occupa attorno al componente.
     *
     * @param c il componente a cui si applica il bordo
     * @return un oggetto {@link Insets} che specifica il padding del bordo
     */
    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(this.radius + 1, this.radius + 1, this.radius + 2, this.radius);
    }

    /**
     * Specifica se il bordo è opaco. Nel caso in cui non lo fosse, permette di visualizzare il contenuto sottostante.
     *
     * @return false, perchè il bordo è trasparente
     */
    @Override
    public boolean isBorderOpaque() {
        return false;
    }

    /**
     * Disegna graficamente il bordo arrotondato attorno al componente.
     *
     * @param c the component for which this border is being painted
     * @param g the paint graphics
     * @param x the x position of the painted border
     * @param y the y position of the painted border
     * @param width the width of the painted border
     * @param height the height of the painted border
     */
    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        g.setColor(Color.WHITE);
        g.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
    }
}
