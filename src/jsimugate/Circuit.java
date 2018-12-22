package jsimugate;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import jsimugate.Part.Tech;

public class Circuit {
	public List<Part> parts;
	public List<PartsBin> bins;
	public ArrayList<Wire> wires;

    boolean debug;
	void logline() {
		if (debug) System.out.println();
	}
	void logline(String s) {
		if (debug) System.out.print(s);
	}
	void log(String s) {
		if (debug) System.out.print(s);
	}
	void logf(String format,float f0,float f1,float f2) {
		if (debug) System.out.printf(format, f0,f1,f2);
	};
	public Circuit(List<Part> parts, List<PartsBin> bins, ArrayList<Wire> wires) {
		this.parts = parts;
		this.bins = bins;
		this.wires = wires;
	}
	public String toString() {
		String string = "";
		for (Part part :parts) string += part.toString();
		for (Wire wire : wires) string += wire.toString();
		return string;
	}

	public void fromString(String s) {
		final String t_rule = "\\[ *\\[ *([-0-9.]+) *, *([-0-9.]+) *, *([-0-9.]+) *\\] *, *\\[ *([-0-9.]+) *, *([-0-9.]+) *, *([-0-9.]+) *\\] *\\]";
		final String part_prepins_rule = "PART: *" + t_rule + " *([A-Za-z_0-9]+)#([0-9]+)\\(([0-9]+) PINS:";
		final Pattern part_pin_pattern = Pattern.compile("([-+])Pin#([0-9]+)");
		final Pattern part_pattern = Pattern.compile(part_prepins_rule);
		final Pattern wire_pattern = Pattern.compile("WIRE: *Pin#([0-9]+) *- *Pin#([0-9]+)");
		Scanner scan = new Scanner(s);
		Map<Integer, Pin> construction = new HashMap<Integer, Pin>();
		while (scan.hasNextLine()) {
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
					Part newPart = (Part) Class.forName("jsimugate." + partName)
							.getConstructor(double.class, double.class).newInstance(200, 200);
					newPart.transform.setTransform(t);
					while (newPart.pins.size() > pinCount) newPart.decrease();
					while (newPart.pins.size() < pinCount) newPart.increase();
					parts.add(newPart);
					log(partName + partNumber + " with " + pinCount + " pins:");

					
					for (int pinIndex = 0;scan.findInLine(part_pin_pattern) != null;pinIndex++) {
						MatchResult pinResult = scan.match();
						boolean invertPin = pinResult.group(1).equals("-");
						int pinNumber = Integer.parseInt(pinResult.group(2));
						if (invertPin) log(" NOT");
						log(" pin" + pinNumber);
						Pin pin = newPart.pins.get(pinIndex);
						if (invertPin) pin.toggleInversion();
						construction.put(pinNumber, pin);
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
			} else if (scan.findInLine(wire_pattern) != null) {
				MatchResult result = scan.match();
				int a=Integer.parseInt(result.group(1));
				int b=Integer.parseInt(result.group(2));
				logline("WIRE pin" + a + " to pin" + b);
				scan.nextLine();
				wires.add(new Wire(construction.get(a),construction.get(b)));
			} else {
				System.err.println("No match reading data: " + scan.nextLine());
			}
		}
		scan.close();
		// call repaint() on graphics after performing this function
	}

}