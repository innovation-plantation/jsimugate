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

    /**
     * Create the register file.
     */
    public RegisterFile() {
        label = "REG FILE";
        resize(11, 8);
        addPinsWE(8);
        addPinsS(3);
        sPins.shiftPinsHorizontally(-40);
        wClkIn = addAuxPinS(-width + 20);
        rdEnaIn = addAuxPinS(width - 20);
        addExtraPinsS(3);
        sExtraPins.shiftPinsHorizontally(40);
    }

    /**
     * Add labels to the pins.
     *
     * @param g graphics context for drawing
     */
    public void drawAtOrigin(Graphics2D g) {
        super.drawAtOrigin(g);
        for (int i=0;i<8;i++) g.drawString(qSave[i]==null?Integer.toString(i)+":?":String.format("%d:%x",i,qSave[i] ),-97+i%4*52, -52+i/4*25);
        g.drawRect(-100,-70,200,50);
        g.drawString("RD", width - 30, height - 5);
        g.drawString("SEL", -50, height - 5);
        g.drawString("SEL", 30, height - 5);
        g.drawString("D", -width + 5, 5);
        g.drawString("Q", width - 15, 5);
        g.drawString("W", -width + 15, height - 10);
        g.rotate(-Math.PI / 2);
        g.drawString(">", -height, -width + 24);

    }

    /**
     * Update the outputs and state based on the input pins and the state.
     */
    int wSel,rSel;
    boolean wSelValid,rSelValid;
    int inData;
    boolean goodInData;

    public void operate() {
        Signal clk = wClkIn.getInValue();
        Signal rd = rdEnaIn.getInValue();
        if (clk.hi && prevClk.lo) { // handle write pulse
            if (wSelValid) qSave[wSel] = goodInData ? inData : null;
            else for (int i = 0; i < 8; i++) qSave[i] = null;
        }
        if (rd.hi) {
            Integer value = qSave[rSel];
            if (rSelValid) {
                if (value != null) ePins.setValue(value);
                else for (int i = 0; i < 8; i++) ePins.pins.get(i).setOutValue(Signal._U);
            } else for (int i = 0; i < 8; i++) ePins.pins.get(i).setOutValue(_X);
        } else for (int i = 0; i < 8; i++) ePins.pins.get(i).setOutValue(_Z);
        prevClk = clk;
        if (wSelValid = sPins.goodValue()) wSel = sPins.getValue();
        if (rSelValid = sExtraPins.goodValue()) rSel = sExtraPins.getValue();
        if (goodInData = wPins.goodValue()) inData = wPins.getValue();

    }

    public void increase() {

    }
}
