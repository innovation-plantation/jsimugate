package jsimugate;

import java.awt.*;
import java.util.Scanner;

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
        label = "INPUT=" + value.getChar();
    }

    /**
     * The user selects the desired input with a character. If that character is a signal, select it as the value.
     *
     * @param ch the input character for selecting the input level of the signal '1','0','H','L', etc.
     */
    public void processChar(char ch) {

        for (Signal s : Signal.values()) if (ch == s.getChar()) setValue(s);
        if (ch == ' ' || ch == '?' && Math.random() < .5) reversePolarity();
    }

    /**
     * Set the output value according to the value selected by the user
     */
    public void operate() {
        pin.setOutValue(value);
    }

    /**
     * deserialize
     *
     * @param details formatted like 0Hz or 0Sec if value>1
     */
    public void setDetails(String details) {
        Scanner scanner = new Scanner(details);
        if (scanner.hasNext()) {
            setValue(Signal.valueOf(scanner.next()));
            Log.println(this.value.toString());
        }
    }

    /**
     * deserialize
     *
     * @return details formatted like 0Hz or 0Sec if value>1
     */
    public String getDetails() {
        if (value != Signal._Z) return value.toString();
        else return super.getDetails();
    }

    public Part reversePolarity() {
        pin.toggleInversion();
        return this;
    }

}
