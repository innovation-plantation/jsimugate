package jsimugate;

import java.awt.*;

/**
 * Implementation of diode component
 */
public class Diode extends Discrete {
    Pin anode, cathode; // anode and cathode

    /**
     * Create the diode, with the annode on the left and the cathode on the right
     * (current flows left to right)
     */
    public Diode() {
        this.setShape(Artwork.diodeShape(),10,12,10,5);
        this.addPin(anode = new Pin(-20, 0).left(10, false));
        this.addPin(cathode = new Pin(20, 0).right(10, false));
        this.anode.recovery=this.cathode.recovery=true;
        this.fill = Color.red;
        this.name = "DIODE";
    }

    public void operate() {
        int a = anode.getInValue().ordinal(), c = cathode.getInValue().ordinal();
        anode.setOutValue(Logic.anode_tt[a][c]);
        cathode.setOutValue(Logic.cathode_tt[a][c]);
    }
}
