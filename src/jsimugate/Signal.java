package jsimugate;

import java.awt.*;

/**
 * Values of THICK and THIN strokes
 * Wires could also use different insulation color based on their current values.
 */
interface SignalConstants {
    enum Gauge {
        THICK(new BasicStroke(7), new BasicStroke(9)),  // thick wire for strong signals
        MEDIUM(new BasicStroke(5), new BasicStroke(7)),  // medium wire for medium strength signals
        THIN(new BasicStroke(3), new BasicStroke(5)),   // thin wire for weak signals
        FINE(new BasicStroke(1), new BasicStroke(3));   // thin wire for weak signals
        Stroke fg, bg;
        public static final Color defaultInsulationColor = new Color(0xFF, 0xFF, 0xFF, 0xC0);
        // other insulation colors could be defined here and used for various signals

        Gauge(Stroke fgStroke, Stroke bgStroke) {
            fg = fgStroke;
            bg = bgStroke;
        }
    }
}

/**
 * Enumerated signal values patterned after VHDL std_logic values, but extensible,
 * for example, to have weaker values as well in order to implement PLA logic circuits.
 * <p>
 * Names begin with underscore because values include '0' and '1'.
 */
public enum Signal implements SignalConstants {
    _U('U', new Color(0x00, 0xFF, 0xFF), Gauge.defaultInsulationColor, Gauge.THICK),
    _X('X', new Color(0xFF, 0xA0, 0x00), Gauge.defaultInsulationColor, Gauge.THICK),
    _0('0', new Color(0x40, 0x40, 0x40), Gauge.defaultInsulationColor, Gauge.THICK),
    _1('1', new Color(0xC0, 0x00, 0x00), Gauge.defaultInsulationColor, Gauge.THICK),
    _Z('Z', new Color(0x88, 0x80, 0xFF), Gauge.defaultInsulationColor, Gauge.THIN),
    _W('W', new Color(0xFF, 0xa0, 0x00), Gauge.defaultInsulationColor, Gauge.THIN),
    _L('L', new Color(0x40, 0x40, 0x40), Gauge.defaultInsulationColor, Gauge.THIN),
    _H('H', new Color(0xC0, 0x00, 0x00), Gauge.defaultInsulationColor, Gauge.THIN),
    _D('-', new Color(0x00, 0xFF, 0x00), Gauge.defaultInsulationColor, Gauge.THIN),
    // non-standard beyond this point
    // yeah, nope, maybe are weaker than weak
    _M('M', new Color(0xFF, 0xa0, 0x00), Gauge.defaultInsulationColor, Gauge.FINE),
    _N('N', new Color(0x40, 0x40, 0x40), Gauge.defaultInsulationColor, Gauge.FINE),
    _Y('Y', new Color(0xC0, 0x00, 0x00), Gauge.defaultInsulationColor, Gauge.FINE),
    ; // could add T F P (true, false, possibly) with Gauge MEDUIM if logic between X and W were needed.

    public char value;
    Color fgColor;
    Color bgColor;
    Stroke fgStroke;
    Stroke bgStroke;
    final boolean hi, lo, good, bad;
    int bitValue;

    /**
     * Selection of values for the signal
     *
     * @param c       The signal's character code
     * @param fgColor the foreground color for drawing wires with this signal
     * @param bgColor the background color for drawing wires with this signal
     * @param gauge   they style of background(outline) of the wire
     */
    Signal(char c, Color fgColor, Color bgColor, Gauge gauge) {
        this.value = c;
        this.fgColor = fgColor;
        this.bgColor = bgColor;
        this.fgStroke = gauge.fg;
        this.bgStroke = gauge.bg;
        this.hi = value == '1' || value == 'H' || value == 'T' || value == 'Y'; // Y
        this.lo = value == '0' || value == 'L' || value == 'F' || value == 'N'; // N
        this.good = hi || lo;
        this.bad = !good;
    }

    /**
     * Draw the shape on the graphics context using the appearance (line style and colors) of
     * wires according to their values.
     *
     * @param g     graphics context
     * @param shape the path to follow drawing the wire.
     */
    void trace(Graphics2D g, Shape shape) {
        Stroke restore = g.getStroke();
        if (bgColor != null) {
            g.setStroke(bgStroke);
            g.setColor(bgColor);
            g.draw(shape);
        }
        g.setStroke(fgStroke);
        g.setColor(fgColor);
        g.draw(shape);
        g.setStroke(restore);
    }

    /**
     * Resolve contention. When two points are connected by wires,
     * use this function to decide the signal value of the wire.
     *
     * @param other the other signal
     * @return the resulting arbitrated signal
     */
    public Signal resolve(Signal other) {
        return Logic.resolve_tt[ordinal()][other.ordinal()];
    }

    /**
     * Resolve contention among multiple signals.
     *
     * @param signals
     * @return
     */
    public static Signal resolve(Signal... signals) {
        Signal result = _Z;
        for (Signal signal : signals) result = result.resolve(signal);
        return result;
    }

    /**
     * The inverted value of the signal.
     *
     * @return
     */
    public Signal not() {
        return Logic.not_tt[ordinal()];
    }

    /**
     * The character that matches this signal, 'L','H','0','1',etc.
     *
     * @return
     */
    public char getChar() {
        return value;
    }

    /**
     * Convert the value to an integer bit value zero or one.
     *
     * @return 1 if it's a high signal, otherwise zero.
     */
    public int asBit() {
        return this.hi ? 1 : 0;
    }

    /**
     * Convert the integer zero or one to a signal.
     *
     * @param bit the signal
     * @return _0 or _1, depending on the value being nonzero.
     */
    public static Signal fromBit(int bit) {
        return bit == 0 ? _0 : _1;
    }

    /**
     * returns the signal with opposite polarity, if applicable.
     * @return the signal with opposite polarity, if applicable, otherwise self.
     */
    public Signal opposite() {
        switch (this) {
            case _1: return _0;
            case _0: return _1;
            case _H: return _L;
            case _L: return _H;
            case _Y: return _N;
            case _N: return _Y;
        }
        return this;
    }
}
