package jsimugate;

import java.util.ArrayList;
import java.util.List;

/**
 * Stack pins horizontally or vertically.
 * The caller is responsible for adding the pins to their parts,
 * resizing the shape of the part, and updating the label if necessary.
 */
public class PinGroup {
    public static List<PinGroup> pinGroups = new ArrayList<PinGroup>();
    List<Pin> pins = new ArrayList<Pin>();
    boolean gap = false;


    public PinGroup() {
    }

    public PinGroup(boolean gap) {
        this.gap = gap;
    }

    /**
     * Returns how many pins are in the group
     *
     * @return the number of pins
     */
    int size() {
        return pins.size();
    }

    /**
     * if there's a PinGroup that contains the pin, find and return it
     *
     * @param pin being searched for
     * @return group containing the pin
     */
    static PinGroup groupOf(Pin pin) {
        for (PinGroup group : pinGroups) {
            if (group.pins.contains(pin)) {
                return group;
            }
        }
        return null;
    }

    private void addPin(Pin pin) {
        if (pins.isEmpty()) pinGroups.add(this);
        pins.add(pin);
    }

    private void removePin(Pin victim) {
        pins.remove(victim);
        if (pins.isEmpty()) pinGroups.remove(this);
    }

    public void shiftPinsHorizontally(int dx) {
        for (Pin i : pins) i.translate(dx, 0);
    }

    public void shiftPinsVertically(int dy) {
        for (Pin i : pins) i.translate(0, dy);
    }

    /**
     * Add and return a pin to the group, shifting the other pins over as necessary to make room
     * These pins are stacked vertically, with new pins on top
     */
    public Pin addPinVertically(Pin pin) {
        int n = pins.size();
        if (gap) switch (n) {
            case 1:
                pins.get(0).translate(0, 20);
                addPin(pin);
                return pin;
            case 2:
                pins.get(1).translate(0, 20);
                addPin(pin);
                return pin;
        }
        shiftPinsVertically(10);
        addPin(pin);
        return pin;
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
                return addPinVertically(new Pin(0, -20));
            case 2:
                return addPinVertically(new Pin(0, -20));
        }
        return addPinVertically(new Pin(0, -10 * n));
    }

    /**
     * Add and return a pin to the group, shifting the other pins over as necessary to make room
     * These pins are arranged horizontally, with the new pin on the left
     */
    public Pin addPinHorizontally(Pin pin) {
        int n = pins.size();
        if (gap) switch (n) {
            case 1:
                pins.get(0).translate(20, 0);
                addPin(pin);
                return pin;
            case 2:
                pins.get(1).translate(20, 0);
                addPin(pin);
                return pin;
        }
        shiftPinsHorizontally(10);
        addPin(pin);
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

        removePin(victim);
        if (gap) switch (n) {
            case 1:
                return victim;
            case 2:
                pins.get(0).translate(0, -20);
                return victim;
            case 3:
                pins.get(1).translate(0, -20);
                return victim;
        }
        shiftPinsVertically(-10);
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

        removePin(victim);
        if (gap) switch (n) {
            case 1:
                return victim;
            case 2:
                pins.get(0).translate(-20, 0);
                return victim;
            case 3:
                pins.get(1).translate(-20, 0);
                return victim;
        }
        shiftPinsHorizontally(-10);
        return victim;
    }


    /**
     * Read the binary value from the pins.
     * Invalid bits are interpreted as zero.
     *
     * @return the integer number converted from binary.
     */
    public long getLongValue() {
        long result = 0;
        for (int n = 0; n < pins.size(); n++) result += (long) pins.get(n).getInValue().asBit() << n;
        return result;
    }

    public int getValue() {
        return (int) getLongValue();
    }

    public boolean goodValue() {
        for (Pin pin : pins) if (pin.getInValue().bad) return false;
        return true;
    }

    /**
     * Drive the pins with the binary value.
     *
     * @param value the number to be converted to binary
     */
    public void setValue(int value) {
        for (int n = 0; n < pins.size(); n++) pins.get(n).setOutValue(Signal.fromBit((value >> n) & 1));
    }
}
