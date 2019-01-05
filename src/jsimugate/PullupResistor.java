package jsimugate;

import java.util.Scanner;

/**
 * Implementation of a pull-up reisistor component
 */
public class PullupResistor extends Discrete {

    private Pin pin;
    Signal value;

    /**
     * Create the component
     */
    public PullupResistor() {
        super();
        this.setShape(Artwork.pullupShape());
        this.hitbox = this.shape.getBounds2D();
        this.pin = this.addPin(new Pin(0, 20));
        this.name = "PULLUP";
        setValue(Signal._H);
    }

    /**
     * Set the value stroke and colors for drawing according to the signal
     *
     * @param newValue new signal. May be set to weaker T value than the standard H value.
     */
    public void setValue(Signal newValue) {
        value = newValue;
        color = newValue.fgColor;
        fill = null;
        stroke = newValue.fgStroke;
    }

    /**
     * Make the value strong
     */
    public void increase() {
        setValue(Signal._H);
    }

    /**
     * Make the value weak
     */
    public void decrease() {
        setValue(Signal._Y);
    }

    /**
     * Set the value of the output pin
     */
    public void operate() {
        this.pin.setOutValue(value);
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
        if (value != Signal._H) return value.toString();
        else return super.getDetails();
    }
}