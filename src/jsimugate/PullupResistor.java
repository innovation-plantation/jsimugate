package jsimugate;

import java.awt.Color;

public class PullupResistor extends Part {

	private Pin pin;

	public PullupResistor(double x, double y) {
		super(x, y);
		this.setShape(Artwork.pullupShape());
		this.hitbox = this.shape.getBounds2D();
		this.color = Color.red;
		this.pin = this.addPin(new Pin(0, 20));
		this.name = "PULLUP";
	}

	/**
	 * override to prevent changing from default
	 */
	public Part asTech(Tech tech) {
		this.tech = Tech.PUSH_PULL;
		return this;
	}

	public void operate() {
		this.pin.setOutValue(Signal._H);
	}
}
