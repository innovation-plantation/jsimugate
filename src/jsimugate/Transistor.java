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
     * Outputting X when the base is unknown is a bit naive. We can do better than that, so...
     * In the event that the base input is unknown, but there is a good signal on the collector
     * that is the same polarity as the signal on the emitter, output Z.
     */
    public void operate() {
        Signal value = tt[e.getInValue().ordinal()][b.getInValue().ordinal()];
        this.c.setOutValue(value);
        if (b.getInValue().bad) {
            c.recovery = true;
            if (c.getInValue().hi && e.getInValue().hi) c.setOutValue(Signal._Z);
            if (c.getInValue().lo && e.getInValue().lo) c.setOutValue(Signal._Z);
        } else {
            c.recovery = false;
        }
    }

    public Part reversePolarity() {
        Transistor newPart = (Transistor) super.reversePolarity();
        newPart.b = b;
        newPart.c = c;
        newPart.e = e;
        return newPart;
    }
}
