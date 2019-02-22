package jsimugate;

import java.awt.*;

/**
 * Implementation of a logic-ground component
 */
public class VGround extends Discrete {
    Pin pin;

    /**
     * Create the component
     */
    public VGround() {
        super();
        this.setShape(Artwork.vGroundShape(), 10);
        this.color = Color.black;
        this.fill = null;
        this.pin = this.addPin(new Pin(0, 0).up(15, false));
        this.name = "GND";
        opposite = VSource.class.getSimpleName();
    }

    /**
     * Set the value of the output pin
     */
    public void operate() {
        this.pin.setOutValue(Signal._0);
    }

    public Part reversePolarity() {
        this.pin.transform.setToTranslation(0, 5);
        this.pin.down(15, false);
        VSource newPart = (VSource) super.reversePolarity();
        newPart.pin = pin;
        return newPart;
    }
}
