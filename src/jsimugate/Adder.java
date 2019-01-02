package jsimugate;

import java.awt.*;
/**
 * Implementation of an adder with carry out
 */
public class Adder extends Part {
    Pin aIn[];
    Pin bIn[];
    Pin out[];
    Pin cOut,cIn;
    int a,b,result;

    public Adder() {
        label = "ADDER";
        setShape(Artwork.adderShape());
        aIn = new Pin[]{
                addPin(new Pin(-80, -20).left(30)),
                addPin(new Pin(-80, -40).left(30)),
                addPin(new Pin(-80, -60).left(30)),
                addPin(new Pin(-80, -80).left(30)),
                addPin(new Pin(-80, -100).left(30)),
                addPin(new Pin(-80, -120).left(30)),
                addPin(new Pin(-80, -140).left(30)),
                addPin(new Pin(-80, -160).left(30)),
        };
        bIn = new Pin[]{
                addPin(new Pin(-80, 160).left(30)),
                addPin(new Pin(-80, 140).left(30)),
                addPin(new Pin(-80, 120).left(30)),
                addPin(new Pin(-80, 100).left(30)),
                addPin(new Pin(-80, 80).left(30)),
                addPin(new Pin(-80, 60).left(30)),
                addPin(new Pin(-80, 40).left(30)),
                addPin(new Pin(-80, 20).left(30)),
        };
        out = new Pin[]{
                addPin(new Pin(80, 80).right(30)),
                addPin(new Pin(80, 60).right(30)),
                addPin(new Pin(80, 40).right(30)),
                addPin(new Pin(80, 20).right(30)),
                addPin(new Pin(80, 0).right(30)),
                addPin(new Pin(80, -20).right(30)),
                addPin(new Pin(80, -40).right(30)),
                addPin(new Pin(80, -60).right(30)),
        };
        cOut = addPin(new Pin(80, -100).right(30));
        cIn = addPin(new Pin(30, 160).down(30));
    }

    /**
     * Get the values of A and B from input pins
     */
    public void getAB() {
        a = Pin.pack(aIn);
        b = Pin.pack(bIn);
    }

    /**
     * Set the result output pins from the result
     */
    public void putResult() {
        Pin.unpack(out, result);
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
     * @param g graphics context for drawing
     */
    public void drawAtOrigin(Graphics2D g) {
        super.drawAtOrigin(g);
        g.drawString(Integer.toString(a),-40,-80);
        g.drawString(Integer.toString(b),-40,95);
        g.drawString(Integer.toString(result),30,15);
        g.drawString("Cin="+cIn.getInValue().asBit(),10,115);
        g.drawString("Cout="+cIn.getInValue().asBit(),0,-95);
    }
}


