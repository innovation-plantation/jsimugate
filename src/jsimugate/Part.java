package jsimugate;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import jsimugate.Part.Tech;

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

	public String toString() {
		String s="";
		s += "PART:" + transform.toString().split("Transform")[1] + sn() + "(" + pins.size()
				+ " PINS:";
		for (Pin pin : pins) {
			s += pin.inverted ? " -" : " +";
			s += pin.sn();
		}
		s += ") ";
		if (tech != Tech.DEFAULT) s += tech;
		s += "\n";
		return s;
	}
	/**
	 * Reads a Part from a scanner, or returns null if there is none. Also updates a pin map (pin-to-pin wiring between parts)
	 * @param scan - a new part is read in from this scanner from the current line, if there is one there
	 * @param pinMap - The pin numbers from the pins of the part are added to this map of numbers to pins. Pin numbers are intended to be unique in the circuit.
	 * @return the new part, or null if unable to read a part from the current line
	 */
	public static Part fromScanner(Scanner scan, Map<Integer, Pin> pinMap) {
		final String t_rule = "\\[ *\\[ *([-0-9.]+) *, *([-0-9.]+) *, *([-0-9.]+) *\\] *, *\\[ *([-0-9.]+) *, *([-0-9.]+) *, *([-0-9.]+) *\\] *\\]";
		final String part_prepins_rule = "PART: *" + t_rule + " *([A-Za-z_0-9]+)#([0-9]+)\\(([0-9]+) PINS:";
		final Pattern part_pin_pattern = Pattern.compile("([-+])Pin#([0-9]+)");
		final Pattern part_pattern = Pattern.compile(part_prepins_rule);

		Part newPart = null;
		if (scan.findInLine(part_pattern) != null) {
			MatchResult result = scan.match();
			log("PART AT");
			float m00 = Float.parseFloat(result.group(1));
			float m01 = Float.parseFloat(result.group(2));
			float m02 = Float.parseFloat(result.group(3));
			float m10 = Float.parseFloat(result.group(4));
			float m11 = Float.parseFloat(result.group(5));
			float m12 = Float.parseFloat(result.group(6));
			logf("\n%7.2f %7.2f %7.2f  ",m00,m01,m02);
			logf("\n%7.2f %7.2f %7.2f  ",m10,m11,m12);
			// Inconsistent order of parameters in AffineTransform toString and constructor!
			AffineTransform t = new AffineTransform(m00, m10, m01, m11, m02 + 150, m12 + 50);
			String partName = result.group(7);
			int partNumber = Integer.parseInt(result.group(8));
			int pinCount = Integer.parseInt(result.group(9));
			try {
				logline("jsimugate." + partName);
				newPart = (Part) Class.forName("jsimugate." + partName)
						.getConstructor(double.class, double.class).newInstance(200, 200);
				newPart.transform.setTransform(t);
				while (newPart.pins.size() > pinCount) newPart.decrease();
				while (newPart.pins.size() < pinCount) newPart.increase();

				log(partName + partNumber + " with " + pinCount + " pins:");

				
				for (int pinIndex = 0;scan.findInLine(part_pin_pattern) != null;pinIndex++) {
					MatchResult pinResult = scan.match();
					boolean invertPin = pinResult.group(1).equals("-");
					int pinNumber = Integer.parseInt(pinResult.group(2));
					if (invertPin) log(" NOT");
					log(" pin" + pinNumber);
					Pin pin = newPart.pins.get(pinIndex);
					if (invertPin) pin.toggleInversion();
					pinMap.put(pinNumber, pin);
				}
				scan.findInLine("\\) *([^ ]*)");
				String techString=scan.match().group(1);
				Tech tech=Tech.DEFAULT;
				if (!techString.isEmpty()) tech = Tech.valueOf(techString);
				logline(" TECH " + tech);
			} catch (Exception e) {
				e.printStackTrace();
			}
			logline(scan.nextLine());
		}
		return newPart;
	}
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
