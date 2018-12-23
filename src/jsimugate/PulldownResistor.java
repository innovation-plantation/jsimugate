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
		this.pin = this.addPin(new Pin(0, -25).down(-10));
		this.name = "PULLDN";
	}

	public void operate() {
		this.pin.setOutValue(Signal._L);
	}
}
