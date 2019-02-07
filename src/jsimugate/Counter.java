package jsimugate;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * Sequential up-counter implementation
 */
public class Counter extends Box {
    Pin ld, rst, clk;
    int number;
    Signal oldClk = null;
    int dSave;
    Signal ldSave;

    public Counter() {
        label = "CTR";

        rst = addPin(sPins.addPinHorizontally()).translate(0, 40).down(30);
        clk = addPin(sPins.addPinHorizontally()).translate(0, 40).down(30);
        ld = addPin(sPins.addPinHorizontally()).translate(0, 40).down(30);
        resize();
        for (int i = 0; i < 8; i++) increase();
    }

    /**
     * Make it a bigger counter with more bits
     */
    public void increase() {
        if (ePins.size() > 30) return;
        addPin(ePins.addPinVertically()).translate(width + 30, 0).right(30);
        addPin(wPins.addPinVertically()).translate(-width - 30, 0).left(30);
        resize();
    }

    /**
     * Make it a smaller counter with fewer bits
     */
    public void decrease() {
        if (Net.directConnections(ePins.pins.get(ePins.size() - 1)).size() > 0) return;
        if (Net.directConnections(wPins.pins.get(wPins.size() - 1)).size() > 0) return;
        removePin(wPins.removePinVertically());
        removePin(ePins.removePinVertically());
        resize();
    }

    /**
     * Draw the pin labels on the part
     *
     * @param g graphics context for drawing
     */
    public void drawAtOrigin(Graphics2D g) {
        super.drawAtOrigin(g);
        AffineTransform restore = g.getTransform();
        g.rotate(-Math.PI / 2);
        g.translate(-height, -20);
        int h = ePins.size();
        if (h > 1) g.drawString("LD", 2, 4);
        g.translate(0, 20);
        g.drawString(h > 2 ? "> CLK" : ">", 0, 4);
        g.translate(0, 20);
        if (ePins.size() > 0) g.drawString("RST", 2, 4);
        g.setTransform(restore);
        g.translate(width - 20, height - 15);
        g.setTransform(restore);
        g.translate(width - 1, height - 15);
        for (int i = 0; i < ePins.size(); i++) {
            String text = Integer.toString(1 << i);
            int textWidth = g.getFontMetrics().stringWidth(text);
            g.drawString(text, -textWidth, -i * 20);
        }
    }

    /**
     * Reset the counter
     */
    public void reset() {
        number = 0;
        oldClk = clk.getInValue();
    }

    /**
     * Regular update interface to the I/O pins
     */
    public void operate() {
        if (rst.getInValue().hi) {
            reset();
            for (Pin pin : ePins.pins) {
                pin.setOutValue(Signal._0);
            }
            return;
        }
        Signal newClk = clk.getInValue();
        if (oldClk == null || !oldClk.good || !newClk.good || !rst.getInValue().good) {
            oldClk = null;
            for (int i = 0; i < ePins.size(); i++) {
                ePins.pins.get(i).setOutValue(Signal._X);
            }
            return;
        }
        if (oldClk.lo && newClk.hi) {
            if (oldClk.hi != clk.getInValue().hi) {
                number++;
                number %= 1 << ePins.size();
                if (ldSave.hi) number = dSave;
            }
            ePins.setValue(number);
        }
        oldClk = newClk;
        dSave = wPins.getValue();
        ldSave = ld.getInValue();
    }
}
