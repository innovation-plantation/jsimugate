package jsimugate;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * Create a multiplexer with two selection pins, can be increased during simulation,
 */
public class Mux extends Box {
    public Mux() {
        label = "MUX";
        addPin(ePins.addPinVertically()).translate(width + 30, 0).right(30);
        addPin(wPins.addPinVertically()).translate(-width - 30, 0).left(30);

        increase();
        increase();
    }

    /**
     * Increase  the size by one selection pin and the corresponding required other pins
     * essentially doubling the height of the part
     */
    public void increase() {
        if (sPins.size() >= 8) return;
        addPin(sPins.addPinHorizontally()).translate(0, height + 30).down(30);
        resize();
        int n = wPins.size();
        for (int i = 0; i < n; i++) addPin(wPins.addPinVertically()).translate(-width - 30, 0).left(30);
        resize();
    }

    /**
     * Reduce the size by one selection pin and cut the height about in half in the process.
     */
    public void decrease() {
        int n = wPins.size() >> 1;
        for (int i = n; i < 2 * n; i++) {
            if (Net.directConnections(wPins.pins.get(i)).size() > 0) return;
        }
        if (removePin(sPins.removePinHorizontally()) == null) return;
        resize();
        for (int i = 0; i < n; i++) {
            removePin(wPins.removePinVertically());
        }
        resize();
    }

    /**
     * Draw the labels for pins after super does the rest of the work.
     *
     * @param g graphics context for drawing
     */
    public void drawAtOrigin(Graphics2D g) {
        super.drawAtOrigin(g);
        AffineTransform restore = g.getTransform();
        g.drawString("SEL", -11, height - 5);
        int n = sPins.size();
        if (n > 2) {
            g.drawString("0", width - 25, height - 5);
            g.drawString(Integer.toString(n - 1), -width + 15, height - 5);
        }
        g.drawString(Integer.toString(wPins.size() - 1), -width + 5, -height + 25);
    }

    /**
     * Operate the output pins according to the innput pins.
     */
    public void operate() {
        for (Pin pin : sPins.pins) {
            if (pin.getInValue().bad) {
                ePins.pins.get(0).setOutValue(Signal._X);
                return;
            }
        }
        int n = sPins.getValue();
        Signal out = wPins.pins.get(n).getInValue();
        ePins.pins.get(0).setOutValue(out.not().not());
    }
}
