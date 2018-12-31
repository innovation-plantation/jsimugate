package jsimugate;

import java.awt.Color;

public class VSource extends Discrete {
	private Pin pin;

	public VSource() {
		this(0, 0);
	}

	public VSource(double x, double y) {
		super(x, y);
		this.setShape(Artwork.vSourceShape(),10);
		this.color = Color.red;
		this.fill = Color.red;
		this.pin = this.addPin(new Pin(0, 5).down(15,false));
		this.name = "SOURCE";
	}

	public void operate() {
		this.pin.setOutValue(Signal._1);
	}
}
