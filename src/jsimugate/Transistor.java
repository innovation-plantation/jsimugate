package jsimugate;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;

/**
 * Bipolar Junction Transistors for digital switching circuits: NPN: Emitter
 * arrow away from base. If emitter is much lower than base then it connects to
 * collector. PNP: Emitter arrow toward base. If emitter is much higher than
 * base then it connects to collector.
 *
 * @author Ted
 */
public class Transistor extends Discrete {

    static final Ellipse2D circle = new Ellipse2D.Double(-15, 15, 30, 30);
    static final GeneralPath resistor = Artwork.zigzagShape();

    Pin b, c, e;
    protected Signal[][] tt;


    public void drawEmitter(Graphics2D g) {
        g.setColor(b.getInValue().fgColor);
        g.drawLine(-5, 30, -10, 40);
    }

    /**
     * Create a transistor at the origin
     */
    public Transistor() {
        super();
        this.setShape(circle, 40, 0, 0, 0);

        this.c = this.addPin(new Pin(20, 40)); // output
        this.b = this.addPin(new Pin(0, -20));
        this.e = this.addPin(new Pin(-20, 40));

        this.color = Color.black;
        this.fill = Color.white;
    }

    /**
     * Draw the transistor at the origin
     *
     * @param g the graphics context onto which the transistor is drawn
     */
    public void drawAtOrigin(Graphics2D g) {
        super.drawAtOrigin(g);
        g.setStroke(defaultStroke);

        // Emitter (left)
        g.setColor(e.getInValue().fgColor);
        g.drawLine(-10, 40, -20, 40);
        drawEmitter(g);

        // Collector (right)
        g.setColor(c.getOutValue().fgColor);
        g.drawLine(5, 30, 10, 40);
        //g.setColor(c.getInValue().fgColor);
        g.drawLine(10, 40, 20, 40);

        // base (top)
        g.setColor(b.getInValue().fgColor);
        g.drawLine(0, 15, 0, 25);
        g.fillRect(-10, 25, 20, 5);
        g.draw(resistor);

    }

    /**
     * Examine the emitter and base input pins and set the collector output pins accordingly.
     */
    public void operate() {
        this.c.setOutValue(tt[e.getInValue().ordinal()][b.getInValue().ordinal()]);
    }

    public Part reversePolarity() {
        Transistor newPart = (Transistor) super.reversePolarity();
        newPart.b = b;
        newPart.c = c;
        newPart.e = e;
        return newPart;
    }
}
