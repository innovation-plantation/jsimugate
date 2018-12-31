package jsimugate;

import java.awt.Color;

public class VGround extends Discrete {
	private Pin pin;

	public VGround() {
		this(0, 0);
	}

	public VGround(double x, double y) {
		super(x, y);
		this.setShape(Artwork.vGroundShape(),10);
		this.color = Color.black;
		this.fill = Color.black;
		this.pin = this.addPin(new Pin(0, 0).up(15,false));
		this.name = "GND";
	}

	public void operate() {
		this.pin.setOutValue(Signal._0);
	}
}
