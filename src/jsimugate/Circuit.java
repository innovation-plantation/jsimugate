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
		Scanner scan = new Scanner(s);
		Map<Integer, Pin> pinMap = new HashMap<Integer, Pin>();
		while (scan.hasNextLine()) {
			Part newPart=Part.fromScanner(scan, pinMap);  
			if (newPart!=null) {
				parts.add(newPart);	
				continue;
			}
			Wire newWire = Wire.fromScanner(scan, pinMap); 
			if (newWire!=null) {
				wires.add(newWire);
				continue;
			}
			System.err.println("No match reading data: " + scan.nextLine());
			
		}
		scan.close();
		// call repaint() on graphics after performing this function
	}



}