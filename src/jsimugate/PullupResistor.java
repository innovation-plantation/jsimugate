package jsimugate;

import java.awt.*;

/**
 * Implementation of a pull-up reisistor component
 */
public class PullupResistor extends Discrete {

    private Pin pin;

    /**
     * Create the component
     */
    public PullupResistor() {
        super();
        this.setShape(Artwork.pullupShape());
        this.hitbox = this.shape.getBounds2D();
        this.color = Color.red;
        this.pin = this.addPin(new Pin(0, 20));
        this.name = "PULLUP";
    }

    /**
     * Set the value of the output pin
     */
    public void operate() {
        this.pin.setOutValue(Signal._H);
    }
}
