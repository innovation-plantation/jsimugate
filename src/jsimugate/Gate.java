package jsimugate;

import java.util.Scanner;

/**
 * Abstraction of a gate, which is a part with multiple pins and a single output.
 */
public class Gate extends Part {

    //List<Pin> inputs = new ArrayList<Pin>();
    PinGroup inputs = new PinGroup(true);
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
        output.setOutValue(Signal._U);
        setSelected(false);
    }

    /**
     * Add a pin to the gate
     */
    protected void addInput() {
        Pin pin = inputs.addPinVertically().translate(-70, 0).left(30);
        addPin(pin);
        reshape(inputs.size());
        updateLabel();
    }

    /**
     * Remove the most recently added pin if it's not connected to anything
     */
    protected void removeInput() {
        Pin pin = inputs.removePinVertically();
        if (pin != null) {
            removePin(pin);
            reshape(inputs.size());
            updateLabel();
        }
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
        for (Pin i : inputs.pins) result = function(result, i.getInValue());
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
        that.output = output;
        that.inputs = inputs;
        that.pins = pins;
        that.children = children;
        for (Symbol child : children) child.parent = that;
        for (Pin pin : pins) pin.toggleInversion();
        return that;
    }
}
