package jsimugate;

import java.awt.*;

/**
 * Implementation of diode component.
 * Diode operation is a bit tricky. There's kludge logic in the Net class to keep them
 * from causing toggling back and forth. To do that, a diode looks at the value of what
 * would be on the wire network if the diode were not there. For that network, resolving
 * pin values becomes O(N*N) so there's a recovery bit flag in the part if it's a diode,
 * and every Net has a recovery bit on if there's any diodes in the net, which
 * allows it to skip the O(N*N) computation for all nets that don't contain diodes.
 */
public class Diode extends Discrete {
    Pin anode, cathode; // anode and cathode

    /**
     * Create the diode, with the annode on the left and the cathode on the right
     * (current flows left to right)
     */
    public Diode() {
        this.setShape(Artwork.diodeShape(), 10, 12, 10, 5);
        this.addPin(anode = new Pin(-20, 0).left(10, false));
        this.addPin(cathode = new Pin(20, 0).right(10, false));
        this.anode.recovery = this.cathode.recovery = true;
        this.fill = Color.red;
        this.name = "DIODE";
    }

    /**
     * Operate the diode by setting the annode and cathode outputs according to the
     * table lookup on their inputs. Messing with the tables can have undesirable
     * results if for example you attempt to propagate X values through the network, they
     * would get stuck wherever two diodes meet. Therefore X values don't propagate through
     * diodes in this model.
     */
    public void operate() {
        int a = anode.getInValue().ordinal(), c = cathode.getInValue().ordinal();
        anode.setOutValue(Logic.anode_tt[a][c]);
        cathode.setOutValue(Logic.cathode_tt[a][c]);
    }
}
