package jsimugate;

public class NPNTransistor extends Transistor {
	public NPNTransistor() {
		this(0, 0);
	}

	public NPNTransistor(double x, double y) {
		super(x, y);
		this.arrow = npn_arrow;
		this.name = "NPN";
		this.tt = Logic.npn_tt;
	}
}
