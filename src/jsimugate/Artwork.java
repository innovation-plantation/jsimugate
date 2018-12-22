package jsimugate;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;

public class Artwork {

	public static Shape andShape(int size) {
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

	public static Shape orShape(int size) {
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

	public static Shape xorShape(int size) {
		int s = 0;
		if (size > 3) s = (size - 3) * 10;
		GeneralPath shape = (GeneralPath) orShape(size);
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

	public static Shape triangleShape() {
		GeneralPath shape = new GeneralPath();
		shape.moveTo(-40, -40);
		shape.lineTo(40, 0);
		shape.lineTo(-40, 40);
		shape.closePath();
		return shape;
	}

	public static Shape majorityShape(int size) {
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

	public static Shape bubbleShape() {
		return new java.awt.geom.Ellipse2D.Double(-10, -10, 20, 20);
	}

	public static Shape diodeShape() {
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

	public static Shape zigzagShape() {
		GeneralPath shape = new GeneralPath();
		shape.moveTo(- 35, 0);
		shape.lineTo(- 30,-5);
		shape.lineTo(- 25, 5);
		shape.lineTo(- 20,-5);
		shape.lineTo(- 15, 5);
		shape.lineTo(- 10,-5);
		shape.lineTo(- 5,  5);
		shape.lineTo(0, 0);
		return shape;
	}
	
	public static Shape sourceShape() {
		return new Line2D.Double(-10, -15, 10, -25);
	}
}

