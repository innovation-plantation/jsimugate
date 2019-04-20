package jsimugate;

import java.awt.*;
import java.awt.geom.AffineTransform;

import static jsimugate.Signal._0;
import static jsimugate.Signal._1;

/**
 * Demultiplexer implementation.
 */
public class DMux extends Box {
    public DMux() {
        label = "DMUX";
        addPin(wPins.addPinVertically()).translate(-width - 30, 0).left(30);
        addPin(ePins.addPinVertically()).translate(width + 30, 0).right(30);

        increase();
        increase();
    }

    /**
     * Increase the number of pins. Double the height, while adding one pin to the width.
     */
    public void increase() {
        if (sPins.size() >= 8) return;
        addPin(sPins.addPinHorizontally()).translate(0, height + 30).down(30);
        resize();
        int n = ePins.size();
        for (int i = 0; i < n; i++) addPin(ePins.addPinVertically()).translate(width + 30, 0).right(30);
        resize();
    }

    /**
     * Decrease the number of pins. Halve the height, while reducing the width by one pin.
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
     * Add label drawing for pins for this part to what is already drawn by the superclass.
     *
     * @param g graphics context for drawing
     */
    public void drawAtOrigin(Graphics2D g) {
        super.drawAtOrigin(g);
        AffineTransform restore = g.getTransform();
        g.drawString("SEL", -11, height - 5);
        String text = Integer.toString(ePins.size() - 1);
        int textWidth = g.getFontMetrics().stringWidth(text);
        g.drawString(text, width - textWidth - 5, -height + 25);
        int n = sPins.size();
        if (n > 2) {
            g.drawString("0", width - 25, height - 5);
            g.drawString(Integer.toString(n - 1), -width + 15, height - 5);
        }
    }

    /**
     * Set output pin values based on input pin values.
     */
    public void operate() {
        Signal in = wPins.pins.get(0).getInValue();
        if (!sPins.goodValue()) {
            for (Pin out : ePins.pins) {
                out.setOutValue(Signal._X);
            }
            return;
        }

        int sel =  sPins.getValue();
        for (int i = 0; i < ePins.size(); i++) {
            if (i != sel) {
                ePins.pins.get(i).setOutValue(_0);
                continue;
            }
            ePins.pins.get(i).setOutValue(in.good?in.not().not():in);

        }
    }
}
