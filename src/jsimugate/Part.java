package jsimugate;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

/**
 * A generic part, to be overridden by specific parts for their individual operational behavior and appearance.
 */
public class Part extends Symbol {

    List<Pin> pins = new ArrayList<Pin>();

    /**
     * The technology of a part indicates the kind of weakening of the signal depending on its value.
     * For example, standard open collector does not drive 1 signals, they are weakened to Z values.
     * Standard TTL also weakens 1 values, but not so drastically, only to H values.
     * PUSH_PULL is the default for use in most cases when there is no need to weaken signal to simulate
     * one of those specific technologies.
     */
    enum Tech {
        PUSH_PULL("Push-Pull (like CMOS)"), // strong 1 and 0 push-pull like values (typically CMOS)
        TTL("Standard TTL (NPN)", Signal._1, Signal._H), // weak when 1 like standard TTL
        OC("Standard Open Collector (NPN)", Signal._1, Signal._Z, "\u2390"), // floating when 1 like TTL OC
        TTL_PNP("Nonstandard TTL (PNP)", Signal._0, Signal._L), // weak when 0 rare dual of standard TTL
        OC_PNP("Nonstandard Open Collector (PNP)", Signal._0, Signal._Z, "\u238F"); // floating when 0 rare in some PLC
        String mark;
        Signal changeFrom = Signal._Z;
        Signal changeTo = Signal._Z;
        String description;

        Tech(String description, Signal changeFrom, Signal changeTo, String mark) {
            this.description = description;
            this.changeFrom = changeFrom;
            this.changeTo = changeTo;
            this.mark = mark;
        }

        Tech(String description, Signal changeFrom, Signal changeTo) {
            this.description = description;
            this.changeFrom = changeFrom;
            this.changeTo = changeTo;
        }

        Tech(String description) {
            this.description = description;
        }
    }

    Tech tech = Tech.PUSH_PULL;

    String name; // Displayed in parts bin if part has no label

    /**
     * Add a pin to the part. The pin will be a child of the part, so it will be drawn in relation to the part's
     * orientation and size. Connections between parts and wires are made via pins.
     *
     * @param pin the pin to be added
     * @return the added pin
     */
    public Pin addPin(Pin pin) {
        pins.add(pin); // to be connected
        addChild(pin); // to be drawn
        return pin;
    }

    /**
     * Remove a pin from the part. It will no longer be drawn or used for connecting wires.
     *
     * @param pin
     * @return
     */
    public Pin removePin(Pin pin) {
        if (pin == null) return null;
        pins.remove(pin);
        removeChild(pin);
        return pin;
    }

    /**
     * Subclasses can have their own shapes. This will be called when the part's size increases or decreases.
     *
     * @param n size of parts (e.g. how many input pins on a gate)
     */
    public void reshape(int n) {
    }

    /**
     * Convert the technology to the one specified, such as open collector.
     *
     * @param technology
     * @return self (for chaining modifiers)
     */
    public Part asTech(Tech technology) {
        this.tech = technology;
        sublabel = technology.mark;
        return this;
    }

    /**
     * Increase something about the part such as clock rate or number of pins.
     */
    public void increase() {
    }

    /**
     * Decrease something about the part such as clock rate or number of pins.
     */
    public void decrease() {
    }

    /**
     * Update the output pins given the input pin values.
     */
    public void operate() {
    }

    /**
     * Process typed user input on the part
     *
     * @param ch
     */
    public void processChar(char ch) {
    }

    /**
     * Serialize the part to a string, suitable for saving and restoring using fromScanner.
     *
     * @return
     */
    public String toString() {
        String s = "";
        s += "PART:" + transform.toString().split("Transform")[1] + sn() + "(" + pins.size() + " PINS:";
        for (Pin pin : pins) {
            s += pin.inverted ? " -" : " +";
            s += pin.sn();
        }
        s += ") ";
        if (tech != Tech.PUSH_PULL) s += tech;
        s += ";" + getDetails() + "\n";
        return s;
    }

    /**
     * Reads a Part from a scanner, or returns null if there is none. Also updates a
     * pin map (pin-to-pin wiring between parts)
     *
     * @param scan   - a new part is read in from this scanner from the current
     *               line, if there is one there
     * @param pinMap - The pin numbers from the pins of the part are added to this
     *               map of numbers to pins. Pin numbers are intended to be unique
     *               in the circuit.
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
            Log.print("PART AT");
            float m00 = Float.parseFloat(result.group(1));
            float m01 = Float.parseFloat(result.group(2));
            float m02 = Float.parseFloat(result.group(3));
            float m10 = Float.parseFloat(result.group(4));
            float m11 = Float.parseFloat(result.group(5));
            float m12 = Float.parseFloat(result.group(6));
            Log.printf("\n%7.2f %7.2f %7.2f  ", m00, m01, m02);
            Log.printf("\n%7.2f %7.2f %7.2f  ", m10, m11, m12);
            // Inconsistent order of parameters in AffineTransform toString and constructor!
            AffineTransform t = new AffineTransform(m00, m10, m01, m11, m02, m12);
            String partName = result.group(7);
            int partNumber = Integer.parseInt(result.group(8));
            int pinCount = Integer.parseInt(result.group(9));
            try {
                Log.println("jsimugate." + partName);
                newPart = (Part) Class.forName("jsimugate." + partName).getConstructor().newInstance();
                newPart.transform.setTransform(t);
                while (newPart.pins.size() > pinCount) newPart.decrease();
                while (newPart.pins.size() < pinCount) newPart.increase();

                Log.print(partName + partNumber + " with " + pinCount + " pins:");

                for (int pinIndex = 0; scan.findInLine(part_pin_pattern) != null; pinIndex++) {
                    MatchResult pinResult = scan.match();
                    boolean invertPin = pinResult.group(1).equals("-");
                    int pinNumber = Integer.parseInt(pinResult.group(2));
                    if (invertPin) Log.print(" NOT");
                    Log.print(" pin" + pinNumber);
                    Pin pin = newPart.pins.get(pinIndex);
                    if (invertPin) pin.setInversion(true);
                    if (pinMap != null) pinMap.put(pinNumber, pin);
                }
                scan.findInLine("\\) *([^;]*) *;? *(.*)");
                MatchResult match = scan.match();
                String techString = match.group(1);
                newPart.tech = Tech.PUSH_PULL;
                if (!techString.isEmpty()) newPart.asTech(Tech.valueOf(techString));
                Log.println(" TECH is " + newPart.tech);
                String details = match.group(2);
                newPart.setDetails(details);
                if (!details.isEmpty()) Log.println(" DETAILS: " + details);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.println(scan.nextLine());
        }
        return newPart;
    }

    /**
     * Override setDetails and getDetails to serialize and desertailze extra information about the part
     * beyond its transform, type, pins, and technology.
     *
     * @param details
     */
    public void setDetails(String details) {
    }

    /**
     * Override setDetails and getDetails to serialize and desertailze extra information about the part
     * beyond its transform, type, pins, and technology.
     *
     * @return details
     */
    public String getDetails() {
        return "";
    }

    /**
     * Convert to an equivalent symbol such by DeMorganizing
     */
    public Part convert() {
        return this;
    }

    /**
     * Make a duplicate copy of this part at the specified location.
     *
     * @param x horizontal position
     * @param y vertical position
     * @return
     */
    public Part dup(double x, double y) {
        Part result = Part.fromScanner(new Scanner(toString()), null);
        result.transform.setToTranslation(x, y);
        return result;
    }
}
