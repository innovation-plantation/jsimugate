package jsimugate;

import java.awt.*;

/**
 * Implementation of an input pin connector
 */
public class OutConnector extends Discrete {
    Pin pin;

    /**
     * Creare a pin at the origin.
     */
    public OutConnector() {
        super();
        setShape(Artwork.ConnectorShape());
        addPin(pin = new Pin(-60, 0).left(30));
        name = "OUTPUT";
        fill = Color.white;
        operate();
    }

    /**
     * Set the input value according to the value from the wire. Display the value on the label.
     */
    public void operate() {
        Signal value = pin.getInValue();
        label = "OUTPUT=" + value.getChar();
        color = value.fgColor;
    }

    public Part reversePolarity() {
        pin.toggleInversion();
        return this;
    }
}
