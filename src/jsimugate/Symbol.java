package jsimugate;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class Symbol extends Numbered {
	static final Shape defaultPath = new Rectangle2D.Double(-4, -4, 8, 8);
	static final Stroke defaultStroke = new BasicStroke(3);
	static final Stroke defaultHighlightStroke = new BasicStroke(10);
	static final Color defaultHighlightColor = new Color(255, 255, 0, 80);
	static final Color defaultFillColor = new Color(200, 200, 200, 245);
	Symbol parent;
	ArrayList<Symbol> children = new ArrayList<Symbol>();
	Shape hitbox = defaultPath;
	protected Shape shape = defaultPath;
	AffineTransform transform = new AffineTransform();
	public Color color = Color.black, fill = defaultFillColor;
	Stroke stroke = defaultStroke;
	private boolean selected = false;
	boolean selecting = false;
	Stroke hilightStroke = defaultHighlightStroke;
	Color highlightColor = defaultHighlightColor;
	String label, sublabel;

	AffineTransform gTransform = new AffineTransform();

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

	/**
	 * Sets the hitbox to the padded bounding box
	 * 
	 * @param shape - new shape
	 * @param pad   - hitbox bigger than bounding box in all directions by this
	 *              amount
	 * @return self for chaining
	 */
	public Symbol setShape(Shape shape, int pad) {
		this.shape = shape;
		Rectangle2D bounds = shape.getBounds2D();
		bounds.setRect(bounds.getX() - pad, bounds.getY() - pad, bounds.getWidth() + pad + pad,
				bounds.getHeight() + pad + pad);
		this.hitbox = bounds;
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

	public Symbol() {

	}

	public Symbol(Point2D xy) {
		transform.translate(xy.getX(), xy.getY());
	}

	void updateLabel() {}

	public boolean at(Point2D xy) {
		return hitbox != null && gTransform.createTransformedShape(hitbox).contains(xy);
		// equivalent to checking if the hitbox contains the inverse gTransform of the
		// point
	}

	public boolean at(Rectangle2D lasso) {
		return hitbox != null && gTransform.createTransformedShape(hitbox).intersects(lasso);
	}

	protected void drawChildren(Graphics2D g) {
		for (Symbol child : children) child.draw(g);
	}

	public void drawAtOrigin(Graphics2D g) {
		drawChildren(g);
		Stroke restore = g.getStroke();
		if (isSelected() || selecting) {
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
		if (label != null) {
			g.setColor(Color.black);
			g.drawString(label, -25, sublabel == null ? 5 : 0);
		}
		if (sublabel != null) {
			g.scale(2.2, 2);
			g.drawString(sublabel, -10, label == null ? 5 : 9);
		}
		g.setStroke(restore);
	}

	public void draw(Graphics2D g) {
		AffineTransform restore = g.getTransform();
		g.transform(transform);
		gTransform = g.getTransform();
		drawAtOrigin(g);
		g.setTransform(restore);
	}

	boolean isSelected() {
		return selected;
	}

	void setSelected(boolean selected) {
		this.selected = selected;
	}

}