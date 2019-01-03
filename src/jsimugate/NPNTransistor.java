package jsimugate;

/**
 * Implementation of an NPN transistor. The emitter and base are pins, and the collector is an output.
 * If the emitter is lower than the base, then its output is delivered to the collector,
 * otherwise the collector is not driven.
 */
public class NPNTransistor extends Transistor {
    /**
     * Create the transistor as NPN type
     */
    public NPNTransistor() {
        super();
        this.arrow = npn_arrow;
        this.name = "NPN";
        this.tt = Logic.npn_tt;
    }
}
