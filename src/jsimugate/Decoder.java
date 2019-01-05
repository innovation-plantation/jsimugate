package jsimugate;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class Decoder extends Box {
    Signal oldClk=null;
    public Decoder() {
        label="DEC";
        addPin(ePins.addPinVertically()).translate(width+30,0).right(30);
        increase();
        increase();
    }
    public void increase() {
        if (sPins.size()>=8) return;
        addPin(sPins.addPinHorizontally()).translate(0,height+30).down(30);
        resize();
        int n=ePins.size();
        for (int i=0;i<n;i++) addPin(ePins.addPinVertically()).translate(width+30,0).right(30);
        resize();
    }

    public void decrease() {
        int n=ePins.size()>>1;
        for (int i=n;i<2*n;i++) {
            if (Net.directConnections(ePins.pins.get(i)).size()>0) return;
        }
        if (removePin(sPins.removePinHorizontally())==null) return;
        resize();
        for (int i=0;i<n;i++) {
            removePin(ePins.removePinVertically());
        }
        resize();
    }

    public void drawAtOrigin(Graphics2D g) {
        super.drawAtOrigin(g);
        AffineTransform restore = g.getTransform();

    }


    public void operate() {
        for (Pin pin:sPins.pins) {
            if (pin.getInValue().bad) {
                for (Pin out:ePins.pins) {
                    out.setOutValue(Signal._X);
                }
                return;
            }
        }
        ePins.setValue(1<<sPins.getValue());
    }
}


