package jsimugate;

import java.awt.*;

/**
 * Implementation of a voltage-source component
 */
public class VSource extends Discrete {
    Pin pin;

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
        opposite = VGround.class.getSimpleName();
    }

    /**
     * Set the value of the output pin
     */
    public void operate() {
        this.pin.setOutValue(Signal._1);
    }

    public Part reversePolarity() {
        this.pin.transform.setToTranslation(0, 0);
        this.pin.up(15, false);
        VGround newPart = (VGround) super.reversePolarity();
        newPart.pin = pin;
        return newPart;
    }
}
