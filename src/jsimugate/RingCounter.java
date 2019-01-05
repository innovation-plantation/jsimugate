package jsimugate;

import java.awt.*;

public class RingCounter extends Box {
    Pin rst,clk;
    public RingCounter() {
        label="RING";
        resize();
        clk=addPin(sPins.addPinHorizontally()).translate(0,40).down(30);
        rst=addPin(sPins.addPinHorizontally()).translate(0,40).down(30);
        increase();
        increase();
        increase();
        increase();
        resize();
    }
    public void increase() {
        addPin(ePins.addPinVertically()).translate(60,0).right(30);
        resize();
    }

    public void decrease() {
        removePin(ePins.removePinVertically());
        resize();
    }

    public void drawAtOrigin(Graphics2D g) {
        super.drawAtOrigin(g);
        g.rotate(-Math.PI/2);
        g.translate(-height,-10);
        if (ePins.size()>1) g.drawString("RST",1,4);
        g.translate(0,20);
        g.drawString("> ",0,4);

    }
    public void operate() {

    }
}
