package jsimugate;

// TODO: deletions, insertion bins, file, menus, transforms, parts, ROM programmer

import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class Wire extends Debuggable {
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
		if (src==null || dst==null) return;
		Point2D p0 = new Point2D.Double(), p1 = new Point2D.Double(); // src
		Point2D p2 = new Point2D.Double(), p3 = new Point2D.Double(); // dst
		src.gTransform.transform(origin, p0);
		src.gTransform.transform(src.control, p1);
		dst.gTransform.transform(dst.control != null ? dst.control : origin, p2);
		dst.gTransform.transform(origin, p3);

		GeneralPath line = new GeneralPath();
		line.moveTo(p0.getX(), p0.getY());
		line.curveTo(p1.getX(), p1.getY(), p2.getX(), p2.getY(), p3.getX(), p3.getY());
		value.trace(g, line);
	}

	public String toString() {
		if (src==null) return "WIRE: "+src + " to "+dst;
		return "WIRE: " + src.sn() + " - " + dst.sn() + "\n";
	}

	public static Wire fromScanner(Scanner scan, Map<Integer, Pin> pinMap) {
		final Pattern wire_pattern = Pattern.compile("WIRE: *Pin#([0-9]+) *- *Pin#([0-9]+)");
		if (scan.findInLine(wire_pattern) == null) return null;
		MatchResult result = scan.match();
		int a = Integer.parseInt(result.group(1));
		int b = Integer.parseInt(result.group(2));
		logline("WIRE pin" + a + " to pin" + b);
		scan.nextLine();
		Pin pinA = pinMap.get(a);
		Pin pinB = pinMap.get(b);
		return new Wire(pinA, pinB);
	}
}
