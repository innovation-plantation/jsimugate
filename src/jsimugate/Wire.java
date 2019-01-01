package jsimugate;

// TODO: deletions, insertion bins, file, menus, transforms, parts, ROM programmer

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

/**
 * A Wire connects two pins together
 */
public class Wire {
    Signal value = Signal._Z;
    static final Point2D.Double origin = new Point2D.Double();
    Pin src, dst;

    /**
     * Create a one-ended wire while the pin for the second end has not yet been selected.
     *
     * @param srcPin The first pin from which the wire is drawn.
     */
    public Wire(Pin srcPin) {
        src = srcPin;
    }

    /**
     * Having prevously selected a source pin, connect the other end here and complete the
     * wire connection.
     *
     * @param dstPin
     * @return
     */
    Wire to(Pin dstPin) {
        dst = dstPin;
        Net.connect(this);
        return this;
    }

    /**
     * Create a complete wire connection from one pin to another
     *
     * @param srcPin
     * @param dstPin
     */
    public Wire(Pin srcPin, Pin dstPin) {
        src = srcPin;
        dst = dstPin;
        Net.connect(this);
    }

    /**
     * Draw the wire
     *
     * @param g the graphics context onto which the wire is drawn
     */
    public void draw(Graphics2D g) {
        if (src == null || dst == null) return;
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

    /**
     * The serialized text necessary to save the wire in order to scan it back in again later.
     *
     * @return
     */
    public String toString() {
        if (src == null) return "WIRE: " + src + " to " + dst;
        return "WIRE: " + src.sn() + " - " + dst.sn() + "\n";
    }

    /**
     * Load a previously serialized wire back from text scanned by this scanner.
     * Use the pin map to coordinate connections between loaded wires when calling this function multiple times.
     *
     * @param scan   the scanner
     * @param pinMap the pin map
     * @return the wire that was read in from the scanner or null if none.
     */
    public static Wire fromScanner(Scanner scan, Map<Integer, Pin> pinMap) {
        final Pattern wire_pattern = Pattern.compile("WIRE: *Pin#([0-9]+) *- *Pin#([0-9]+)");
        if (scan.findInLine(wire_pattern) == null) return null;
        MatchResult result = scan.match();
        int a = Integer.parseInt(result.group(1));
        int b = Integer.parseInt(result.group(2));
        Log.println("WIRE pin" + a + " to pin" + b);
        scan.nextLine();
        Pin pinA = pinMap.get(a);
        Pin pinB = pinMap.get(b);
        return new Wire(pinA, pinB);
    }
}
