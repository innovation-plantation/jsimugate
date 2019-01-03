package jsimugate;

/**
 * Logic gate implementation for XOR.
 */
public class XorGate extends Gate {
    /**
     * Construct new gate centered at the origin
     */
    public XorGate() {
        super();
    }

    /**
     * Use the distinctive gate symbol
     *
     * @param n number of pins used to size the shape
     */
    public void reshape(int n) {
        setShape(Artwork.xorShape(n));
    }

    /**
     * identity for AND.  (1 AND X) = X
     *
     * @return identity value for the function
     */
    public Signal function() {
        return Signal._0;
    }

    /**
     * And the two values together
     *
     * @param a The first value
     * @param b The second signal
     * @return The result of the two signals being ANDed together by a gate
     */
    public Signal function(Signal a, Signal b) {
        return Logic.xor_tt[a.ordinal()][b.ordinal()];
    }

    /**
     * Switch the label between AND and NAND depending on the inversion of the output pin
     */
    public void updateLabel() {
        label = output.inverted ? "XNOR" : "XOR";
    }

    /**
     * Eliminate inversons on pins, if odd number of inversions on pins, transfer inversion to output pin
     *
     * @return self for chaining.
     */
    public Part convert() {
        for (Pin pin : inputs.pins) {
            if (pin.inverted) {
                pin.toggleInversion();
                output.toggleInversion();
            }
        }
        return this;
    }
}
