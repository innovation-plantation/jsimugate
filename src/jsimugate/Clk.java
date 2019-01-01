package jsimugate;

import java.awt.geom.Rectangle2D;

/**
 * Implementation of clock that toggles on and off at 1Hz rate or as adjusted by
 * doubling the frequency up to 64 Hz with the increase() function or
 * halving the frequency down to 64 second period with the decrease() function.
 * The schematic label is kept updated according to the frequency.
 */
public class Clk extends Part {

    static final Rectangle2D rect = new Rectangle2D.Double(-40, -20, 60, 40);
    private Pin pin;
    Signal value = Signal._0;
    private javax.swing.Timer timer;
    int hz = 1, sec = 1;

    /**
     * Create the part with the internal value initially toggling off and on at a 1Hz period
     */
    public Clk() {
        super();
        setShape(rect);
        label = "CLK";
        addPin(pin = new Pin(45, 0).right(25));
        timer = new javax.swing.Timer(500, e -> {
            value = value == Signal._0 ? Signal._1 : Signal._0;
        });
        timer.start();
    }

    /**
     * Update the pin with the internal signal value
     */
    public void operate() {
        pin.setOutValue(value);
    }

    /**
     * Adjust the clock rate keep the label in sync with it
     */
    public void adjustClock() {
        timer.setDelay(500 * sec / hz);
        if (hz > sec) label = hz + " Hz";
        else if (sec > hz) label = sec + " sec";
        else label = "CLK";
    }

    /**
     * Double the frequency, while updating the label to indicate the new frequency.
     * Do not exceed 64s period limit.
     */
    public void increase() {
        if (sec > 1) sec /= 2;
        else if (hz < 64) hz *= 2;
        adjustClock();
    }

    /**
     * Half the frequency, while updating the label to indicate the new frequency.
     * Do not exceed 64 Hz limit.
     */
    public void decrease() {
        if (hz > 1) hz /= 2;
        else if (sec < 64) sec *= 2;
        adjustClock();
    }
}
