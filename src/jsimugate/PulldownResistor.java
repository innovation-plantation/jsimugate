package jsimugate;

import java.awt.*;

/**
 * Implementation of a pull-down reisistor component
 */
public class PulldownResistor extends Discrete {

    private Pin pin;

    /**
     * Create the component
     */
    public PulldownResistor() {
        super();
        this.setShape(Artwork.pulldownShape());
        this.hitbox = this.shape.getBounds2D();
        this.color = Color.black;
        this.fill = Color.black;
        this.pin = this.addPin(new Pin(0, -20));
        this.name = "PULLDN";
    }

    /**
     * Set the value of the output pin
     */
    public void operate() {
        this.pin.setOutValue(Signal._L);
    }
}
