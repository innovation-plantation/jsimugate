package jsimugate;

import java.awt.*;

/**
 * Implementation of a logic-ground component
 */
public class VGround extends Discrete {
    private Pin pin;

    /**
     * Create the component
     */
    public VGround() {
        super();
        this.setShape(Artwork.vGroundShape(), 10);
        this.color = Color.black;
        this.fill = Color.black;
        this.pin = this.addPin(new Pin(0, 0).up(15, false));
        this.name = "GND";
    }

    /**
     * Set the value of the output pin
     */
    public void operate() {
        this.pin.setOutValue(Signal._0);
    }
}
