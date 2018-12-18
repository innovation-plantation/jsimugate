package jsimugate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Net {
	private Set<Pin> pins = new HashSet<Pin>();
	private Set<Wire> wires = new HashSet<Wire>();
	private static List<Net> nets = new ArrayList<Net>();

	/**
	 * This should be called every time a wire is constructed
	 * 
	 * @param wire
	 * @return the net that the wire should belong to.
	 */
	public static void connect(Wire wire) {
		Net srcNet = null, dstNet = null;
		for (Net net : nets) {
			if (net.pins.contains(wire.src)) srcNet = net;
			if (net.pins.contains(wire.dst)) dstNet = net;
		}
		// they're noth null, create a new one with the wire. Or if the same node, just
		// return it with the wire added
		if (srcNet == dstNet) {
			if (srcNet == null) {
				srcNet = new Net();
				srcNet.pins.add(wire.src);
				srcNet.pins.add(wire.dst);
				nets.add(srcNet);
			}
			srcNet.wires.add(wire);
			return;
		}
		// if one is null, return the other one with the wire added
		if (dstNet == null) {
			srcNet.pins.add(wire.dst);
			srcNet.wires.add(wire);
			return;
		}
		if (srcNet == null) {
			dstNet.pins.add(wire.src);
			dstNet.wires.add(wire);
			return;
		}
		// merge two nets
		dstNet.pins.addAll(srcNet.pins);
		dstNet.wires.addAll(srcNet.wires);
		nets.remove(srcNet);
		// help out the garbage collector
		srcNet.pins.clear();
		srcNet.wires.clear();
		srcNet.pins = null;
		srcNet.wires = null;
		srcNet = null;
		System.gc();
		// return dstNet;
	}

	/**
	 * This should be called every time a wire is being removed.
	 * 
	 * @param wire
	 */
	public static void disconnect(Wire wire) {
		// find the net with the wire
		Net net = null;
		for (Net n : nets) if (n.wires.contains(wire)) {
			net = n;
			break;
		}
		// rebuild new nets from all the other wires in the net, after removing the net
		// from the nets list
		nets.remove(net);
		net.wires.remove(wire);
		for (Wire w : net.wires) connect(w);
		// help out the garbage collector
		net.pins.clear();
		net.wires.clear();
		net.pins = null;
		net.wires = null;
		net = null;
		System.gc();
	}

	/**
	 * Returns the wire that connects the src and dst pins, ignores order of
	 * parameters
	 * 
	 * @param src one end of the connection,
	 * @param dst other end of the connection
	 * @return the wire
	 */
	public static Wire findWire(Pin src, Pin dst) {
		if (src == null || dst == null) return null;
		for (Net net : nets) {
			if (net.pins.contains(src)) {
				if (net.pins.contains(dst)) {
					for (Wire wire : net.wires) {
						if (wire.src == src && wire.dst == dst) {
							System.out.println("SD "+src+" "+dst);
							return wire;
						}
						if (wire.src == dst && wire.dst == src) {
							System.out.println("DS "+src+" "+dst);
							return wire;
						}
					}
					return null; // src pin not connected directly to dst
				}
				return null; // src pin not connected to dst
			}
		}
		return null; // src pin not connected to anything
	}

	/**
	 * Find all wires directly connected to the pin
	 * 
	 * @param pin
	 * @return the set of wires connected to this pin
	 */
	public static Collection<Wire> directConnections(Pin pin) {
		HashSet<Wire> result = new HashSet<Wire>();
		for (Net net : nets) {
			if (net.pins.contains(pin)) for (Wire wire : net.wires) {
				if (wire.src == pin) result.add(wire);
			}
			if (net.pins.contains(pin)) for (Wire wire : net.wires) {
				if (wire.dst == pin) result.add(wire);
			}
		}
		return result;
	}

	/**
	 * Resolve the output values from the parts of all connected signal values on
	 * the connected network of wires Set all the wire values and pin input values
	 * accordingly.
	 */
	public void operate() {
		Signal result = Signal._Z;
		for (Pin pin : pins) result = Logic.resolve_tt[result.ordinal()][pin.getOutValue().ordinal()];
		for (Pin pin : pins) pin.setInValue(result);
		for (Wire wire : wires) wire.value = result;
	}

	public static void operateAll() {
		for (Net net : nets) net.operate();
	}

	public static void dump() {
		System.out.println("Nets:" + nets.size());
		for (Net net : nets) {
			System.out.println(" Net:" + net + "  Wires:" + net.wires.size() + "  Pins:" + net.pins.size());
			System.out.print("  Pins:");
			for (Pin pin : net.pins) System.out.print(" " + pin);
			System.out.println();
			System.out.print("  Wires:");
			for (Wire wire : net.wires) System.out.print(" " + wire);
			System.out.println();
		}
	}
}
