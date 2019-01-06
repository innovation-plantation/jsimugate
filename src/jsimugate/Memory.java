package jsimugate;

import java.awt.*;

import static jsimugate.Signal._X;
import static jsimugate.Signal._Z;

public class Memory extends Box {
    Pin wClkIn, rdEnaIn;
    private Integer[] qSave=new Integer[256];
    Signal prevClk = _X;

    public Memory() {
        name = "RAM";
        for (int i=0;i<8;i++) {
            addPin(wPins.addPinVertically()).left(30).translate(-width-30, 0);
            addPin(ePins.addPinVertically()).right(30).translate(width+30, 0);
        }
        resize();
        rdEnaIn = addPin(sPins.addPinHorizontally()).down(30).translate(0, height+30);
        wClkIn = addPin(sPins.addPinHorizontally()).down(30).translate(0, height+30);
        resize();
    }

    public void drawAtOrigin(Graphics2D g) {
        super.drawAtOrigin(g);
        g.drawString("RD", width-30, height -5);
        g.drawString("A", -width + 5, 5);
        g.drawString("D", width - 15, 5);
        g.drawString("W", -width+15, height -10);
        g.drawString("RAM",-15,-15);
        g.rotate(-Math.PI/2);
        g.drawString(">", -height, -width+24);
    }

    public void operate() {
        Signal clk = wClkIn.getInValue();
        Signal rd = rdEnaIn.getInValue();
        int sel = wPins.getValue();
        boolean selValid = wPins.goodValue();
        if (clk.hi && prevClk.lo) { // handle write pulse
            boolean goodInData = ePins.goodValue();
            if (selValid) qSave[sel] = goodInData?ePins.getValue():null;
            else for (int i=0;i<8;i++) qSave[i] = null;
            Log.print("WR data "+(goodInData?"+":"*")+ePins.getValue());
            Log.println(" to "+(selValid?"+":"*")+sel);
        }
        if (rd.hi) {
            Integer value = qSave[sel];
            if (selValid && value!=null) ePins.setValue(value);
            else for (int i=0;i<8;i++) ePins.pins.get(i).setOutValue(_X);
        } else for (int i=0;i<8;i++) ePins.pins.get(i).setOutValue(_Z);
        prevClk = clk;
    }
}
