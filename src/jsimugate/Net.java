package jsimugate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Net {
	Set<Pin> pins=new HashSet<Pin>();
	List<Wire> wires=new ArrayList<Wire>();
	static List<Net> nets=new ArrayList<Net>();
	
	/**
	 * This should be called every time a wire is constructed
	 * @param wire
	 * @return the net that the wire should belong to. 
	 */
	public static void connect(Wire wire) {
		Net srcNet=null,dstNet=null;
		for (Net net:nets) {
			if (net.pins.contains(wire.src)) srcNet = net;
			if (net.pins.contains(wire.dst)) dstNet = net;
		}
		// they're noth null, create a new one with the wire. Or if the same node, just return it with the wire added
		if (srcNet==dstNet) {
			if (srcNet==null) {
				srcNet = new Net();
				srcNet.pins.add(wire.src);
				srcNet.pins.add(wire.dst);
				nets.add(srcNet);
			}
			srcNet.wires.add(wire);
//			wire.net = srcNet;
			return; // srcNet;
		}
		// if one is null, return the other one with the wire added
		if (dstNet==null) {
			srcNet.wires.add(wire);
//			wire.net = srcNet;
			return; // srcNet;
		}
		if (srcNet==null) {
			dstNet.wires.add(wire);
//			wire.net=dstNet;
			return; // dstNet;
		}
		// merge two nets
		dstNet.pins.addAll(srcNet.pins);
		dstNet.wires.addAll(srcNet.wires);
//	    wire.net = dstNet; 
	//	for (Wire w:srcNet.wires) w.net = dstNet;
		nets.remove(srcNet);
		// help out the garbage collector
		srcNet.pins.clear();
		srcNet.wires.clear();
		srcNet.pins=null;
		srcNet.wires = null;
		srcNet = null;
		System.gc();
		//return dstNet;
	}

	/**
	 * This should be called every time a wire is being removed.
	 * @param wire
	 */
	public static void disconnect(Wire wire) {
		// find the net with the wire
		Net net=null;
		for (Net n:nets) if (n.wires.contains(wire)) {
			net=n;
			break;
		}
		// rebuild new nets from all the other wires in the net, after removing the net from the nets list
		nets.remove(net);
		net.wires.remove(wire);
		for (Wire w:net.wires) connect(w);  
		// help out the garbage collector
		net.pins.clear();
		net.wires.clear();
		net.pins=null;
		net.wires=null;
		net=null;
		System.gc();
	}
	
	public static Wire findWire(Pin src,Pin dst) {
		for (Net net:nets) {
			if (net.pins.contains(src)) {
				if (net.pins.contains(dst)) {					
					for (Wire wire:net.wires) {
						if (wire.src==src && wire.dst==dst || wire.src==dst && wire.dst==src) return wire;
					}
					return null; // src pin not connected directly to dst
				}
				return null; // src pin not connected to dst
			}
		}
		return null; // src pin not connected to anything
	}
}
