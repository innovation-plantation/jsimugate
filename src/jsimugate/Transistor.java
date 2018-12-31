package jsimugate;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;

/**
 * Bipolar Junction Transistors for digital switching circuits: NPN: Emitter
 * arrow away from base. If emitter is much lower than base then it connects to
 * collector. PNP: Emitter arrow toward base. If emitter is much higher than
 * base then it connects to collector.
 * 
 * @author Ted
 *
 */
public class Transistor extends Discrete {

	static final Ellipse2D circle = new Ellipse2D.Double(-15, 15, 30, 30);
	static final GeneralPath resistor = Artwork.zigzagShape();
	static final GeneralPath npn_arrow = new GeneralPath(), pnp_arrow = new GeneralPath();
	private Pin b, c, e;
	protected GeneralPath arrow;
	protected Signal[][] tt;

	static {
		// NPN arrow (points outwards toward the bottom left)
		npn_arrow.moveTo(-10 + 3, 40 - 6);
		npn_arrow.lineTo(-10 - 1.5, 40 - 10.5);
		npn_arrow.lineTo(-10 + 0, 40 - 0);
		npn_arrow.lineTo(-10 + 9, 40 - 4.5);
		npn_arrow.closePath();

		// PNP arrow (points inwards from the bottom left)
		pnp_arrow.moveTo(-5 - 3, 30 + 6);
		pnp_arrow.lineTo(-5 + 1.5, 30 + 10.5);
		pnp_arrow.lineTo(-5 + 0, 30 + 0);
		pnp_arrow.lineTo(-5 - 9, 30 + 4.5);
		pnp_arrow.closePath();
	}

	public Transistor(double x, double y) {
		super(x, y);
		this.setShape(circle,40,0,0,0);

		this.c = this.addPin(new Pin(20, 40)); // output
		this.b = this.addPin(new Pin(0, -20));
		this.e = this.addPin(new Pin(-20, 40));

		this.color = Color.black;
		this.fill = Color.white;
	}

	public void drawAtOrigin(Graphics2D g) {
		super.drawAtOrigin(g);
		g.setStroke(defaultStroke);
		// base (top)
		g.setColor(b.getInValue().fgColor);
		g.drawLine(0, 15, 0, 25);
		g.fillRect(-10, 25, 20, 5);
		g.draw(resistor);

		// Emitter (left)
		g.setColor(e.getInValue().fgColor);
		g.drawLine(-5, 30, -10, 40);
		g.drawLine(-10, 40, -20, 40);
		g.fill(arrow);

		// Collector (right)
		g.setColor(c.getOutValue().fgColor);
		g.drawLine(5, 30, 10, 40);
		g.setColor(c.getInValue().fgColor);
		g.drawLine(10, 40, 20, 40);
	}

	public void operate() {
		this.c.setOutValue(tt[e.getInValue().ordinal()][b.getInValue().ordinal()]);
	}

}
