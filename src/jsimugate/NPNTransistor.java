package jsimugate;

import java.awt.*;
import java.awt.geom.GeneralPath;

/**
 * Implementation of an NPN transistor. The emitter and base are pins, and the collector is an output.
 * If the emitter is lower than the base, then its output is delivered to the collector,
 * otherwise the collector is not driven.
 */
public class NPNTransistor extends Transistor {
    static final GeneralPath arrow = new GeneralPath(), tail = new GeneralPath();

    /**
     * Set up path in advance for these shapes.
     */
    static {
        tail.moveTo(-7.5, 35);
        tail.lineTo(-5, 30);
        // NPN arrow (points outwards toward the bottom left)
        arrow.moveTo(-10 + 3, 40 - 6);
        arrow.lineTo(-10 - 1.5, 40 - 10.5);
        arrow.lineTo(-10 + 0, 40 - 0);
        arrow.lineTo(-10 + 9, 40 - 4.5);
        arrow.closePath();
    }

    /**
     * Create the transistor as NPN type
     */
    public NPNTransistor() {
        super();
        name = "NPN";
        tt = Logic.npn_tt;
        opposite = PNPTransistor.class.getSimpleName();
    }

    public void drawEmitter(Graphics2D g) {
        g.setColor(b.getInValue().fgColor);
        g.draw(tail);
        g.setColor(e.getInValue().fgColor);
        g.fill(arrow);

    }
}
