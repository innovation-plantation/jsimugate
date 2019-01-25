package jsimugate;

import java.util.Scanner;

/**
 * Implementation of a pull-down reisistor component
 */
public class PulldownResistor extends Discrete {

    Pin pin;
    Signal value;

    /**
     * Create the component
     */
    public PulldownResistor() {
        super();
        this.setShape(Artwork.pulldownShape());
        this.hitbox = this.shape.getBounds2D();
        this.pin = this.addPin(new Pin(0, -20));
        this.name = "PULLDN";
        setValue(Signal._L);
        opposite=PullupResistor.class.getSimpleName();
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
        setValue(Signal._L);
    }

    /**
     * Make the value weak
     */
    public void decrease() {
        setValue(Signal._N);
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
        if (value != Signal._L) return value.toString();
        else return super.getDetails();
    }
    public Part reversePolarity() {
        pin.transform.setToTranslation(0,20);
        PullupResistor newPart = (PullupResistor)super.reversePolarity();
        newPart.pin = pin;
        return newPart;
    }
}
