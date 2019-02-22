package jsimugate;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * The basic drawing element for all hierarchical components that have orientations and positions
 * recorded in transforms relative to their parents. They have shapes and hit boxes that are used
 * for drawing and pick testing.
 */
public class Symbol extends Numbered {
    static final Shape defaultPath = new Rectangle2D.Double(-4, -4, 8, 8);
    static final Stroke defaultStroke = new BasicStroke(3);

    static Stroke getDefaultStroke() {
        return defaultStroke;
    }

    static final Stroke thinStroke = new BasicStroke(2);

    static Stroke getThinStroke() {
        return new BasicStroke(2);
    }

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

    /**
     * Create a symbol at the origin.
     */
    public Symbol() {
    }

    /**
     * Create a symbol at position x,y
     *
     * @param x
     * @param y
     */
    public Symbol(double x, double y) {
        super();
        transform.setToTranslation(x, y);
    }

    /**
     * Add a child symbol to this one, so that whenever this is rendered, the child will be as well.
     *
     * @param child
     * @return
     */
    public Symbol addChild(Symbol child) {
        child.parent = this;
        children.add(child);
        return this;
    }

    /**
     * Remove the child symbol from this symbol so that whenever this is rendered, the child will
     * no longer be rendered with it.
     *
     * @param child
     * @return
     */
    public Symbol removeChild(Symbol child) {
        child.parent = null;
        children.remove(child);
        return this;
    }

    /**
     * Sets the hitbox to the padded bounding box
     *
     * @param shape     - new shape
     * @param padTop    - padding to the top (pixel units)
     * @param padRight- padding to the right(pixel units)
     * @param padBottom - padding to the bottom (pixel units)
     * @param padLeft   - padding to the left (pixel units)
     * @return
     */

    public Symbol setShape(Shape shape, int padTop, int padRight, int padBottom, int padLeft) {
        this.shape = shape;
        Rectangle2D bounds = shape.getBounds2D();
        bounds.setRect(bounds.getX() - padLeft, bounds.getY() - padTop, bounds.getWidth() + padLeft + padRight,
                bounds.getHeight() + padTop + padBottom);
        this.hitbox = bounds;
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
        return setShape(shape, pad, pad, pad, pad);
    }

    /**
     * Set the shape and hitbox to that of the provided shape
     *
     * @param shape
     * @return
     */
    public Symbol setShape(Shape shape) {
        this.shape = shape;
        this.hitbox = shape;
        return this;
    }


    /**
     * Set the outline color of this symbol
     *
     * @param color
     * @return
     */
    public Symbol setColor(Color color) {
        this.color = color;
        return this;
    }

    /**
     * Set the fill color of this symbol
     *
     * @param color
     * @return
     */
    public Symbol setFill(Color color) {
        this.fill = color;
        return this;
    }

    /**
     * Update the label of this symbol - to be overwritten by subclasses which change their labels over time.
     */
    void updateLabel() {
    }

    /**
     * Indicate whether the xy point is inside the hitbox, taking into consideration transformations.
     *
     * @param xy the coordinate of the point
     * @return
     */
    public boolean at(Point2D xy) {
        return hitbox != null && gTransform.createTransformedShape(hitbox).contains(xy);
        // equivalent to checking if the hitbox contains the inverse gTransform of the
        // point
    }

    /**
     * Indicate whether the lasso rectangle intersects the hitbox, taking into consideration transformations.
     *
     * @param lasso the lasso of selection
     * @return
     */
    public boolean at(Rectangle2D lasso) {
        return hitbox != null && gTransform.createTransformedShape(hitbox).intersects(lasso);
    }

    /**
     * Draw all the children of this symbol
     *
     * @param g graphics context for drawing
     */
    protected void drawChildren(Graphics2D g) {
        for (Symbol child : children) child.draw(g);
    }

    /**
     * Draw this symbol with all of its children at the origin.
     * Some symbols may override this for custom drawing.
     *
     * @param g graphics context for drawing
     */
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
            AffineTransform restoreT = g.getTransform();
            g.scale(2.2, 2);
            g.drawString(sublabel, -10, label == null ? 5 : 9);
            g.setTransform(restoreT);
        }
        g.setStroke(restore);
    }

    /**
     * Draw the part and all of its children wherever it belongs on the display
     * according to transforms of ancestors.
     *
     * @param g
     */
    public void draw(Graphics2D g) {
        AffineTransform restore = g.getTransform();
        g.transform(transform);
        gTransform = g.getTransform();
        drawAtOrigin(g);
        g.setTransform(restore);
    }

    /**
     * Indicates whether or not this is selected
     *
     * @return true if the symbol is marked as selected.
     */
    boolean isSelected() {
        return selected;
    }

    /**
     * Set the selection status to selected (true) or not (false)
     *
     * @param selected true if the symbol is to be marked as selected
     */
    void setSelected(boolean selected) {
        this.selected = selected;
    }

}