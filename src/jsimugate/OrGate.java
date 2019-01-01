package jsimugate;

public class OrGate extends Gate {
    /**
     * Construct new gate centered at the origin
     */
    public OrGate() {
        super();
        opposite = AndGate.class.getSimpleName();
    }

    /**
     * Use the distinctive gate symbol
     *
     * @param n number of pins used to size the shape
     */
    public void reshape(int n) {
        setShape(Artwork.orShape(n));
    }

    /**
     * identity for OR.  (0 OR X) = X
     *
     * @return identity value for the function
     */
    public Signal function() {
        return Signal._0;
    }

    /**
     * Or the two values together
     *
     * @param a The first value
     * @param b The second signal
     * @return The result of the two signals being ORed together by a gate
     */
    public Signal function(Signal a, Signal b) {
        return Logic.or_tt[a.ordinal()][b.ordinal()];
    }

    /**
     * Switch the label between OR and NOR depending on the inversion of the output pin
     */
    public void updateLabel() {
        label = output.inverted ? "NOR" : "OR";
    }
}
