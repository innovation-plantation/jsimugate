package jsimugate;

import java.awt.*;
import java.awt.geom.GeneralPath;

/**
 * Static functions to return schematic symbol shapes for gates and other components
 */
public class Artwork {

    /**
     * Distinctive shape of the gate
     *
     * @param size number of input pins - stretches the part wider as needed
     * @return the shape
     */
    public static GeneralPath andShape(int size) {
        int s = 0;
        if (size > 4) s = (size - 4) * 10;
        GeneralPath shape = new GeneralPath();
        shape.moveTo(-40, -40 - s);
        shape.lineTo(0, -40 - s);
        shape.curveTo(22, -40 - s, 40, -22 - s, 40, -s);
        shape.lineTo(40, s);
        shape.curveTo(40, 22 + s, 22, 40 + s, 0, 40 + s);
        shape.lineTo(-40, 40 + s);
        shape.closePath();
        return shape;
    }

    /**
     * Distinctive shape of the gate
     *
     * @param size number of input pins - stretches the part wider as needed
     * @return the shape
     */
    public static GeneralPath orShape(int size) {
        int s = 0;
        if (size > 3) s = (size - 3) * 10;
        GeneralPath shape = new GeneralPath();
        shape.moveTo(-40, -20 - s);
        shape.quadTo(-40, -30 - s, -50, -40 - s);
        shape.curveTo(0, -40 - s, 10, -30, 40, 0);
        shape.curveTo(10, 30, 0, 40 + s, -50, 40 + s);
        shape.quadTo(-40, 30 + s, -40, 20 + s);
        shape.closePath();
        return shape;
    }

    /**
     * Distinctive shape of the gate
     *
     * @param size number of input pins - stretches the part wider as needed
     * @return the shape
     */
    public static GeneralPath xorShape(int size) {
        int s = 0;
        if (size > 3) s = (size - 3) * 10;
        GeneralPath shape = orShape(size);
        shape.moveTo(-55, -20);
        shape.quadTo(-55, -30 - s, -63, -40 - s);
        shape.lineTo(-60, -40 - s);
        shape.quadTo(-52, -30 - s, -52, -20);
        shape.lineTo(-52, 20);
        shape.quadTo(-52, 30 + s, -60, 40 + s);
        shape.lineTo(-63, 40 + s);
        shape.quadTo(-55, 30 + s, -55, 20);
        shape.closePath();
        return shape;
    }

    /**
     * Distinctive shape of the NOT gate and buffer
     *
     * @return the shape
     */
    public static GeneralPath triangleShape() {
        GeneralPath shape = new GeneralPath();
        shape.moveTo(-40, -40);
        shape.lineTo(40, 0);
        shape.lineTo(-40, 40);
        shape.closePath();
        return shape;
    }

    /**
     * Distinctive shape of the gate. This degenerates to a Buffer or Not gate shape with one input
     *
     * @param size number of input pins - stretches the part wider as needed
     * @return the shape
     */
    public static GeneralPath majorityShape(int size) {
        if (size < 2) return triangleShape();
        int s = 0;
        if (size > 4) s = (size - 4) * 10;
        GeneralPath shape = new GeneralPath();
        shape.moveTo(-40, -40 - s);
        shape.lineTo(0, -40 - s);
        shape.lineTo(40, 0);
        shape.lineTo(0, 40 + s);
        shape.lineTo(-40, 40 + s);
        shape.closePath();
        return shape;
    }

    /**
     * Shape of a bubble for inversion of pins
     *
     * @return the shape
     */
    public static Shape bubbleShape() {
        return new java.awt.geom.Ellipse2D.Double(-10, -10, 20, 20);
    }

    /**
     * Shape of a diode pointing rightwards
     *
     * @return the shape
     */
    public static GeneralPath diodeShape() {
        GeneralPath shape = new GeneralPath();
        shape.moveTo(4, 0);
        shape.lineTo(4, 6);
        shape.lineTo(7, 6);
        shape.lineTo(7, -6);
        shape.lineTo(4, -6);
        shape.lineTo(4, 0);
        shape.lineTo(-7, 6);
        shape.lineTo(-7, -6);
        shape.closePath();
        return shape;
    }

    /**
     * Shape of a resistor zigzag line vertical
     *
     * @return the shape
     */
    public static GeneralPath zigzagShape() {
        GeneralPath shape = new GeneralPath();
        shape.moveTo(0, -15);
        shape.lineTo(4, -10);
        shape.lineTo(-4, -6);
        shape.lineTo(4, -2);
        shape.lineTo(-4, 2);
        shape.lineTo(4, 6);
        shape.lineTo(-4, 10);
        shape.lineTo(0, 15);
        return shape;
    }

    /**
     * Shape of a pullup resistor with its source
     *
     * @return the shape
     */
    public static Shape pullupShape() {
        GeneralPath shape = zigzagShape();
        shape.moveTo(-7, -12);
        shape.lineTo(7, -20);
        return shape;
    }

    /**
     * Shape of a pulldown resistor with is ground
     *
     * @return the shape
     */
    public static Shape pulldownShape() {
        GeneralPath shape = zigzagShape();
        shape.moveTo(-5, 17);
        shape.lineTo(5, 17);
        shape.moveTo(6, 16);
        shape.lineTo(-6, 16);
        shape.closePath();
        shape.moveTo(-7, 15);
        shape.lineTo(0, 25);
        shape.lineTo(7, 15);
        shape.closePath();
        return shape;
    }

    /**
     * Shape of an input/output connector pin
     *
     * @return the shape
     */
    public static Shape ConnectorShape() {
        return new Polygon(new int[]{-30, 45, 55, 45, -30}, new int[]{-10, -10, 0, 10, 10}, 5);
    }

    /**
     * Shape of a net reference - implied connection between points at different areas of the schematic
     * @return the shape
     */
    public static Shape ReferenceShape () {
        GeneralPath shape = new GeneralPath();
        shape.moveTo(5, 0);
        shape.lineTo(15, 0);
        shape.moveTo(5, -5);
        shape.lineTo(15,0);
        shape.lineTo(5,5);
        return shape;
    }

    /**
     * Shape of a voltage/logic ground schematic symbol
     *
     * @return the shape
     */
    public static Shape vGroundShape() {
        GeneralPath shape = new GeneralPath();
        shape.moveTo(-7, 15);
        shape.lineTo(0, 25);
        shape.lineTo(7, 15);
        shape.closePath();
        return shape;
    }

    /**
     * Shape of a voltage source schematic symbol
     *
     * @return the shape
     */
    public static Shape vSourceShape() {
        GeneralPath shape = new GeneralPath();
        shape.moveTo(-7, -12);
        shape.lineTo(7, -20);
        shape.lineTo(7, -15);
        shape.lineTo(-7, -7);
        shape.closePath();
        return shape;
    }

    /**
     * Shape of adder and ALU parts
     *
     * @return the shape
     */
    public static Shape adderShape() {
        GeneralPath shape = new GeneralPath();
        shape.moveTo(-40, 0);
        shape.lineTo(-50, -10);
        shape.lineTo(-50, -170);
        shape.lineTo(50, -120);
        shape.lineTo(50, 120);
        shape.lineTo(-50, 170);
        shape.lineTo(-50, 10);
        shape.closePath();
        return shape;
    }

    /**
     * Shape of keys on keyboard
     *
     * @return the shape
     */
    public static Shape keyboardShape() {
        GeneralPath shape = new GeneralPath();
        for (int i = -10; i <= 10; i += 4) {
            shape.moveTo(-50, i);
            shape.lineTo(50, i);
        }
        return shape;
    }

    /**
     * Shape of segment a in seven segment display
     */
    public static Shape aSegmentShape() {
        GeneralPath shape = new GeneralPath();
        shape.moveTo(-15, -40);
        shape.lineTo(-10, -45);
        shape.lineTo(10, -45);
        shape.lineTo(15, -40);
        shape.lineTo(10, -35);
        shape.lineTo(-10, -35);
        shape.closePath();
        return shape;
    }

    /**
     * Shape of segment b in seven segment display
     */
    public static Shape bSegmentShape() {
        GeneralPath shape = new GeneralPath();
        shape.moveTo(20, -35);
        shape.lineTo(25, -30);
        shape.lineTo(25, -10);
        shape.lineTo(20, -5);
        shape.lineTo(15, -10);
        shape.lineTo(15, -30);
        shape.closePath();
        return shape;
    }

    /**
     * Shape of segment c in seven segment display
     */
    public static Shape cSegmentShape() {
        GeneralPath shape = new GeneralPath();
        shape.moveTo(20, 35);
        shape.lineTo(25, 30);
        shape.lineTo(25, 10);
        shape.lineTo(20, 5);
        shape.lineTo(15, 10);
        shape.lineTo(15, 30);
        shape.closePath();
        return shape;
    }

    /**
     * Shape of segment d in seven segment display
     */
    public static Shape dSegmentShape() {
        GeneralPath shape = new GeneralPath();
        shape.moveTo(-15, 40);
        shape.lineTo(-10, 45);
        shape.lineTo(10, 45);
        shape.lineTo(15, 40);
        shape.lineTo(10, 35);
        shape.lineTo(-10, 35);
        shape.closePath();
        return shape;
    }

    /**
     * Shape of segment e in seven segment display
     */
    public static Shape eSegmentShape() {
        GeneralPath shape = new GeneralPath();
        shape.moveTo(-20, 35);
        shape.lineTo(-25, 30);
        shape.lineTo(-25, 10);
        shape.lineTo(-20, 5);
        shape.lineTo(-15, 10);
        shape.lineTo(-15, 30);
        shape.closePath();
        return shape;
    }

    /**
     * Shape of segment f in seven segment display
     */
    public static Shape fSegmentShape() {
        GeneralPath shape = new GeneralPath();
        shape.moveTo(-20, -35);
        shape.lineTo(-25, -30);
        shape.lineTo(-25, -10);
        shape.lineTo(-20, -5);
        shape.lineTo(-15, -10);
        shape.lineTo(-15, -30);
        shape.closePath();
        return shape;
    }

    /**
     * Shape of segment a in seven segment display
     */
    public static Shape gSegmentShape() {
        GeneralPath shape = new GeneralPath();
        shape.moveTo(-15, 0);
        shape.lineTo(-10, -5);
        shape.lineTo(10, -5);
        shape.lineTo(15, 0);
        shape.lineTo(10, 5);
        shape.lineTo(-10, 5);
        shape.closePath();
        return shape;
    }
}
