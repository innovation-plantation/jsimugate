package jsimugate;

import java.awt.*;
import java.awt.geom.GeneralPath;

/**
 * Implementation of a PNP transistor. The emitter and base are pins, and the collector is an output.
 * If the emitter is higher than the base, then its output is delivered to the collector,
 * otherwise the collector is not driven.
 */
public class PNPTransistor extends Transistor {
    static final GeneralPath arrow = new GeneralPath(), tail = new GeneralPath(), arrowPNPMET = new GeneralPath();

    /**
     * Set up path in advance for these shapes.
     */
    {
    	this.color = Signal._H.fgColor;
    	morphName = PNPMultiTransistor.class.getSimpleName();
    	opposite = NPNTransistor.class.getSimpleName();
    }
    static {    	
        tail.moveTo(-10, 40);
        tail.lineTo(-7.5, 35);
        // PNP arrow (points inwards from the bottom left)
        arrow.moveTo(-8,  36);
        arrow.lineTo(-3.5,40.5);
        arrow.lineTo(-5,  30);
        arrow.lineTo(-14, 34.5);
        arrow.closePath();
        // PMP MET arrow 
	    arrowPNPMET.moveTo(  -6   +12,-12   +6);
        arrowPNPMET.lineTo(  -10.5+12,-9    +6);
        arrowPNPMET.lineTo(  -0   +12,-8    +6);
        arrowPNPMET.lineTo(  -4.5 +12,-18   +6);
        arrowPNPMET.closePath();
    }

    /**
     * Create the transistor as PNP type
     */
    public PNPTransistor() {
        super();
        name = "PNP";
        tt = Logic.pnp_tt;
        tt_met = Logic.met_pnp_tt;
        color = Color.red;
        opposite = NPNTransistor.class.getSimpleName();
        resistorMET = Artwork.pulldownShape();
        baseColorMET = Signal._L.fgColor;
        resistorMETOffset = -15;
    }

    /*
     * PNP Emitter (arrow points inward)
     */
    public void drawEmitter(Graphics2D g) {
        g.setColor(base.fgColor);
        g.draw(tail);
        g.setColor(b.getInValue().fgColor);
        g.fill(arrow);
    }
    
    /*
     * Draw emitter #n of PNP Multi-Emitter Transistor
     */
	public void drawMETEmitter(Graphics2D g, int n) {
		int x = 0, y = -10 - n * 20;
		Color bColor = Signal._L.fgColor;
		Color eColor = emitters.pins.get(n).getInValue().fgColor;
		g.setColor(eColor);

		g.drawLine(x + 10, y + 5, x, y); // emitter tail
		g.drawLine(x - 1, y, x - 6, y); // line to pin
		// PNP arrow (points inwards)
		g.setColor(bColor);
		g.translate(0, -n * 20);
		g.fill(arrowPNPMET);
		g.translate(0, n * 20);
		g.setColor(Color.blue);
	}
	
	/**
	 * request to increase the number of pins on the part
	 */
	public void increase() {
		morph().addInput();
	}
	
	/**
     * request to decrease the number of pins on the part
     */
    public void decrease() {
    }
}
