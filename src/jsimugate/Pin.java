package jsimugate;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * A pin is a spot on a part to which wires can be connected.
 * A pins can have a bubble that causes its value to be inverted when the bubble is visible.
 */
public class Pin extends Symbol {

    Shape line, shortLine, longLine;
    Point2D control = new Point2D.Double();
    boolean inverted;
    Inversion bubble;
    private Signal inValue = Signal._Z;
    private Signal outValue = Signal._Z;
    boolean recovery = false; // keep diode pins from self-sampling and oscillating.

    /**
     * Create a new pin at the x y location relative to the center of the part.
     *
     * @param x horizontal position (based on the part being in its default orientation and position)
     * @param y vertical position (based on the part being in its default orientation and position)
     */
    public Pin(double x, double y) {
        super(x, y);
        if (parent == null) gTransform = transform;
        hitbox = Artwork.bubbleShape();
    }

    /**
     * An invertible pin positioned on the right side of the part with a leg going
     * dx distance to get to the part's boundary.
     *
     * @param dx The length of the leg pointing toward the part
     * @return the pin
     */
    Pin right(double dx) {
        return right(dx, true);
    }

    /**
     * An invertible pin positioned on the left side of the part with a leg going
     * dx distance to get to the part's boundary.
     *
     * @param dx The length of the leg pointing toward the part
     * @return the pin
     */
    Pin left(double dx) {
        return left(dx, true);
    }

    /**
     * An invertible pin positioned on the bottom side of the part with a leg going
     * dx distance to get to the part's boundary.
     *
     * @param dx The length of the leg pointing toward the part
     * @return the pin
     */
    Pin down(double dx) {
        return down(dx, true);
    }

    /**
     * An invertible pin positioned on the top side of the part with a leg going
     * dx distance to get to the part's boundary.
     *
     * @param dx The length of the leg pointing toward the part
     * @return the pin
     */
    Pin up(double dx) {
        return up(dx, true);
    }

    /**
     * A pin positioned on the right side of the part with a leg going
     * dx distance to get to the part's boundary.
     *
     * @param dx         The length of the leg pointing toward the part
     * @param invertible if a bubble is allowed on this pin
     * @return the pin
     */
    Pin right(double dx, boolean invertible) {
        control = new Point2D.Double(dx, 0);
        longLine = new Line2D.Double(-dx, 0, 0, 0);
        shortLine = new Line2D.Double(23 - dx, 0, 0, 0);
        line = longLine;
        if (invertible) addChild(bubble = new Inversion(10 - dx, 0));
        return this;
    }

    /**
     * A pin positioned on the left side of the part with a leg going
     * dx distance to get to the part's boundary.
     *
     * @param dx         The length of the leg pointing toward the part
     * @param invertible if a bubble is allowed on this pin
     * @return the pin
     */
    Pin left(double dx, boolean invertible) {
        control = new Point2D.Double(-dx, 0);
        longLine = new Line2D.Double(0, 0, dx, 0);
        shortLine = new Line2D.Double(0, 0, dx - 23, 0);
        line = longLine;
        if (invertible) addChild(bubble = new Inversion(dx - 10, 0));
        return this;
    }

    /**
     * A pin positioned on the bottom side of the part with a leg going
     * dx distance to get to the part's boundary.
     *
     * @param dy         The length of the leg pointing toward the part
     * @param invertible if a bubble is allowed on this pin
     * @return the pin
     */
    Pin down(double dy, boolean invertible) {
        control = new Point2D.Double(0, dy);
        longLine = new Line2D.Double(0, 0, 0, -dy);
        shortLine = new Line2D.Double(0, 0, 0, 23 - dy);
        line = longLine;
        if (invertible) addChild(bubble = new Inversion(0, 10 - dy));
        return this;
    }

    /**
     * A pin positioned on the top side of the part with a leg going
     * dx distance to get to the part's boundary.
     *
     * @param dy         The length of the leg pointing toward the part
     * @param invertible if a bubble is allowed on this pin
     * @return the pin
     */
    Pin up(double dy, boolean invertible) {
        control = new Point2D.Double(0, -dy);
        longLine = new Line2D.Double(0, 0, 0, dy);
        shortLine = new Line2D.Double(0, 23 - dy, 0, 0);
        line = longLine;
        if (invertible) addChild(bubble = new Inversion(0, dy - 10));
        return this;
    }

    /**
     * Draw the pin onto the graphics context
     *
     * @param g graphics context
     */
    public void drawAtOrigin(Graphics2D g) {
        if (line != null) {
            if (outValue != Signal._Z) outValue.trace(g, line);
            else inValue.trace(g, line);

        }
        this.fill = inValue.fgColor;
        this.color = outValue.fgColor;
        super.drawAtOrigin(g);
    }

    /**
     * Switch between inverted and non-inverted pin, toggling the visibility of the bubble.
     */
    void toggleInversion() {
        if (bubble != null) {
            line = (inverted = !inverted) ? shortLine : longLine;
            bubble.setVisible(inverted);
            parent.updateLabel();
        }
    }

    /**
     * Move the pin
     *
     * @param x horizontal distance
     * @param y vertical distance
     */
    public void translate(double x, double y) {
        transform.translate(x, y);
    }

    /**
     * Get the value that the part is outputting to the pin
     *
     * @return the value on the pin from the part
     */
    Signal getOutValue() {
        return outValue;
    }

    /**
     * The part calls this to set its output value to the pin
     *
     * @param newValue
     */
    void setOutValue(Signal newValue) {
        outValue = inverted ? newValue.not() : newValue;
        Part.Tech tech = ((Part) parent).tech;
        if (outValue == tech.changeFrom) outValue = tech.changeTo;
    }

    /**
     * The value from the wire going to the part.
     *
     * @return the input value to the part
     */
    Signal getInValue() {
        return inverted ? inValue.not() : inValue;
    }

    /**
     * Set the value from the wire going to the part.
     */
    void setInValue(Signal newValue) {
        inValue = newValue;
    }

    /**
     * Read the binary value from the pins.
     * Invalid bits are interpreted as zero.
     * @param pins pins with a binary value
     * @return the integer number converted from binary.
     */
    public static int pack(Pin[] pins) {
        int result = 0;
        for (int n = 0; n < pins.length; n++) result += pins[n].getInValue().asBit() << n;
        return result;
    }

    /**
     * Drive the pins with the binary value.
     * @param pins pins to receive the binary value
     * @param src the number to be converted to binary
     */
    public static void unpack(Pin[] pins, int src) {
        for (int n = 0; n < pins.length; n++) pins[n].setOutValue(Signal.fromBit((src >> n) & 1));
    }
}
