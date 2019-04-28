package jsimugate;

import java.awt.geom.Rectangle2D;

/**
 * Box shaped parts that can resize their shape and draw their own boxes.
 * All pins in pin groups are adjusted when the size of the box is adjusted.
 * The assumption is that the pins were drawn according to the old box size
 * so resize() needs to be called after adding pins to east/west groups before
 * adding to north/south groups and vice versa.
 */
public class Box extends Part {
    int height = 0, width = 0;

    PinGroup ePins = new PinGroup();
    PinGroup sPins = new PinGroup();
    PinGroup wPins = new PinGroup();
    PinGroup nPins = new PinGroup();


    /**
     * Cakculate the box size for zero pins. This will be used as a basis for
     * adjusting next time it gets resized because the difference between the
     * box sizes is the amount that pins need to be moved away when growing,
     * or back in when shrinking.
     */
    public Box() {
        resize();
    }

    /**
     * Derived classes are could be created like new Box().resize(10,20);
     *
     * @param w width to accommodate w pins
     * @param h height to accommodate h pins
     * @return
     */
    public Box resize(int w, int h) {
        int newWidth = 10 * (w + 1), newHeight = 10 * (h + 1);
        int dw = newWidth - width;
        int dh = newHeight - height;
        setShape(new Rectangle2D.Double(-newWidth, -newHeight, 2 * newWidth, 2 * newHeight));
        for (Pin pin : ePins.pins) pin.translate(dw, 0);
        for (Pin pin : sPins.pins) pin.translate(0, dh);
        for (Pin pin : wPins.pins) pin.translate(-dw, 0);
        for (Pin pin : nPins.pins) pin.translate(0, -dh);
        height += dh;
        width += dw;
        return this;
    }

    /**
     * Resize to accommodate the pins list.
     *
     * @return
     */
    public Box resize() {
        int w = Math.max(nPins.size(), sPins.size());
        int h = Math.max(ePins.size(), wPins.size());
        return resize(w, h);
    }

    /**
     * Resize to accommodate the pins list.
     *
     * @return
     */
    public Box resizeWithPadding(int horizontal_pad, int vertical_pad) {
        int w = Math.max(nPins.size(), sPins.size());
        int h = Math.max(ePins.size(), wPins.size());
        return resize(w + horizontal_pad, h + vertical_pad);
    }

    public Pin addAuxPinS(int offset, int length) {
        return addPin(new Pin(offset, height + length).down(length));
    }

    public Pin addAuxPinS(int offset) {
        return addAuxPinS(offset, 30);
    }

    public Pin addPinN(int length) {
        return addPin(nPins.addPinHorizontally()).translate(0, -height - length).up(length);
    }

    public Pin addPinS(int length) {
        return addPin(sPins.addPinHorizontally()).translate(0, height + length).down(length);
    }

    public Pin addPinE(int length) {
        return addPin(ePins.addPinVertically()).translate(width + length, 0).right(length);
    }

    public Pin addPinW(int length) {
        return addPin(wPins.addPinVertically()).translate(-width - length, 0).left(length);
    }

    public Pin addPinN() {
        return addPinN(30);
    }

    public Pin addPinS() {
        return addPinS(30);
    }

    public Pin addPinE() {
        return addPinE(30);
    }

    public Pin addPinW() {
        return addPinW(30);
    }

    public void addPinsN(int n) {
        for (int i = 0; i < n; i++) addPinN();
    }

    public void addPinsS(int n) {
        for (int i = 0; i < n; i++) addPinS();
    }

    public void addPinsE(int n) {
        for (int i = 0; i < n; i++) addPinE();
    }

    public void addPinsW(int n) {
        for (int i = 0; i < n; i++) addPinW();
    }

    public void addPinsWE(int n) {
        for (int i = 0; i < n; i++) {
            addPinW();
            addPinE();
        }
    }

    public void addPinsNS(int n) {
        for (int i = 0; i < n; i++) {
            addPinN();
            addPinS();
        }
    }
}

