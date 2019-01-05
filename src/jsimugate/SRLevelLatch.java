package jsimugate;

import java.awt.*;

public class SRLevelLatch extends Box {
    Pin rIn,sIn,qOut,qBar;
    Signal m1=Signal._X,m2=Signal._X;
    public SRLevelLatch() {
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
        g.drawString("" +
                "S",-width+5,-height+15);
        g.drawString("R",-width+5,height-5);
        g.drawString("Q",width-15,-height+15);
        g.drawString("Q",width-15,height-5);
    }

    public void operate() {



    }
}
