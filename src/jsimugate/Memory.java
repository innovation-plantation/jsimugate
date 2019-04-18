package jsimugate;

import java.awt.*;
import java.util.concurrent.ConcurrentSkipListMap;

import static jsimugate.Signal._X;
import static jsimugate.Signal._Z;

/**
 * Implementation of RAM memory component
 */
public class Memory extends Box {
    Pin wClkIn, rdEnaIn;
    private ConcurrentSkipListMap<Long, Integer> qSave = new ConcurrentSkipListMap<Long, Integer>();
    Signal prevClk = _X;

    /**
     * Construct a new RAM part. Label, pins created here.
     */
    public Memory() {
        name = "RAM";
        rdEnaIn = addPin(sPins.addPinHorizontally()).down(30).translate(0, height + 30);
        wClkIn = addPin(sPins.addPinHorizontally()).down(30).translate(0, height + 30);
        resize();
        for (int i = 0; i < 8; i++) {
            addPin(ePins.addPinVertically()).right(30).translate(width + 30, 0);
        }
        for (int i = 0; i < 8; i++) {
            addPin(wPins.addPinVertically()).left(30).translate(-width - 30, 0);
        }
        resize();
    }

    /**
     * Add RAM-specific pin labeling to the part
     *
     * @param g graphics context for drawing
     */
    public void drawAtOrigin(Graphics2D g) {
        super.drawAtOrigin(g);
        g.drawString("RD", width - 30, height - 5);
        g.drawString("A", -width + 5, 5);
        g.drawString("D", width - 15, 5);
        g.drawString("W", -width + 15, height - 10);
        g.drawString("RAM", -15, -15);
        g.rotate(-Math.PI / 2);
        g.drawString(">", -height, -width + 24);
    }

    /**
     * Operate the the memory interface to the I/O pins. This gets called continuously at regular intervals.
     */
    public void operate() {
        Signal clk = wClkIn.getInValue();
        Signal rd = rdEnaIn.getInValue();
        long sel = wPins.getLongValue();
        boolean selValid = wPins.goodValue();
        if (clk.hi && prevClk.lo) { // handle write pulse
            boolean goodInData = ePins.goodValue();
            if (selValid) {
                if (goodInData) qSave.put(sel, ePins.getValue());
                else qSave.remove(sel);
            } else qSave.clear();
            Log.print("WR data " + (goodInData ? "+" : "*") + ePins.getValue());
            Log.println(" to " + (selValid ? "+" : "*") + sel);
        }
        if (rd.hi) {
            Integer value = qSave.get(sel);
            if (selValid) {
                if (value != null) ePins.setValue(value);
                else for (int i = 0; i < 8; i++) ePins.pins.get(i).setOutValue(Signal._U);
            } else for (int i = 0; i < 8; i++) ePins.pins.get(i).setOutValue(_X);
        } else for (int i = 0; i < 8; i++) ePins.pins.get(i).setOutValue(_Z);
        prevClk = clk;
    }

    /**
     * Grow the address bus
     */
    public void increase() {
        if (wPins.size() < 63) addPin(wPins.addPinVertically()).left(30).translate(-width - 30, 0);
        resize();
    }

    /**
     * Shrink the address bus
     */
    public void decrease() {
        removePin(wPins.removePinVertically());
        resize();
    }
}
