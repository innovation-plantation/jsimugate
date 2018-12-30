package jsimugate;

import java.awt.Color;

import jsimugate.Part.Tech;

public class Diode extends Part {
	Pin anode,cathode; // anode and cathode
	public Diode() {
		this.shape = Artwork.diodeShape();
		this.addPin(anode=new Pin(-20,0).left(10));
		this.addPin(cathode=new Pin(20,0).right(10));
		this.fill = Color.black;
	}
	/**
	 * override to prevent changing from default
	 */
	public Part asTech(Tech tech) {
		this.tech = Tech.PUSH_PULL;
		return this;
	}
	
	public void operate() {
		int a=anode.getInValue().ordinal(), c=cathode.getInValue().ordinal();
		anode.setOutValue(Logic.anode_tt[a][c]);
		cathode.setOutValue(Logic.cathode_tt[a][c]);
	}
}
