package jsimugate;

import java.awt.*;

import static jsimugate.Signal.*;

public class LevelTrigSR extends Box {
    Pin rIn,sIn,qOut,qBar;

    private Signal qSave=_X;
    private Signal qNotNotSave=_X;

    public LevelTrigSR() {
        name="SR";
        wPins.gap=ePins.gap=true;
        resize();
        rIn=this.wPins.addPinVertically().left(30).translate(-40,0);
        sIn=this.wPins.addPinVertically().left(30).translate(-40,0);
        qBar=this.ePins.addPinVertically().right(30).translate(40,0);
        qOut=this.ePins.addPinVertically().right(30).translate(40,0);
        addPin(sIn);
        addPin(rIn);
        addPin(qOut);
        addPin(qBar);
        this.resizeWithPadding(1,0);
        qBar.setInversion(true);
    }

    public void drawAtOrigin(Graphics2D g) {
        super.drawAtOrigin(g);
        g.drawString("S",-width+5,-height+15);
        g.drawString("R",-width+5,height-5);
        g.drawString("Q",width-15,-height+15);
        g.drawString("Q",width-15,height-5);
    }

    public void operate() {
        Signal s=sIn.getInValue(), r=rIn.getInValue();
        if (s.bad || r.bad) qSave = qNotNotSave = _X;
        else if (s.hi && r.hi) { // for sr 1 has priority, for rs it would be 0
            qSave =_1;
            qNotNotSave = _0;
        } else if (s.hi)  qSave=qNotNotSave=_1;
        else if (r.hi) qSave=qNotNotSave=_0;
        else if (qSave != qNotNotSave) qSave = qNotNotSave = _X;
        qOut.setOutValue(qSave);
        qBar.setOutValue(qNotNotSave);
    }
}
