package jsimugate;

import java.util.ArrayList;
import java.util.List;

/**
 * Stack pins horizontally or vertically.
 * The caller is responsible for adding the pins to their parts,
 * resizing the shape of the part, and updating the label if necessary.
 */
public class PinGroup {
    List<Pin> pins = new ArrayList<Pin>();
    boolean gap = false;

    public PinGroup() {}
    public PinGroup(boolean gap) {this.gap=gap;}

    /**
     * Returns how many pins are in the group
     * @return the number of pins
     */
    int size() {
        return pins.size();
    }

    /**
     * Add and return a pin to the group, shifting the other pins over as necessary to make room
     * These pins are stacked vertically, with new pins on top
     */
    public Pin addPinVertically() {
        int n = pins.size();
        Pin pin;
        if (gap) switch (n) {
            case 1:
                pins.get(0).translate(0, 20);
                pins.add(pin = new Pin(0, -20));
                return pin;
            case 2:
                pins.get(1).translate(0, 20);
                pins.add(pin = new Pin(0, -20));
                return pin;
        }
        for (Pin i : pins) i.translate(0, 10);
        pins.add(pin = new Pin(0, -10 * n));
        return pin;

    }

    /**
     * Add and return a pin to the group, shifting the other pins over as necessary to make room
     * These pins are arranged horizontally, with the new pin on the left
     *
     */
    public Pin addPinHorizontally(Pin pin) {
        int n = pins.size();
        if (gap) switch (n) {
            case 1:
                pins.get(0).translate(20, 0);
                pins.add(pin);
                return pin;
            case 2:
                pins.get(1).translate(20, 0);
                pins.add(pin);
                return pin;
        }
        for (Pin i : pins) i.translate(10, 0);
        pins.add(pin);
        return pin;
    }
    public Pin addPinHorizontally() {
        int n = pins.size();
        if (gap) switch (n) {
            case 1:
                return addPinHorizontally(new Pin(-20, 0));
            case 2:
                return addPinHorizontally(new Pin(-20, 0));
        }
        for (Pin i : pins) i.translate(10, 0);
        return addPinHorizontally(new Pin(-10 * n, 0));
    }

    /**
     * Remove the last pin if it's not connected to anything
     * These pins are arranged vertically, with the new pins on top
     */
    protected Pin removePinVertically() {
        int n = pins.size();
        if (n < 1) return null;
        Pin victim = pins.get(n - 1);
        // if wires attached return now without removing victim.
        if (Net.directConnections(victim).size() > 0) return null;

        pins.remove(victim);
        switch (n) {
            case 1:
                break;
            case 2:
                pins.get(0).translate(0, -20);
                break;
            case 3:
                pins.get(1).translate(0, -20);
                break;
            default:
                for (Pin i : pins) i.translate(0, -10);
        }
        return victim;
    }

    /**
     * Remove the last pin if it's not connected to anything
     * These pins are arranged vertically, with the new pins on top
     */
    protected Pin removePinHorizontally() {
        int n = pins.size();
        if (n < 1) return null;
        Pin victim = pins.get(n - 1);
        // if wires attached return now without removing victim.
        if (Net.directConnections(victim).size() > 0) return null;

        pins.remove(victim);
        switch (n) {
            case 1:
                break;
            case 2:
                pins.get(0).translate(-20, 0);
                break;
            case 3:
                pins.get(1).translate(-20, 0);
                break;
            default:
                for (Pin i : pins) i.translate(-10, 0);
        }
        return victim;
    }

    /**
     * Read the binary value from the pins.
     * Invalid bits are interpreted as zero.
     * @return the integer number converted from binary.
     */
    public int getValue() {
        int result = 0;
        for (int n = 0; n < pins.size(); n++) result += pins.get(n).getInValue().asBit() << n;
        return result;
    }

    /**
     * Drive the pins with the binary value.
     * @param value the number to be converted to binary
     */
    public void setValue(int value) {
        for (int n = 0; n < pins.size(); n++) pins.get(n).setOutValue(Signal.fromBit((value >> n) & 1));
    }
}
