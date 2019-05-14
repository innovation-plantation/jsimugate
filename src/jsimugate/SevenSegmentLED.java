package jsimugate;

import java.awt.*;


public class SevenSegmentLED extends Box {
    static Shape[] shapes = {
            Artwork.aSegmentShape(),
            Artwork.bSegmentShape(),
            Artwork.cSegmentShape(),
            Artwork.dSegmentShape(),
            Artwork.eSegmentShape(),
            Artwork.fSegmentShape(),
            Artwork.gSegmentShape(),

    };

    /**
     * Create the part with 7 pins
     */
    public SevenSegmentLED() {
        name = "7SEG";
        addPinsW(7);
        resizeWithPadding(3, -1);
        fill = Color.darkGray;
    }

    /**
     * Add RAM-specific pin labeling to the part
     *
     * @param g graphics context for drawing
     */
    public void drawAtOrigin(Graphics2D g) {
        super.drawAtOrigin(g);
        g.setColor(color.lightGray);
        g.drawString("a", -width, 63);
        g.drawString("b", -width, 43);
        g.drawString("c", -width, 23);
        g.drawString("d", -width, 3);
        g.drawString("e", -width, -17);
        g.drawString("f", -width, -37);
        g.drawString("g", -width, -57);

        for (int i = 0; i < 7; i++) {
            Signal signal = wPins.pins.get(i).getInValue();
            Color color = signal.fgColor;
            if (signal.bad) color = color.darker().darker();
            if (signal.hi) color = color.brighter().brighter();
            g.setColor(color);
            g.fill(shapes[i]);
        }
    }
}
