package jsimugate;

import java.awt.*;
import java.util.ArrayList;

import static jsimugate.Signal.*;

/**
 * Level triggered D latch
 */
public class LevelTrigD extends Box {
    Pin clkIn;

    private ArrayList<Signal> qSave = new ArrayList<Signal>();

    /**
     * Create the pins and label for the part, and resize its rectangle accordingly.
     */
    public LevelTrigD() {
        name = "D LEV";
        clkIn = addPinS();
        resize();
        addPinsWE(1);
        qSave.add(_X);
        resize();
    }

    /**
     * Draw the labels on the pins. Everything else is drawn by the superclass.
     *
     * @param g graphics context for drawing
     */
    public void drawAtOrigin(Graphics2D g) {
        super.drawAtOrigin(g);
        g.drawString("CLK", -10, height - 5);
        g.drawString("D", -width + 5, 5);
        g.drawString("Q", width - 15, 5);
    }

    /**
     * Set the outputs based on input pins and state.
     */
    public void operate() {
        Signal clk = clkIn.getInValue();
        for (int i = 0; i < wPins.size(); i++) {
            Signal d = wPins.pins.get(i).getInValue();
            if (clk.bad) qSave.set(i, _X);
            else if (clk.hi) {
                if (d.hi) qSave.set(i, _1);
                else if (d.lo) qSave.set(i, _0);
                else qSave.set(i, _X);
            }
            ePins.pins.get(i).setOutValue(qSave.get(i));
        }
    }

    /**
     * Increase the number of bits in the latch
     */
    public void increase() {
        addPinsWE(1);
        qSave.add(_X);
        resize();
    }

    /**
     * Decrease the number of bits in the latch.
     */
    public void decrease() {
        int n = qSave.size();
        if (n < 2) return;
        if (Net.directConnections(ePins.pins.get(n - 1)).size() > 0) return;
        if (Net.directConnections(wPins.pins.get(n - 1)).size() > 0) return;
        removePin(wPins.removePinVertically());
        removePin(ePins.removePinVertically());
        qSave.remove(qSave.size() - 1);
        resize();

    }
}
