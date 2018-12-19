package jsimugate;

import java.util.ArrayList;
import java.util.List;

public class Part extends Symbol implements Cloneable {

	List<Pin> pins = new ArrayList<Pin>();

	enum Tech {
		DEFAULT, OC_NPN(Signal._1, Signal._Z, "\u2390"), OC_PNP(Signal._0, Signal._Z, "\u238F"),
		TTL_NPN(Signal._1, Signal._H), TTL_PNP(Signal._0, Signal._L);
		String label;
		Signal changeFrom = Signal._Z;
		Signal changeTo = Signal._Z;

		Tech(Signal changeFrom, Signal changeTo, String label) {
			this.changeFrom = changeFrom;
			this.changeTo = changeTo;
			this.label = label;
		}

		Tech(Signal changeFrom, Signal changeTo) {
			this.changeFrom = changeFrom;
			this.changeTo = changeTo;
		}

		Tech() {}
	};

	Tech tech = Tech.DEFAULT;

	public Part(double x, double y) {
		super(x, y);
	}

	public Pin addPin(Pin pin) {
		pins.add(pin); // to be connected
		addChild(pin); // to be drawn
		return pin;
	}

	public Pin removePin(Pin pin) {
		pins.remove(pin);
		removeChild(pin);
		return pin;
	}

	public void reshape(int n) {}

	public void setTech(Tech technology) {
		this.tech = technology;
		sublabel = technology.label;
	}

	public void increase() {}

	public void decrease() {}

	public void operate() {}

	/**
	 * Convert to an equivalent symbol such by DeMorganizing
	 */
	public Part convert() {
		return this;
	}

	public Part dup(int x, int y) {
		return null;
	}

}
