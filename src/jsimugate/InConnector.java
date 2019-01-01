package jsimugate;

import java.awt.*;

/**
 * Implementation of an input pin connector
 */
public class InConnector extends Part {

    Pin pin;
    Signal value = Signal._Z;

    /**
     * Creare a pin at the origin.
     */
    public InConnector() {
        super();
        setShape(Artwork.ConnectorShape());
        addPin(pin = new Pin(85, 0).right(30));
        this.name = "INPUT";
        fill = Color.white;
    }

    /**
     * Set the output value according to the value selected by the user
     */
    public void operate() {
        pin.setOutValue(value);
        label = "INPUT=" + value.getChar();
        this.color = value.fgColor;
    }

    /**
     * The user selects the desired input with a character. If that character is a signal, select it ast the value.
     *
     * @param ch the input character for selecting the input level of the signal '1','0','H','L', etc.
     */
    public void processChar(char ch) {
        for (Signal s : Signal.values()) if (ch == s.getChar()) value = s;
    }
}
