package jsimugate;

import java.awt.Color;

public class PulldownResistor extends Part {

	private Pin pin;

	public PulldownResistor(double x, double y) {
		super(x, y);
		this.setShape(Artwork.pulldownShape());
		this.hitbox = this.shape.getBounds2D();
		this.color = Color.black;
		this.fill = Color.black;
		this.pin = this.addPin(new Pin(0, -20));
		this.name = "PULLDN";
	}

	/**
	 * override to prevent changing from default
	 */
	public Part asTech(Tech tech) {
		this.tech = Tech.PUSH_PULL;
		return this;
	}

	public void operate() {
		this.pin.setOutValue(Signal._L);
	}
}
