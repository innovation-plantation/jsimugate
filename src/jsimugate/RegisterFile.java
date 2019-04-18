package jsimugate;

import java.awt.*;

import static jsimugate.Signal._X;
import static jsimugate.Signal._Z;

/**
 * A register file contains eight registers.
 * Selection and control lines at the bottom, input data on the left, and output data on the right.
 * One control line is for edge-controlled writing, the other is enabling the output.
 */
public class RegisterFile extends Box {

    Pin wClkIn, rdEnaIn;
    private Integer[] qSave = new Integer[]{null, null, null, null, null, null, null, null};
    Signal prevClk = _X;
    Signal[] aSave = new Signal[3], dSave = new Signal[8];

    public RegisterFile() {
        label = "REG FILE";

        resize(7, 8);
        for (int i = 0; i < 8; i++) {
            addPin(wPins.addPinVertically()).left(30).translate(-width - 30, 0);
            addPin(ePins.addPinVertically()).right(30).translate(width + 30, 0);
        }
        for (int i = 0; i < 3; i++) {
            addPin(sPins.addPinHorizontally()).down(30).translate(0, height + 30);
        }
        wClkIn = addPin(new Pin(-width + 20, height + 30).down(30));
        rdEnaIn = addPin(new Pin(width - 20, height + 30).down(30));
    }

    /**
     * Add labels to the pins.
     *
     * @param g graphics context for drawing
     */
    public void drawAtOrigin(Graphics2D g) {
        super.drawAtOrigin(g);
        g.drawString("RD", width - 30, height - 5);
        g.drawString("SEL", -11, height - 5);
        g.drawString("D", -width + 5, 5);
        g.drawString("Q", width - 15, 5);
        g.drawString("W", -width + 15, height - 10);
        g.rotate(-Math.PI / 2);
        g.drawString(">", -height, -width + 24);
    }

    /**
     * Update the outputs and state based on the input pins and the state.
     */
    int sel;
    boolean selValid;
    int inData;
    boolean goodInData;

    public void operate() {
        Signal clk = wClkIn.getInValue();
        Signal rd = rdEnaIn.getInValue();
        if (clk.hi && prevClk.lo) { // handle write pulse
            if (selValid) qSave[sel] = goodInData ? inData : null;
            else for (int i = 0; i < 8; i++) qSave[i] = null;
        }
        if (rd.hi) {
            Integer value = qSave[sel];
            if (selValid) {
                if (value != null) ePins.setValue(value);
                else for (int i = 0; i < 8; i++) ePins.pins.get(i).setOutValue(Signal._U);
            }
            else for (int i = 0; i < 8; i++) ePins.pins.get(i).setOutValue(_X);
        } else for (int i = 0; i < 8; i++) ePins.pins.get(i).setOutValue(_Z);
        prevClk = clk;
        if (selValid = sPins.goodValue()) sel = sPins.getValue();
        if (goodInData = wPins.goodValue()) inData = wPins.getValue();

    }
}
