package jsimugate;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class Display extends Box {
    Pin clkIn;
    String disp = "@";
    int dispValue;

    public Display() {
        name = "DISP";
        for (int i = 0; i < 7; i++) {
            addPin(nPins.addPinHorizontally()).up(30).translate(0, -height - 30);
            addPin(sPins.addPinHorizontally()).down(30).translate(0, height + 30);
        }
        resizeWithPadding(1, 6);
        clkIn = addPin(wPins.addPinVertically()).left(30).translate(-width - 30, 0);
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
        g.drawString("lsb", width - 35, height - 3);
        g.drawString("B", width - 55, height - 1);
        g.drawString("D", width - 75, height - 1);
        g.drawString("H", width - 95, height - 1);
        g.drawString("P", width - 115, height - 1);
        g.drawString("a", width - 135, height - 1);
        g.drawString("A", width - 155, height - 1);
        g.drawString(">", -width, 4);
        g.rotate(Math.PI / 2);
        g.scale(12, 16);
        FontMetrics metrics = g.getFontMetrics();
        int x = -metrics.stringWidth(disp) / 2, y = metrics.getHeight() / 4;
        g.setColor(Color.yellow);
        g.drawString(disp, x, y);
        g.setColor(Color.blue);
    }

    Signal oldClkValue = Signal._Z;

    public void operate() {
        Signal newClkValue = clkIn.getInValue();
        nPins.setValue(dispValue);
        if (newClkValue.hi && oldClkValue.lo) {
            dispValue = sPins.getValue();
            disp = Character.toString((char) dispValue);
        }
        oldClkValue = newClkValue;
    }
}
