package jsimugate;

import java.awt.*;

/**
 * Create a three-state buffer. Growing it converts it to a rectangular part.
 * Shrinking to one-bit converts it back to a triangular part.
 */
public class ThreeState extends Box {
    Pin ena;

    /**
     * Initially set up the part for one-bit of data with a triangular shape.
     */
    public ThreeState() {
        this.name = "3STATE";
        ena = this.addPin(this.sPins.addPinHorizontally().translate(0, 40).down(30));
        this.addPin(this.wPins.addPinVertically().left(30).translate(-60, 0));
        this.addPin(this.ePins.addPinVertically().right(30).translate(60, 0));
        resize();
        setShape(Artwork.triangleShape());
    }

    /**
     * Increase the part size. Single bit parts are triangular, otherwise it's a box,
     * so that shape conversion depends on the number of pins.
     */
    public void increase() {
        if (wPins.size() == 1) {
            resize();
            this.wPins.pins.get(0).translate(20, 0);
            this.ePins.pins.get(0).translate(-20, 0);
        }
        this.addPin(wPins.addPinVertically().left(30).translate(-50, 0));
        this.addPin(ePins.addPinVertically().right(30).translate(50, 0));
        resize();
    }

    /**
     * Decrease the part size. Single bit parts are triangular, otherwise it's a box,
     * so that shape conversion depends on the number of pins.
     */
    public void decrease() {
        int n = wPins.size();
        if (n < 2) return;
        if (Net.directConnections(ePins.pins.get(n - 1)).size() > 0) return;
        if (Net.directConnections(wPins.pins.get(n - 1)).size() > 0) return;
        this.removePin(wPins.removePinVertically());
        this.removePin(ePins.removePinVertically());
        resize();
        if (wPins.size() > 1) return;
        this.setShape(Artwork.triangleShape());
        this.wPins.pins.get(0).translate(-20, 0);
        this.ePins.pins.get(0).translate(20, 0);
    }

    /**
     * Pin labeling done here - everything else is drawn by super.
     *
     * @param g graphics context for drawing
     */
    public void drawAtOrigin(Graphics2D g) {
        super.drawAtOrigin(g);
        g.drawString("EN", -10, height - 3);
    }

    /**
     * Operate the output based on input pins.
     */
    public void operate() {
        Signal enabled = ena.getInValue();
        if (enabled.bad) {
            for (Pin pin : ePins.pins) pin.setOutValue(Signal._X);
            return;
        }
        if (enabled.lo) {
            for (Pin pin : ePins.pins) pin.setOutValue(Signal._Z);
            return;
        }
        for (int i = 0; i < wPins.size(); i++) {
            ePins.pins.get(i).setOutValue(wPins.pins.get(i).getInValue().not().not());
        }
    }

    /**
     * override to prevent changing to open collector technologies
     */
    public Part asTech(Tech tech) {
        if (tech != Tech.OC && tech != Tech.OC_PNP) return super.asTech(tech);
        return this;
    }
}
