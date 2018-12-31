package jsimugate;

import java.awt.Color;

public class PullupResistor extends Discrete {

	private Pin pin;

	public PullupResistor() {
		this(0, 0);
	}

	public PullupResistor(double x, double y) {
		super(x, y);
		this.setShape(Artwork.pullupShape());
		this.hitbox = this.shape.getBounds2D();
		this.color = Color.red;
		this.pin = this.addPin(new Pin(0, 20));
		this.name = "PULLUP";
	}

	public void operate() {
		this.pin.setOutValue(Signal._H);
	}
}
