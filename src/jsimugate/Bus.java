package jsimugate;

import java.awt.*;

/**
 * A group of connectors to connect wires to.
 */
public class Bus extends Box {
    public Bus() {
        this.fill = new Color(0, 0, 0, 1);
        increase();
        increase();
        increase();
        increase();
        this.name = "BUS";
    }

    /**
     * Make wider bus
     */
    public void increase() {
        addPin(wPins.addPinVertically());
        resize();
    }

    /**
     * Make narrower bus
     */
    public void decrease() {
        removePin(wPins.removePinVertically());
        resize();
    }
}
