package jsimugate;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

public class PartsBin extends Part {

	private Part prototype;

	public PartsBin(double x, double y, Part part) {
		super(x, y);
		this.prototype = part;
		this.setShape(new Rectangle2D.Double(-25, -25, 50, 50));
		this.addChild(part);
		this.fill = new Color(0x00,0xFF,0xFF,0x10);
		this.color = Color.gray;
	}

	public void drawAtOrigin(Graphics2D g) {
		g.setColor(fill);
		g.fill(shape);
		g.setColor(color);
		g.draw(shape);
		AffineTransform restore = g.getTransform();
		g.scale(.25, .25);
		drawChildren(g);
		g.scale(3, 3);
		g.drawString(children.get(0).label, -30, 30);
		g.setTransform(restore);
	}
	
	public Part produce(double x,double y) {
		return prototype.dup(x, y);
	}
}
