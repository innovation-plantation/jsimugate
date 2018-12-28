package jsimugate;

import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;

public class Artwork {

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

	public static GeneralPath xorShape(int size) {
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

	public static GeneralPath triangleShape() {
		GeneralPath shape = new GeneralPath();
		shape.moveTo(-40, -40);
		shape.lineTo(40, 0);
		shape.lineTo(-40, 40);
		shape.closePath();
		return shape;
	}

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

	public static Shape bubbleShape() {
		return new java.awt.geom.Ellipse2D.Double(-10, -10, 20, 20);
	}

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

	public static GeneralPath zigzagShape() {
		GeneralPath shape = new GeneralPath();
		shape.moveTo(0, -15);
		shape.lineTo(4, -10);
		shape.lineTo(-4, -6);
		shape.lineTo(4, -2);
		shape.lineTo(-4, 2);
		shape.lineTo(4, 6);
		shape.lineTo(-4, 10);
		shape.lineTo(0, 15);;
		return shape;
	}

	public static Shape sourceShape() {
		return new Line2D.Double(-10, -15, 10, -25);
	}

	public static Shape pullupShape() {
		GeneralPath shape = zigzagShape();
		shape.moveTo(-7, -12);
		shape.lineTo(7, -20);
		return shape;
	}

	public static Shape pulldownShape() {
		GeneralPath shape = zigzagShape();
		shape.moveTo(-7, 15);
		shape.lineTo(0, 25);
		shape.lineTo(7, 15);
		shape.closePath();
		return shape;
	}
	
	public static Shape InPinShape() {
		return new Polygon(
				new int[] {-40,40,50,40,-40},
				new int[] {-10,-10,0,10,10},5);
	}
	
	public static Shape OutPinShape() {
		return new Polygon(
				new int[] {40,-40,-50,-40,40},
				new int[] {-0,10,0,-10,-10},5);
	}
}
