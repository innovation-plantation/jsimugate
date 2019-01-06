package jsimugate;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

/**
 * Implementation of a parts bin: A box from which new parts can be dragged
 * to be created and to which existing parts can be dropped to dispose of them.
 * The box is labeled with a mini part symbol and its name
 */
public class PartsBin extends Part {

    private Part prototype;
    double scale = .25;

    /**
     * Create the parts bin labeled with the name and shape of the part.
     *
     * @param x    horizontal position of the bin
     * @param y    horizontal position of the bin
     * @param part The part that will be cloned when dragging from the parts bin.
     */
    public PartsBin(double x, double y, Part part) {
        super();
        this.transform.setToTranslation(x, y);
        this.prototype = part;
        this.setShape(new Rectangle2D.Double(-25, -25, 50, 50));
        this.addChild(part);
        Rectangle bounds = part.hitbox.getBounds();
        double w = bounds.width, h = bounds.height;
        scale = .25; // this looks better than linear scaling because of aliasing.
        if (h > 100) scale /= 3;
        if (w < 40) scale *= 1.5;

        this.fill = new Color(0x00, 0xFF, 0xFF, 0x10);
        this.color = Color.gray;
    }

    /**
     * Render the parts bin on the graphics context.
     *
     * @param g
     */
    public void drawAtOrigin(Graphics2D g) {
        g.setColor(fill);
        g.fill(shape);
        g.setColor(color);
        g.draw(shape);
        AffineTransform restore = g.getTransform();

        g.scale(scale, scale);
        g.translate(0, -10);
        drawChildren(g);
        g.setTransform(restore);
        g.scale(.75, .75);

        if (prototype != null) {
            String text = prototype.name;
            if (text == null) text = prototype.label;
            if (text != null) {
                g.setColor(Color.blue);
                g.drawString(text, -30, 30);
            }
        }
        g.setTransform(restore);
    }

    /**
     * Produce a new part at the desired location
     *
     * @param x horizontal position
     * @param y vertical position
     * @return the new part
     */
    public Part produce(double x, double y) {
        return prototype.dup(x, y);
    }
}
