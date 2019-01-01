package jsimugate;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Abstraction of a gate, which is a part with multiple inputs and a single output.
 */
public class Gate extends Part {

    List<Pin> inputs = new ArrayList<Pin>();
    Pin output;

    /**
     * Create a gate at the origin
     */
    public Gate() {
        super();
        reshape(0);
        addPin(output = new Pin(80, 0).right(40));
        addInput();
        addInput();
        setSelected(false);
    }

    /**
     * Add an input pin to the gate, shifting the other inputs over as necessary to make room
     */
    protected void addInput() {
        int n = inputs.size();
        reshape(n + 1);
        switch (n) {
            case 0:
                inputs.add(addPin(new Pin(-80, 0).left(40)));
                break;
            case 1:
                inputs.get(0).transform.translate(0, -20);
                inputs.add(addPin(new Pin(-80, 20).left(40)));
                break;
            case 2:
                inputs.get(1).transform.translate(0, -20);
                inputs.add(addPin(new Pin(-80, 20).left(40)));
                break;
            default:
                for (Pin i : inputs) i.translate(0, -10);
                inputs.add(addPin(new Pin(-80, n * 10).left(40)));
        }
        updateLabel();
    }

    /**
     * Remove the last pin if it's not connected to anything
     */
    protected void removeInput() {
        int n = inputs.size();
        if (n < 1) return;
        Pin victim = inputs.get(n - 1);
        // if wires attached return now without removing victim.
        if (Net.directConnections(victim).size() > 0) return;
        reshape(n - 1);
        inputs.remove(removePin(victim));
        switch (n) {
            case 1:
                break;
            case 2:
                inputs.get(0).transform.translate(0, 20);
                break;
            case 3:
                inputs.get(1).transform.translate(0, 20);
                break;
            default:
                for (Pin i : inputs) i.translate(0, 10);
        }
        updateLabel();
    }

    /**
     * The identity value for the function that the gate returns.
     * Could be abstract, arbitrarily not so.
     *
     * @return U signal indicating the value was not overridden by the actual gate
     */
    public Signal function() {
        return Signal._U;
    }

    /**
     * The function that the gate returns. Could be abstract, arbitrarily not so.
     *
     * @return U signal indicating the value was not overridden by the actual gate
     */
    public Signal function(Signal a, Signal b) {
        return Signal._U;
    }

    /**
     * Compute the function of all the input pins, and set the output pin accordingly
     */
    public void operate() {
        Signal result = function();
        for (Pin i : inputs) result = function(result, i.getInValue());
        output.setOutValue(result);
    }

    /**
     * request to increase the number of pins on the part
     */
    public void increase() {
        addInput();
    }

    /**
     * request to decrease the number of pins on the part
     */
    public void decrease() {
        removeInput();
    }

    /**
     * Inverts the output
     *
     * @return this, for chaining modifiers like this.
     */
    public Gate not() {
        output.toggleInversion();
        return this;
    }

    /**
     * Pilfer resources from this for that as with move semantics.
     * <p>
     * After return, caller is expected to use that instead of this, setting
     * whatever pointers used this to point to that instead.
     */
    String opposite;

    public Part convert() {
        if (opposite == null) return super.convert();
        String s = toString().replaceAll(this.getClass().getSimpleName(), opposite);

        Gate that = (Gate) Part.fromScanner(new Scanner(s), null);
        that.inputs = inputs;
        that.output = output;
        that.pins = pins;
        that.children = children;
        for (Symbol child : children) child.parent = that;
        for (Pin pin : pins) pin.toggleInversion();
        return that;
    }
}
