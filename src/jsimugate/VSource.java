package jsimugate;

import java.awt.*;

/**
 * Implementation of a voltage-source component
 */
public class VSource extends Discrete {
    private Pin pin;

    /**
     * Create the component
     */
    public VSource() {
        super();
        this.setShape(Artwork.vSourceShape(), 10);
        this.color = Color.red;
        this.fill = Color.red;
        this.pin = this.addPin(new Pin(0, 5).down(15, false));
        this.name = "SOURCE";
    }

    /**
     * Set the value of the output pin
     */
    public void operate() {
        this.pin.setOutValue(Signal._1);
    }
}
