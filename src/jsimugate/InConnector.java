package jsimugate;

import java.awt.*;

/**
 * Implementation of an input pin connector
 */
public class InConnector extends Discrete {

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
        setValue(Signal._Z);
        fill = Color.white;
    }

    /**
     * Select the desired input signal.
     *
     * @param s the level of the signal _1,_0,_H,_L, etc.
     */
    public void setValue(Signal s) {
        value = s;
        this.color = value.fgColor;
    }

    /**
     * The user selects the desired input with a character. If that character is a signal, select it as the value.
     *
     * @param ch the input character for selecting the input level of the signal '1','0','H','L', etc.
     */
    public void processChar(char ch) {

        for (Signal s : Signal.values()) if (ch == s.getChar()) setValue(s);
        label = "INPUT=" + value.getChar();
    }

    /**
     * Set the output value according to the value selected by the user
     */
    public void operate() {
        pin.setOutValue(value);
    }
}
