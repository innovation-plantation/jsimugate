package jsimugate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Part extends Symbol {

	List<Pin> pins = new ArrayList<Pin>();

	enum OC {
		NPN("\u2390"), PNP("\u238F"),NONE;
		String label;
		OC() {}
		OC(String label) {
			this.label = label;
		}
	};

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
	
	public void setOC(OC oc) {
		sublabel = oc.label;
	}

	public void increase() {}

	public void decrease() {}

	public void operate() {}
}
