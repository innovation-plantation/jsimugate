package jsimugate;

import java.util.*;

/**
 * Groups of wires and pins that are connected.
 */
public class Net {
    Set<Pin> pins = new HashSet<Pin>();
    Set<Wire> wires = new HashSet<Wire>();
    static List<Net> nets = new ArrayList<Net>();

    /**
     * This should be called every time a wire is constructed
     *
     * @param wire
     * @return the net that the wire should belong to.
     */
    public static void connect(Wire wire) {
        if (wire.src == null || wire.dst == null) return; // dummy wire
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
        dstNet.wires.add(wire);
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
        wire.src.setInValue(Signal._Z);
        wire.dst.setInValue(Signal._Z);
        Net net = null;
        for (Net n : nets)
            if (n.wires.contains(wire)) {
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
                            Log.println("SD " + src + " " + dst);
                            return wire;
                        }
                        if (wire.src == dst && wire.dst == src) {
                            Log.println("DS " + src + " " + dst);
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
     * Output all of the nets, wires, and pins as text for debugging purposes.
     */
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
