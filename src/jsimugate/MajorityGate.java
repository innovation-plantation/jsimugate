package jsimugate;

/**
 * Implementation of a majority gate or not gate or buffer depending on the number of pins and
 * whether or not the output is inverted.
 */
public class MajorityGate extends Gate {

    /**
     * Create a part at the origin
     */
    public MajorityGate() {
        super();
        removeInput();
    }

    /**
     * Set the shape of the part to a majority gate part shape for n pins
     *
     * @param n number of pins
     */
    public void reshape(int n) {
        setShape(Artwork.majorityShape(n));
    }

    /**
     * process the pins and determine the output pin value.
     */
    public void operate() {
        int hi = 0, lo = 0;
        for (Pin i : inputs.pins) {
            if (i.getInValue().bad) {
                output.setOutValue(Signal._X);
                return;
            }
            if (i.getInValue().hi) hi++;
            if (i.getInValue().lo) lo++;
        }
        if (hi > lo) output.setOutValue(Signal._1);
        else if (lo > hi) output.setOutValue(Signal._0);
        else output.setOutValue(Signal._X);
    }

    /**
     * Increase the number of pins
     */
    public void increase() {
        addInput();
        if (inputs.size() % 2 == 0) addInput();
    }

    /**
     * Decrease the number of pins
     */
    public void decrease() {
        if (inputs.size() > 1) removeInput();
        if (inputs.size() % 2 == 0) removeInput();
    }

    /**
     * Update the label to NOT, BUFFER, or MAJ3, MAJ5, etc. depending on number of pins and inversion.
     */
    public void updateLabel() {
        label = inputs.size() > 1 ? "MAJ" + inputs.size() : output.inverted ? "NOT" : "BUFFER";
    }

    public Part convert() {
        for (Pin pin : pins) pin.toggleInversion();
        return this;
    }
}
