package jsimugate;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class Symbol {
	static final Shape defaultPath = new Rectangle2D.Double(-3, -3, 7, 7);
	static final Stroke defaultStroke = new BasicStroke(3);
	static final Stroke defaultHighlightStroke = new BasicStroke(10);
	static final Color defaultHighlightColor = new Color(255,255,0,80);
	Symbol parent;
	ArrayList<Symbol> children = new ArrayList<Symbol>();
	Shape hitbox = defaultPath;
	private Shape shape = defaultPath;
	AffineTransform transform = new AffineTransform();
	public Color color = Color.black, fill = Color.lightGray;
	Stroke stroke = defaultStroke;
	boolean selected=false;
	boolean selecting=false;
	Stroke hilightStroke = defaultHighlightStroke;
	Color highlightColor = defaultHighlightColor;

	AffineTransform gTransform=new AffineTransform();

	public Symbol addChild(Symbol child) {
		child.parent = this;
		children.add(child);
		return this;
	}

	public Symbol removeChild(Symbol child) {
		child.parent = null;
		children.remove(child);
		return this;
	}

	public Symbol setShape(Shape shape) {
		this.shape = shape;
		this.hitbox = shape;
		return this;
	}

	public Symbol setColor(Color color) {
		this.color = color;
		return this;
	}

	public Symbol setFill(Color color) {
		this.fill = color;
		return this;
	}

	public Symbol(double x, double y) {
		transform.translate(x, y);
	}

	public Symbol(Point2D xy) {
		transform.translate(xy.getX(), xy.getY());
	}

	public void drawAtOrigin(Graphics2D g) {
		if (selected || selecting) {
			g.setColor(highlightColor);
			g.setStroke(hilightStroke);
			g.draw(shape);
		}
		if (fill != null) {
			g.setColor(fill);
			g.fill(shape);
		}
		if (color != null) {
			g.setStroke(stroke);
			g.setColor(color);
			g.draw(shape);
		}
	}
	
	public boolean at(Point2D xy) {
		return hitbox !=null && gTransform.createTransformedShape(hitbox).contains(xy);
		// equivalent to checking if the hitbox contains the inverse gTransform of the point	
	}
	
	public boolean at(Rectangle2D lasso) {
		return hitbox !=null && gTransform.createTransformedShape(hitbox).intersects(lasso);
	}

	public void draw(Graphics2D g) {
		AffineTransform restore = g.getTransform();
		g.transform(transform);
		gTransform = g.getTransform();
		for (Symbol child : children) child.draw(g);
		drawAtOrigin(g);
		g.setTransform(restore);
	}
}