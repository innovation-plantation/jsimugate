package jsimugate;

import java.awt.Color;

public class Diode extends Discrete {
	Pin anode, cathode; // anode and cathode

	public Diode() {
		this.shape = Artwork.diodeShape();
		this.addPin(anode = new Pin(-20, 0).left(10,false));
		this.addPin(cathode = new Pin(20, 0).right(10,false));
		this.fill = Color.red;
		this.name = "DIODE";
	}

	public void operate() {
		int a = anode.getInValue().ordinal(), c = cathode.getInValue().ordinal();
		anode.setOutValue(Logic.anode_tt[a][c]);
		cathode.setOutValue(Logic.cathode_tt[a][c]);
	}
}
