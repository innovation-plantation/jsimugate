package jsimugate;

import java.awt.*;

/**
 * Implementation of a PNP transistor. The emitter and base are inputs, and the collector is an output.
 * If the emitter is higher than the base, then its output is delivered to the collector,
 * otherwise the collector is not driven.
 */
public class PNPTransistor extends Transistor {
    /**
     * Create the transistor as PNP type
     */
    public PNPTransistor() {
        super();
        this.arrow = pnp_arrow;
        this.name = "PNP";
        this.tt = Logic.pnp_tt;
        this.color = Color.red;
    }
}
