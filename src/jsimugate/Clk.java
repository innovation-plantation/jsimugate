package jsimugate;

import java.awt.geom.Rectangle2D;

public class Clk extends Part {

	static final Rectangle2D rect = new Rectangle2D.Double(-40, -20, 60, 40);
	private Pin pin;
	Signal value = Signal._0;
	private javax.swing.Timer timer;
	int hz=1,sec=1;

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
	
	public void adjustClock() {
		timer.setDelay(500*sec/hz);
		if (hz>sec) label = hz+" Hz";	
		else if (sec>hz) label = sec+" sec";
		else label="CLK";
	}
	
	public void increase() {
		if (sec>1) sec/=2;
		else if (hz<64) hz*=2;
		adjustClock();
	}

	public void decrease() {
		if (hz>1) hz/=2;
		else if (sec<64) sec*=2;
		adjustClock();
	}
}
