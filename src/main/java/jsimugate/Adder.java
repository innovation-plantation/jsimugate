package jsimugate;

import java.awt.*;

/**
 * Implementation of an adder with carry out
 */
public class Adder extends Part {
    PinGroup aIn = new PinGroup();
    PinGroup bIn = new PinGroup();
    PinGroup out = new PinGroup();
    Pin cOut, cIn;
    int a, b, result; // numerical values for pin groups

    public Adder() {
        label = "ADDER";
        setShape(Artwork.adderShape());

        for (int i = 0; i < 8; i++) {
            addPin(aIn.addPinVertically().translate(-80, -90).left(30));
            addPin(bIn.addPinVertically().translate(-80, 90).left(30));
            addPin(out.addPinVertically().translate(80, 10).right(30));
        }

        cOut = addPin(new Pin(80, -100).right(30));
        cIn = addPin(new Pin(30, 160).down(30));
    }

    /**
     * Get the values of A and B from input pins
     */
    public void getAB() {
        a = aIn.getValue();
        b = bIn.getValue();
    }

    /**
     * Set the result output pins from the result
     */
    public void putResult() {
        out.setValue(result);
    }

    /**
     * Perform the addition
     * on the input pin groups a and b,
     * The out pins and the carry pin receive the output.
     */
    public void operate() {
        getAB();
        result = a + b + cIn.getInValue().asBit();
        int cy = result & 0x100;
        cOut.setOutValue(Signal.fromBit(cy));
        putResult();
    }

    /**
     * Draw the input and output decimal values in addition to the normal rendering.
     *
     * @param g graphics context for drawing
     */
    public void drawAtOrigin(Graphics2D g) {
        super.drawAtOrigin(g);
        g.drawString(Integer.toString(a), -40, -80);
        g.drawString(Integer.toString(b), -40, 95);
        g.drawString(Integer.toString(result), 30, 15);
        g.drawString("Cin=" + cIn.getInValue().asBit(), 10, 115);
        g.drawString("Cout=" + cIn.getInValue().asBit(), 0, -95);
    }
}


