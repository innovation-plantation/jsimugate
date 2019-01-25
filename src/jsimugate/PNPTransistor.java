package jsimugate;

import java.awt.*;
import java.util.Scanner;

/**
 * Implementation of a PNP transistor. The emitter and base are pins, and the collector is an output.
 * If the emitter is higher than the base, then its output is delivered to the collector,
 * otherwise the collector is not driven.
 */
public class PNPTransistor extends Transistor {
    /**
     * Create the transistor as PNP type
     */
    public PNPTransistor() {
        super();
        arrow = pnp_arrow;
        name = "PNP";
        tt = Logic.pnp_tt;
        color = Color.red;
        opposite = NPNTransistor.class.getSimpleName();
    }

}
