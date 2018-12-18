package jsimugate;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class Pin extends Symbol {

	Shape line,shortLine,longLine;
	Point2D control;
	boolean inverted;
	Inversion bubble;
	private Signal inValue = Signal._Z;
	private Signal outValue = Signal._Z;

	public Pin(double x, double y) {
		super(x, y);
		if (parent==null) gTransform = transform;
		hitbox = Artwork.bubbleShape();
	}

	Pin right(double dx) {
		control = new Point2D.Double(dx, 0);
		longLine = new Line2D.Double(-dx, 0, 0, 0);
		shortLine = new Line2D.Double(23 - dx, 0, 0, 0);
		line=longLine;
		addChild(bubble = new Inversion(10 - dx, 0));
		return this;
	}

	Pin left(double dx) {
		control = new Point2D.Double(-dx, 0);
		longLine = new Line2D.Double(0, 0, dx, 0);
		shortLine = new Line2D.Double(0, 0, dx - 23, 0);
		line=longLine;
		addChild(bubble = new Inversion(dx - 10, 0));
		return this;
	}

	Pin down(double dy) {
		control = new Point2D.Double(0, dy);
		longLine = new Line2D.Double(0, 0, 0, -dy);
		shortLine = new Line2D.Double(0, 0, 0, 23 - dy);
		line=longLine;
		addChild(bubble = new Inversion(0, 10 - dy));
		return this;
	}

	public void drawAtOrigin(Graphics2D g) {
		if (line != null) {
			if (outValue!=Signal._Z) outValue.trace(g, line);
			else inValue.trace(g, line);
			
		}
		super.drawAtOrigin(g);
	}

	void toggleInversion() {
		if (bubble != null) {
			line = (inverted = !inverted) ? shortLine : longLine;
			bubble.setVisible(inverted);
			parent.updateLabel();
		}
	}

	public void translate(double x, double y) {
		transform.translate(x, y);
	}

	Signal getOutValue() {
		return outValue;
	}

	void setOutValue(Signal newValue) {
		outValue = inverted?newValue.not():newValue;
	    Part.Tech tech = ((Part)parent).tech;
	    if (outValue==tech.changeFrom) outValue=tech.changeTo;
		this.setInValue(outValue);
	}

	Signal getInValue() {
		return inverted?inValue.not():inValue;
	}

	void setInValue(Signal newValue) {
		inValue = newValue;
	}
}
