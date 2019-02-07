package jsimugate;

import java.awt.*;
import java.util.ArrayList;

import static jsimugate.Signal.*;

/**
 * Edge triggered D flip-flop with asynchronous reset.
 */
public class EdgeTrigD extends Box {
    Pin clkIn, rstIn;
    private ArrayList<Signal> qSave = new ArrayList<Signal>(),dSave=new ArrayList<Signal>();
    Signal prevClk = _X;

    /**
     * Create the pins and labels on the part and set its size.
     */
    public EdgeTrigD() {
        name = "D EDGE";
        clkIn = addPin(sPins.addPinHorizontally()).down(30).translate(0, 40);
        rstIn = addPin(sPins.addPinHorizontally()).down(30).translate(0, 40);
        resize(); // must resize between adding horizontally and vertically.
        addPin(wPins.addPinVertically()).left(30).translate(-width - 30, 0);
        addPin(ePins.addPinVertically()).right(30).translate(width + 30, 0);
        dSave.add(_X);
        qSave.add(_X);
        resize();
    }

    /**
     * Add labels to the drawing of the shape and pins that were already drawn.
     *
     * @param g graphics context for drawing
     */
    public void drawAtOrigin(Graphics2D g) {
        super.drawAtOrigin(g);
        g.drawString("RST", -20, height - 2);
        g.drawString("D", -width + 5, 5);
        g.drawString("Q", width - 15, 5);
        g.rotate(-Math.PI / 2);
        g.drawString(">", -height, 15);
    }

    /**
     * Set the output based on the input pins and the saved state.
     */
    public void operate() {
        Signal clk = clkIn.getInValue();
        Signal rst = rstIn.getInValue();
        for (int i = 0; i < wPins.size(); i++) {
            Signal d = dSave.get(i);
            dSave.set(i,wPins.pins.get(i).getInValue());
            if (rst.hi) qSave.set(i, _0);
            if (rst.bad || clk.bad) qSave.set(i, _X);
            else if (clk.hi && prevClk.lo) {
                if (d.hi) qSave.set(i, _1);
                else if (d.lo) qSave.set(i, _0);
                else qSave.set(i, _X);
            }
            ePins.pins.get(i).setOutValue(qSave.get(i));
        }
        prevClk = clk;
    }

    /**
     * Adding pins turns a single flip flop into a multi-bit latch.
     */
    public void increase() {
        addPin(wPins.addPinVertically()).left(30).translate(-width - 30, 0);
        addPin(ePins.addPinVertically()).right(30).translate(width + 30, 0);
        dSave.add(_X);
        qSave.add(_X);
        resize();
    }

    /**
     * Reduce the number of pins on the part.
     */
    public void decrease() {
        int n = qSave.size();
        if (n < 2) return;
        if (Net.directConnections(ePins.pins.get(n - 1)).size() > 0) return;
        if (Net.directConnections(wPins.pins.get(n - 1)).size() > 0) return;
        removePin(wPins.removePinVertically());
        removePin(ePins.removePinVertically());
        dSave.remove(dSave.size() - 1);
        qSave.remove(qSave.size() - 1);
        resize();

    }
}
