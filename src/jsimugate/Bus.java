package jsimugate;

import java.awt.*;

public class Bus extends Box {
    public Bus() {
        this.fill = new Color(0,0,0,1);
        increase();
        increase();
        increase();
        increase();
        this.name="BUS";
    }
    public void increase() {
        addPin(wPins.addPinVertically());
        resize();
    }
    public void decrease() {
        removePin(wPins.removePinVertically());
        resize();
    }
}
