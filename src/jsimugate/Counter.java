package jsimugate;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class Counter extends Box {
    Pin ld,rst,clk;
    int number;
    Signal oldClk=null;
    public Counter() {
        label="CTR";

        rst=addPin(sPins.addPinHorizontally()).translate(0,40).down(30);
        clk=addPin(sPins.addPinHorizontally()).translate(0,40).down(30);
        ld=addPin(sPins.addPinHorizontally()).translate(0,40).down(30);
        resize();
        for (int i=0;i<8;i++) increase();
    }
    public void increase() {
        if (ePins.size()>31) return;
        addPin(ePins.addPinVertically()).translate(width+30,0).right(30);
        addPin(wPins.addPinVertically()).translate(-width-30,0).left(30);
        resize();
    }

    public void decrease() {
        removePin(wPins.removePinVertically());
        removePin(ePins.removePinVertically());
        resize();
    }

    public void drawAtOrigin(Graphics2D g) {
        super.drawAtOrigin(g);
        AffineTransform restore = g.getTransform();
        g.rotate(-Math.PI/2);
        g.translate(-height,-20);
        int h = ePins.size();
        if (h>1) g.drawString("LD",2,4);
        g.translate(0,20);
        g.drawString(h>2?"> CLK":">",0,4);
        g.translate(0,20);
        if (ePins.size()>0) g.drawString("RST",2,4);
        g.setTransform(restore);
        g.translate(width-20,height-15);
        g.setTransform(restore);
        g.translate(width-1,height-15);
        for (int i=0;i<ePins.size();i++) {
            //if (i==0||i%2!=0) continue;
            String text=Integer.toString(1<<i);
            int textWidth = g.getFontMetrics().stringWidth(text);
            g.drawString(text,-textWidth,-i*20);
        }
    }

    public void reset() {
        number = 0;
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
            ePins.setValue(number);
            if (oldClk.hi != clk.getInValue().hi) {
                number++;
                number %= 1<<ePins.size();
                if (ld.getInValue().hi) number = wPins.getValue();
            }
        }
        oldClk = newClk;
    }
}
