package jsimugate;

import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

public class Wire {
	Signal value = Signal._Z;
	static final Point2D.Double origin = new Point2D.Double();
	Pin src, dst;
	
	public Wire(Pin srcPin) {
		src = srcPin;
	}
	void to(Pin dstPin) {
		dst = dstPin;
		Net.connect(this);
	}
	
	public Wire(Pin srcPin, Pin dstPin) {
		src = srcPin;
		dst = dstPin;
		Net.connect(this);
	}

	public void draw(Graphics2D g) {
		Point2D p0 = new Point2D.Double(), p1 = new Point2D.Double(); // src
		Point2D p2 = new Point2D.Double(), p3 = new Point2D.Double(); // dst
		src.gTransform.transform(origin, p0);
		src.gTransform.transform(src.control, p1);
		dst.gTransform.transform(dst.control!=null?dst.control:origin, p2);
		dst.gTransform.transform(origin, p3);
		
		GeneralPath line=new GeneralPath();
		line.moveTo(p0.getX(), p0.getY());
		line.curveTo(p1.getX(), p1.getY(), p2.getX(), p2.getY(), p3.getX(), p3.getY());
		value.trace(g, line);
	}
}
