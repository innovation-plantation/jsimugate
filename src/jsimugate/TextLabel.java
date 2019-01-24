package jsimugate;

import javax.swing.*;
import java.awt.*;

/**
 * Implement a text label to be placed independently of parts.
 */
public class TextLabel extends Part {
    Shape defaultShape;
    public TextLabel() {
        name="TEXT";
        label="T";
        this.color = this.fill = null;
        defaultShape = shape;
    }

    /**
     * Just draw the text, but highlight it if it's selected.
     * @param g graphics context for drawing
     */
    public void drawAtOrigin(Graphics2D g) {
        if (shape==defaultShape) {
            setShape(g.getFontMetrics().getStringBounds(label,g));
        }
        if (isSelected()) {
            g.setColor(highlightColor);
            g.fill(hitbox);
        }
        g.setColor(Color.black);
        g.drawString(label,0,0);
    }

    /**
     * Upon double-clicking, accept new text for the label
     */
    public void processDoubleClick(){
        String newLabel = JOptionPane.showInputDialog(null, "Enter new text:",
                        "Text Label", 1);
        if (newLabel==null || newLabel.matches(" *")) return;
        label = newLabel;
        shape = defaultShape; // trigger recalculating the new shape within the graphics context
    }
}
