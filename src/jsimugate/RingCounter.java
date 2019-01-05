package jsimugate;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class RingCounter extends Box {
    Pin rst,clk;
    int phase;
    Signal oldClk=null;

    public RingCounter() {
        label="RING";
        rst=addPin(sPins.addPinHorizontally()).translate(0,40).down(30);
        clk=addPin(sPins.addPinHorizontally()).translate(0,40).down(30);
        resize();
        increase();
        increase();
        increase();
        increase();
    }
    public void increase() {
        addPin(ePins.addPinVertically()).translate(width+30,0).right(30);
        resize();
    }

    public void decrease() {
        removePin(ePins.removePinVertically());
        resize();
    }

    public void drawAtOrigin(Graphics2D g) {
        super.drawAtOrigin(g);
        AffineTransform restore = g.getTransform();
        g.rotate(-Math.PI/2);
        g.translate(-height,-10);

        g.drawString("> ",0,4);
        g.translate(0,20);
        if (ePins.size()>0) g.drawString("RST",1,4);
        g.setTransform(restore);
        g.translate(width-1,height-15);
        for (int i=0;i<ePins.size();i++) {
            String text="\u03D5"+i;
            int textWidth = g.getFontMetrics().stringWidth(text);
            g.drawString(text,-textWidth,-i*20);
        }
    }

    public void reset() {
        phase = 0;
        oldClk=clk.getInValue();
    }
    public void operate() {
        if (rst.getInValue().hi) {
            reset();
            for (Pin pin:ePins.pins) {
                pin.setOutValue(Signal._0);
            }
            return;
        }
        Signal newClk = clk.getInValue();
        if (oldClk==null || !oldClk.good || !newClk.good || !rst.getInValue().good) {
            oldClk = null;
            for (int i=0;i<ePins.size();i++) {
                ePins.pins.get(i).setOutValue(Signal._X);
            }
            return;
        }
        if (oldClk.lo && newClk.hi) {
            for (int i = 0; i < ePins.size(); i++) {
                if (i == phase) ePins.pins.get(i).setOutValue(Signal._1);
                else ePins.pins.get(i).setOutValue(Signal._0);
            }
            if (oldClk.hi != clk.getInValue().hi) {
                if (++phase >= ePins.size()) phase = 0;
            }
        }
        oldClk = newClk;
    }
}
