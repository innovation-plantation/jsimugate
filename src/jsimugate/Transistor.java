package jsimugate;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.RoundRectangle2D.Double;
import java.util.Scanner;

/**
 * Bipolar Junction Transistors for digital switching circuits: NPN: Emitter
 * arrow away from base. If emitter is much lower than base then it connects to
 * collector. PNP: Emitter arrow toward base. If emitter is much higher than
 * base then it connects to collector.
 *
 * @author Ted
 */

class TransistorShape {
    RoundRectangle2D shell = new RoundRectangle2D.Double(-15,15, 30,30, 30,30);
	protected int x=0,y=0,size=0;

	public TransistorShape(int x, int y) {
		this.x=x;this.y=y;
	}
	public TransistorShape() {}
	public void setSize(int n) {
		getOblong().setFrame(-5, 5-n*20, 30, 10+n*20);	
	}
	public RoundRectangle2D getOblong() {
		return shell;
	}

}
class TTLShape {
	TransistorShape set=new TransistorShape(); // single emitter transistor
	TransistorShape met=new TransistorShape(-10,30); // multi emitter transistor
	
	public TTLShape() {
		setSize(0);
	}

	Shape setSize(int n) {
		met.setSize(n);
		Area composite = new Area(set.shell);
		if (n>0) composite.add(new Area(met.shell));
		return composite;
	}
	int getSize() {
		return met.size;
	}
}
public abstract class Transistor extends Discrete {
    static final TTLShape body = new TTLShape();
	static final Shape resistor = Artwork.zigzagShape();
	static String morphName;

	protected Shape resistorMET= resistor; 
	protected int resistorMETOffset;
	protected Color baseColorMET = Signal._U.fgColor;
	
    Pin b, c, e;
    PinGroup emitters = new PinGroup();
    protected Signal[][] tt;
    protected Signal[][] tt_met;
    protected Signal base=Signal._Z;
    
    /**
     * Create a transistor at the origin
     */
    public Transistor() {
        super();
        this.setShape(body.setSize(0), 40, 0, 0, 0);

        this.c = this.addPin(new Pin(20, 40)); // output
        this.b = this.addPin(new Pin(0, -20));
        this.e = this.addPin(new Pin(-20, 40));

        this.color = Color.gray;
        this.fill = Color.white;
    }
    public void reshape() {
        this.setShape(body.setSize(emitters.size())); 
    }

    /**
     * Draw the transistor at the origin
     *
     * @param g the graphics context onto which the transistor is drawn
     */
    public void drawAtOrigin(Graphics2D g) {
        int n=emitters.size();
        super.drawAtOrigin(g);
        // FOR DEBUGGING: g.drawString(this.sn(),20,25);
        g.setStroke(defaultStroke);

        // Emitter (left)
        g.setColor(e.getInValue().fgColor);
        drawEmitter(g);

        // Collector (right)
        g.setColor(c.getOutValue().fgColor);
        g.drawLine(5, 30, 10, 40);
        //g.setColor(c.getInValue().fgColor);
        g.drawLine(10, 40, 20, 40);

        // base (top)
        g.setColor(base.fgColor);
        g.drawLine(0, 15, 0, 25);
        g.fillRect(-10, 25, 20, 5);
        if (n==0) g.draw(resistor);
        else {
        	// MET - Big base always pulled up
        	int mid=10-10*n;
            g.setColor(baseColorMET); 
        	g.fillRect(10,10-20*n,5,20*n);  // base
        	g.drawLine(15,mid,35,mid); // base to pullup
        	g.translate(35, mid-resistorMETOffset);
        	g.draw(resistorMET); 
        	g.translate(-35, -mid+resistorMETOffset);

        	for (int i=0;i<n;i++) drawMETEmitter(g,i);

            g.setColor(base.fgColor); // MET collector
        	int x=0,y=10;
        	g.drawLine(x+10, y-5, x, y);   	
        }

    }

	/**
     * Add a pin to the transistor
     */
    Pin addInput() {
    	Pin pin;
        if (emitters.pins.isEmpty()) {
        	// CONVERT TO MULTI TRANSISTOR
        	pin = removePin(b).translate(-20, 10);

        } else pin = new Pin(-20, -10-20*emitters.size());
        pin.left(20,false);
        emitters.addPin(pin);
        addPin(pin);

        reshape();
        updateLabel();
        return pin;
    }

    /**
     * Remove the most recently added pin if it's not connected to anything
     */
    protected void removeInput() {
    	System.out.println(emitters.size());
    	switch (emitters.size()) {
    	case 0: return;
    	case 1:  
    		b = emitters.pins.get(0);
    		b.translate(20, -10).nondirectional();
    		emitters.pins.set(0, new Pin(0,0));
    		emitters.removePin();
    		break;
    	default: 
            removePin(emitters.removePin());
        }
        reshape();
        updateLabel();
    }

	/**
	 * request to increase the number of pins on the part
	 */
	abstract public void increase() ;


	/**
     * request to decrease the number of pins on the part
     */
    abstract public void decrease();

    /**
     * Examine the emitter and base input pins and set the collector output pins accordingly.
     * Outputting X when the base is unknown is a bit naive. We can do better than that, so...
     * In the event that the base input is unknown, but there is a good signal on the collector
     * that is the same polarity as the signal on the emitter, output Z.
     */
    public void operate() {
        Signal value;
        base = getBaseValue();

        value= tt[e.getInValue().ordinal()][base.ordinal()];
        this.c.setOutValue(value);
        if (b.getInValue().bad) {
            c.recovery = true;
            if (c.getInValue().hi && e.getInValue().hi) c.setOutValue(Signal._Z);
            if (c.getInValue().lo && e.getInValue().lo) c.setOutValue(Signal._Z);
        } else {
            c.recovery = false;
        }
    }

    private Signal getBaseValue() {
    	if (emitters.size()>0) return getMETSignal();
    	if (b==null) return Signal._Z;
		return  b.getInValue(); // TTL must override.
	}

	public Part reversePolarity() {
        Transistor newPart = (Transistor) super.reversePolarity();
        newPart.b = b;
        newPart.c = c;
        newPart.e = e;
        newPart.emitters = emitters;
        newPart.reshape();
        return newPart;
    }
    public Part morph(String newTech) {
        if (newTech == null) return this;
        String s = toString().replaceAll(this.getClass().getSimpleName(), newTech);
        Discrete that = (Discrete) Part.fromScanner(new Scanner(s), null);
        that.pins = pins;
        that.children = children;
        for (Symbol child : children) child.parent = that;
        return that;
    }
    public Transistor morph() {
        Transistor newPart = (Transistor) morph(morphName);
        newPart.b = b;
        newPart.c = c;
        newPart.e = e;
        newPart.emitters = emitters;
        newPart.reshape();
        newPart.setSelected(isSelected());
        Circuit.circuit.parts.set(Circuit.circuit.parts.indexOf(this), newPart);
        return newPart;
    }
	public void drawEmitter(Graphics2D g) {
	}
	public void drawMETEmitter(Graphics2D g, int n) {
	}
	public void drawMETCollector(Graphics2D g) {
	}
	public Signal getMETSignal() {
		if (emitters.size()<1) return b.getInValue();
		Signal compositeValue = Signal._U;
		for (Pin pin: emitters.pins) {
			Signal pinValue = pin.getInValue();
			compositeValue = tt_met[pinValue.ordinal()][compositeValue.ordinal()];
		}
		return compositeValue;	
	}

}


