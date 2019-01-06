package jsimugate;

import java.awt.*;
import java.util.ArrayList;

import static jsimugate.Signal.*;

public class EdgeTrigD extends Box {
    Pin clkIn,rstIn;
    private ArrayList<Signal> qSave=new ArrayList<Signal>();
    Signal prevClk = _X;

    public EdgeTrigD() {
        name = "D EDGE";
        clkIn = addPin(sPins.addPinHorizontally()).down(30).translate(0, 40);
        rstIn = addPin(sPins.addPinHorizontally()).down(30).translate(0, 40);
        resize();
        addPin(wPins.addPinVertically()).left(30).translate(-width-30, 0);
        addPin(ePins.addPinVertically()).right(30).translate(width+30, 0);
        qSave.add(_X);
        resize();
    }

    public void drawAtOrigin(Graphics2D g) {
        super.drawAtOrigin(g);
        g.drawString("RST", -20, height - 2);
        g.drawString("D", -width + 5, 5);
        g.drawString("Q", width - 15, 5);
        g.rotate(-Math.PI/2);
        g.drawString(">", -height, 15);
    }

    public void operate() {
        Signal clk = clkIn.getInValue();
        Signal rst = rstIn.getInValue();
        for (int i = 0; i < wPins.size(); i++) {
            Signal d = wPins.pins.get(i).getInValue();
            if (rst.hi) qSave.set(i, _0);
            if (rst.bad||clk.bad) qSave.set(i, _X);
            else if (clk.hi && prevClk.lo) {
                if (d.hi) qSave.set(i, _1);
                else if (d.lo) qSave.set(i, _0);
                else qSave.set(i, _X);
            }
            ePins.pins.get(i).setOutValue(qSave.get(i));
        }
        prevClk = clk;
    }

    public void increase() {
        addPin(wPins.addPinVertically()).left(30).translate(-width-30, 0);
        addPin(ePins.addPinVertically()).right(30).translate(width+30, 0);
        qSave.add(_X);
        resize();
    }

    public void decrease() {
        int n = qSave.size();
        if (n < 2) return;
        if (Net.directConnections(ePins.pins.get(n - 1)).size() > 0) return;
        if (Net.directConnections(wPins.pins.get(n - 1)).size() > 0) return;
        removePin(wPins.removePinVertically());
        removePin(ePins.removePinVertically());
        qSave.remove(qSave.size() - 1);
        resize();

    }
}
