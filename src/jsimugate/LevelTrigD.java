package jsimugate;

import java.awt.*;

import static jsimugate.Signal.*;
import static jsimugate.Signal._X;

public class LevelTrigD extends Box {
    Pin dIn, clkIn,qOut,qBar;

    private Signal qSave=_X;
    private Signal qNotNotSave=_X;

    public LevelTrigD() {
        name="D";
        resize();
        clkIn = addPin(sPins.addPinHorizontally()).down(30).translate(0,40);
        resize();
        dIn =addPin(this.wPins.addPinVertically()).left(30).translate(-50,0);
        qOut=addPin(this.ePins.addPinVertically()).right(30).translate(50,0);
        this.resize();
    }

    public void drawAtOrigin(Graphics2D g) {
        super.drawAtOrigin(g);
        g.drawString("CLK",-10,height-5);
        g.drawString("D",-width+5,5);
        g.drawString("Q",width-15,5);
    }

    public void operate() {
        Signal d= dIn.getInValue(), clk= clkIn.getInValue();
        if (d.bad || clk.bad) qSave = _X;
        else if (clk.hi) qSave=d.not().not();
        qOut.setOutValue(qSave);
    }
}
