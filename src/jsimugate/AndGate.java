package jsimugate;

/**
 * Logic gate implementation for AND.
 */
public class AndGate extends Gate {

    /**
     * Construct new gate centered at the origin
     */
    public AndGate() {
        super();
        opposite = OrGate.class.getSimpleName();
    }

    /**
     * Use the distinctive gate symbol
     *
     * @param n number of pins used to size the shape
     */
    public void reshape(int n) {
        setShape(Artwork.andShape(n));
    }

    /**
     * identity for AND.  (1 AND X) = X
     *
     * @return identity value for the function
     */
    public Signal function() {
        return Signal._1;
    }

    /**
     * And the two values together
     *
     * @param a The first value
     * @param b The second signal
     * @return The result of the two signals being ANDed together by a gate
     */
    public Signal function(Signal a, Signal b) {
        return Logic.and_tt[a.ordinal()][b.ordinal()]; // use a lookup table
    }

    /**
     * Switch the label between AND and NAND depending on the inversion of the output pin
     */
    public void updateLabel() {
        label = output.inverted ? "NAND" : "AND";
    }
}
