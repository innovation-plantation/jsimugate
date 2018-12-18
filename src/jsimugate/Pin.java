package jsimugate;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class Pin extends Symbol {

	Shape line;
	Point2D control;
	boolean invertible, inverted;
	Inversion bubble;
	Signal in_value = Signal._Z;
	private Signal out_value=Signal._Z;

	public Pin(double x, double y) {
		super(x, y);
	}

	Pin right(double dx) {
		control = new Point2D.Double(dx,0);
		line = new Line2D.Double( - dx,0,0, 0);
		addChild(bubble = new Inversion( 10 - dx, 0));
		return this;
	}

	Pin left(double dx) {
		control = new Point2D.Double(- dx,0);
		line = new Line2D.Double(0, 0,  dx, 0);
		addChild(bubble = new Inversion( dx - 10, 0));
		return this;
	}

	Pin down(double dy) {
		control = new Point2D.Double(0, dy);
		line = new Line2D.Double(0, 0, 0, - dy);
		addChild(bubble = new Inversion(0,  10 - dy));
		return this;
	}

	public void drawAtOrigin(Graphics2D g) {
		if (line != null) in_value.trace(g, line);
		super.drawAtOrigin(g);
	}

	void toggleInversion() {
		if (invertible) {
			inverted = !inverted;
			bubble.setVisible(inverted);
		}
	}

	public void translate(double x, double y) {
		transform.translate(x, y);
	}

	Signal getOutValue() {
		return out_value;
	}

	void setOutValue(Signal out_value) {
		this.in_value = this.out_value = out_value;
	}
}
