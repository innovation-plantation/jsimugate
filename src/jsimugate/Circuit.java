package jsimugate;

import java.awt.*;
import java.util.List;
import java.util.*;

/**
 * A structure of parts and their wires along with the parts bins.
 */
public class Circuit {
    public List<Part> parts = new ArrayList<Part>();
    public List<PartsBin> bins = new ArrayList<PartsBin>();
    public ArrayList<Wire> wires = new ArrayList<Wire>();

    boolean debug; // when true, generate debug info to standard output

    /**
     * List of all parts suitable for saving. Inverse of fromString
     *
     * @return the string
     */
    public String toString() {
        String string = "";
        for (Part part : parts) string += part.toString();
        for (Wire wire : wires) string += wire.toString();
        return string;
    }

    /**
     * Load from list of all parts previously creared by fromString
     *
     * @return the string
     */
    public void fromString(String s) {
        Scanner scan = new Scanner(s);
        fromScanner(scan);
        scan.close();
    }

    /**
     * Read in the circuit from a scanner
     *
     * @param scan Scanner for the input stream
     */
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

    /**
     * Display all the components, wires, and parts bins.
     *
     * @param g
     */
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

    /**
     * Start the circuit running. With each tick, process all the wires and operate all the parts,
     * then call the repaint function to trigger updates to the display.
     *
     * @param repaint
     */
    public void startup(Runnable repaint) {
        new javax.swing.Timer(10, e -> {

            for (Net net : Net.nets) {
                Signal wire_value = Signal._Z;
                for (Pin pin : net.pins) {
                    wire_value = Logic.resolve_tt[wire_value.ordinal()][pin.getOutValue().ordinal()];
                }
                if (!net.recovery) { // for no-diodes on the net. avoid n^2 computation.
                    for (Pin pin : net.pins) {
                        pin.setInValue(wire_value);
                    }
                } else {  // Order N^2 computation is required here
                    // for stability in circuits having diodes,
                    // exclude your own output from what you see on the wire.
                    for (Pin pin : net.pins) {
                        Signal pin_value = Signal._Z;
                        for (Pin other : net.pins) { // for diodes
                            if (pin != other) {
                                pin_value = Logic.resolve_tt[pin_value.ordinal()][other.getOutValue().ordinal()];
                            }
                        }
                        pin.setInValue(pin_value);
                    }
                }

                for (Wire wire : net.wires) wire.value = wire_value;
            }
            for (Part part : parts) { // set output pin values from f(input pin values)
                part.operate();
            }
            repaint.run();
        }).start();
    }

    public Circuit withStandardBins() {
        int xPos = 50, yPos = 50;
        for (Part part : new Part[]{
                new OrGate(), new AndGate(), new MajorityGate().not(),
                new XorGate(), new Bus(), new ThreeState(),
                new InConnector(), new Diode(), new OutConnector(),
                new VGround(), new NPNTransistor(), new PullupResistor(),
                new VSource(), new PNPTransistor(), new PulldownResistor(),
                null,
                new Clk(), new RingCounter(), new Counter(),
                new Decoder(), new Mux(), new DMux(),
                new LevelTrigSR(), new LevelTrigD(), new EdgeTrigD(),
                new Adder(), new Alu(), new ROMemory("[80] 55 aa 55 aa 55 aa 55 aa"),
                new Keyboard(), new RegisterFile(), new Memory(),
                null,
                new Display(),
                new SevenSegmentDecoder(),
                new SevenSegmentLED(),

        }) {
            if (part == null) {
                yPos += 10;
                continue;
            }
            bins.add(new PartsBin(xPos, yPos, part));
            xPos += 50;
            if (xPos > 150) {
                xPos = 50;
                yPos += 50;
            }
        }
        return this;

    }
}