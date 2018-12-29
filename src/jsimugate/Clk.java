package jsimugate;

import java.awt.geom.Rectangle2D;

public class Clk extends Part {

	static final Rectangle2D rect = new Rectangle2D.Double(-40, -20, 60, 40);
	private Pin pin;
	Signal value = Signal._0;
	private javax.swing.Timer timer;

	public Clk() {
		super();
		setShape(rect);
		label = "CLK";
		addPin(pin = new Pin(45, 0).right(25));
		timer = new javax.swing.Timer(500, e -> {
			value = value == Signal._0 ? Signal._1 : Signal._0;
		});
		timer.start();
	}

	public void operate() {
		pin.setOutValue(value);
	}

}
