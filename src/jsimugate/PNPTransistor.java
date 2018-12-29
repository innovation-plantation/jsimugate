package jsimugate;

import java.awt.Color;

public class PNPTransistor extends Transistor {

	public PNPTransistor(double x, double y) {
		super(x, y);
		this.arrow = pnp_arrow;
		this.name = "PNP";
		this.tt = Logic.pnp_tt;
		this.color=Color.red;
	}
}
