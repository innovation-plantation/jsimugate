package jsimugate;

import java.awt.*;
import java.awt.geom.AffineTransform;

import static jsimugate.Signal._0;
import static jsimugate.Signal._1;

/**
 * A binary decoder part implementation
 */
public class Decoder extends Box {
    /**
     * Create a new decoder (2 to 4)
     */
    public Decoder() {
        label = "DEC";
        addPinE();
        increase();
        increase();
    }

    /**
     * Increase the number of selection input pins by 1, doubling the number of output pins
     */
    public void increase() {
        if (sPins.size() >= 8) return;
        addPinS();
        resize();
        int n = ePins.size();
        for (int i = 0; i < n; i++) addPinE();
        resize();
    }

    /**
     * Reduce the number of selection input pins by 1, halving the number of output pins
     */
    public void decrease() {
        int n = ePins.size() >> 1;
        for (int i = n; i < 2 * n; i++) {
            if (Net.directConnections(ePins.pins.get(i)).size() > 0) return;
        }
        if (removePin(sPins.removePinHorizontally()) == null) return;
        resize();
        for (int i = 0; i < n; i++) {
            removePin(ePins.removePinVertically());
        }
        resize();
    }

    /**
     * Add the pin labels to the part
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
        g.translate(width - 1, height - 15);
        for (int i = 0; i < ePins.size(); i++) {
            String text = Integer.toString(i);
            int textWidth = g.getFontMetrics().stringWidth(text);
            g.drawString(text, -textWidth, -i * 20);
        }
    }

    /**
     * Operation of a decoder. One bit high depending on the selection, or all X out if the input is unknown.
     */
    public void operate() {
        for (Pin pin : sPins.pins) {
            if (pin.getInValue().bad) {
                for (Pin out : ePins.pins) {
                    out.setOutValue(Signal._X);
                }
                return;
            }
        }
        for (int i = 0; i < ePins.size(); i++) {
            ePins.pins.get(i).setOutValue(i == sPins.getValue() ? _1 : _0);
        }
    }
}


