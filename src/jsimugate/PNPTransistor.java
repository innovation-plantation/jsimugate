package jsimugate;

import java.awt.*;
import java.awt.geom.GeneralPath;

/**
 * Implementation of a PNP transistor. The emitter and base are pins, and the collector is an output.
 * If the emitter is higher than the base, then its output is delivered to the collector,
 * otherwise the collector is not driven.
 */
public class PNPTransistor extends Transistor {
    static final GeneralPath arrow = new GeneralPath(), tail = new GeneralPath();

    /**
     * Set up path in advance for these shapes.
     */
    static {
        tail.moveTo(-10, 40);
        tail.lineTo(-7.5, 35);
        // PNP arrow (points inwards from the bottom left)
        arrow.moveTo(-5 - 3, 30 + 6);
        arrow.lineTo(-5 + 1.5, 30 + 10.5);
        arrow.lineTo(-5 + 0, 30 + 0);
        arrow.lineTo(-5 - 9, 30 + 4.5);
        arrow.closePath();

    }

    /**
     * Create the transistor as PNP type
     */
    public PNPTransistor() {
        super();
        name = "PNP";
        tt = Logic.pnp_tt;
        color = Color.red;
        opposite = NPNTransistor.class.getSimpleName();
    }

    public void drawEmitter(Graphics2D g) {
        g.setColor(e.getInValue().fgColor);
        g.draw(tail);
        g.setColor(b.getInValue().fgColor);
        g.fill(arrow);

    }
}
