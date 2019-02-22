package jsimugate;

import java.awt.*;

/**
 * Implementation of Keyboard device. A high "down" signal indicates when a key is down,
 * and the ASCII code is also output.
 */
public class Keyboard extends Box {
    boolean keydown;
    int key;

    /**
     * Creaate the keyboard with pins for output
     */
    public Keyboard() {
        label = "KBD";
        for (int i = 0; i < 7; i++) {
            addPin(nPins.addPinHorizontally()).up(30).translate(0, -height - 30);
        }
        addPin(ePins.addPinVertically()).right(30).translate(width + 30, 0);
        resizeWithPadding(-1, 0);
    }

    /**
     * Labeling of pins and display of the currently pressed character
     *
     * @param g graphics context for drawing
     */
    public void drawAtOrigin(Graphics2D g) {
        super.drawAtOrigin(g);
        g.drawString("DN", 50, 5);
        g.drawString("msb", -68, -8);
        g.setColor(color.red);
        g.drawString(Character.toString((char) key), 20, 5);
        g.setColor(new Color(0, 0, 0, 0x40));
        g.draw(Artwork.keyboardShape());
    }

    /**
     * When a character is pressed down, activate the "down" line.
     * When it is released, deactivate the "down" line.
     * Ignore modifier keys like "shift" and "ctrl". They have codes >= 0xFF.
     *
     * @param c
     */
    public void processChar(char c) {
        if (c >= 0xFF) return;
        keydown = c != '\0';
        key = c;
    }

    public void operate() {
        ePins.setValue(keydown ? 1 : 0);
        nPins.setValue(key);
    }
}


