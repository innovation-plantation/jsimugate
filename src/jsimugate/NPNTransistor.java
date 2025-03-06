package jsimugate;

import java.awt.*;
import java.awt.geom.GeneralPath;

/**
 * Implementation of an NPN transistor. The emitter and base are pins, and the collector is an output.
 * If the emitter is lower than the base, then its output is delivered to the collector,
 * otherwise the collector is not driven.
 */
public class NPNTransistor extends Transistor {
    static final GeneralPath arrow = new GeneralPath(), tail = new GeneralPath(), arrowMET = new GeneralPath();
    /**
     * Set up path in advance for these shapes.
     */
    {
    	color = Signal._L.fgColor;
    	morphName = NPNMultiTransistor.class.getSimpleName();
    	opposite = PNPTransistor.class.getSimpleName();
    }
    static {
        tail.moveTo(-7.5, 35);
        tail.lineTo(-5, 30);
        // NPN arrow (points outwards toward the bottom left)
        arrow.moveTo(-10 + 3, 40 - 6);
        arrow.lineTo(-10 - 1.5, 40 - 10.5);
        arrow.lineTo(-10 + 0, 40 - 0);
        arrow.lineTo(-10 + 9, 40 - 4.5);
        arrow.closePath();
        // NPN MET arrow 
		arrowMET.moveTo(  6   -3,12 -20  );
        arrowMET.lineTo(  10.5-3,9 -20);
        arrowMET.lineTo(  0   -3,8  -20 );
        arrowMET.lineTo(  4.5 -3,18  -20);
        arrowMET.closePath();
    }

    /**
     * Create the transistor as NPN type
     */
    public NPNTransistor() {
        super();
        name = "NPN";
        tt = Logic.npn_tt;
        tt_met = Logic.met_npn_tt;
        opposite = PNPTransistor.class.getSimpleName();
        resistorMET = Artwork.pullupShape();
        baseColorMET = Signal._H.fgColor;
        resistorMETOffset = 15;
    }

    /*
     * NPN Emitter (arrow points outward)
     */
    public void drawEmitter(Graphics2D g) {
        g.setColor(base.fgColor);
        g.draw(tail);
        g.setColor(e.getInValue().fgColor);
        g.fill(arrow);

    }
      
    /*
     * Draw emitter #n of NPN Multi-Emitter Transistor
     */
    public void drawMETEmitter(Graphics2D g, int n) {
    	int x=0,y=-10-n*20;
    	Color eColor = emitters.pins.get(n).getInValue().fgColor; 
    	Color bColor = Signal._H.fgColor;
    	g.setColor(bColor);
        g.drawLine(x+10, y+5, x, y); //  emitter tail
        g.setColor(eColor);
        g.drawLine(x-1,y,x-6,y); // line to pin
        // NPN arrow (points outwards)
        g.translate(0,-n*20);
        g.fill(arrowMET);
        g.translate(0,n*20);
        g.setColor(bColor);
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
