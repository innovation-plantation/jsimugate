package jsimugate;

import java.awt.*;

/**
 * Implementation of an inversion bubble on a pin that can be set visible (and active) or not.
 */
public class Inversion extends Symbol {

    boolean inverted;

    /**
     * Create the inversion at the desired location
     *
     * @param x horizontal placement
     * @param y vertical placement
     */
    public Inversion(double x, double y) {
        super(x, y);
        setShape(Artwork.bubbleShape());
        setVisible(false);
    }

    /**
     * Activate and display the bubble or deactivate and hide it.
     *
     * @param visible should it be visible or not?
     */
    public void setVisible(boolean visible) {
        inverted = visible;
        if (visible) {
            setFill(Color.white);
            setColor(Color.black);
        } else {
            setFill(null);
            setColor(null);

        }
    }

}
