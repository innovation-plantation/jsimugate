package jsimugate;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Circuit {
	public List<Part> parts;
	public List<PartsBin> bins;
	public ArrayList<Wire> wires;

	boolean debug;

	public Circuit(List<Part> parts, List<PartsBin> bins, ArrayList<Wire> wires) {
		this.parts = parts;
		this.bins = bins;
		this.wires = wires;
	}

	public String toString() {
		String string = "";
		for (Part part : parts) string += part.toString();
		for (Wire wire : wires) string += wire.toString();
		return string;
	}


	public void fromString(String s) {
		Scanner scan = new Scanner(s);
		fromScanner(scan);
		scan.close();
	}

	public void fromScanner(Scanner scan) {
		for (Part part : parts) part.setSelected(false);
		Map<Integer, Pin> pinMap = new HashMap<Integer, Pin>();
		while (scan.hasNextLine()) {
			Part newPart = Part.fromScanner(scan, pinMap);
			if (newPart != null) {
				parts.add(newPart);
				newPart.setSelected(true);
				continue;
			}
			Wire newWire = Wire.fromScanner(scan, pinMap);
			if (newWire != null) {
				// the other end of the wire doesn't exist because parts on one end are not
				// being duplicated
				if (newWire.src == null) continue;
				if (newWire.dst == null) continue;
				wires.add(newWire);
				continue;
			}
			System.err.println("No match reading data: " + scan.nextLine());
		}
	}
	
	public void render(Graphics2D g) {
		for (Part part : parts) {
			part.draw(g);
			for (Pin pin : part.pins) {
				pin.setInValue(Signal._Z);
			}
		}

		for (Wire wire : wires) wire.draw(g);

		for (PartsBin bin : bins) {
			bin.draw(g);
		}
	}


}