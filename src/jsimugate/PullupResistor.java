package jsimugate;

import java.awt.Color;

public class PullupResistor extends Part {

	private Pin pin;

	public PullupResistor(double x, double y) {
		super(x, y);
		this.setShape(Artwork.pullupShape());
		this.hitbox = this.shape.getBounds2D();
		this.color = Color.red;
		this.pin = this.addPin(new Pin(0, 25).down(10));
		this.name = "PULLUP";
	}

	public void operate() {
		this.pin.setOutValue(Signal._H);
	}
}
