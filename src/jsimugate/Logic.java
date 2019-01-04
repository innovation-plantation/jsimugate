package jsimugate;

/**
 * Truth tables for standard logic parts essentially using VHDL's std_logic levels.
 */

import static jsimugate.Signal.*;

public class Logic {
    /**
     * resolve contention on a wire
     */
    public static final Signal[][] resolve_tt = {
            // U _ X _ 0 _ 1 _ Z _ W _ L _ H _ -
            {_U, _U, _U, _U, _U, _U, _U, _U, _U}, // U
            {_U, _X, _X, _X, _X, _X, _X, _X, _X}, // X
            {_U, _X, _0, _X, _0, _0, _0, _0, _X}, // 0
            {_U, _X, _X, _1, _1, _1, _1, _1, _X}, // 1
            {_U, _X, _0, _1, _Z, _W, _L, _H, _X}, // Z
            {_U, _X, _0, _1, _W, _W, _W, _W, _X}, // W
            {_U, _X, _0, _1, _L, _W, _L, _W, _X}, // L
            {_U, _X, _0, _1, _H, _W, _W, _H, _X}, // H
            {_U, _X, _X, _X, _X, _X, _X, _X, _X} // -
    };
    /**
     * buffer
     */
    public static final Signal[] buf_tt =
            // U _ X _ 0 _ 1 _ Z _ W _ L _ H _ -
            {_U, _X, _0, _1, _X, _X, _0, _1, _X};
    /**
     * not-gate
     */
    public static final Signal[] not_tt =
            // U _ X _ 0 _ 1 _ Z _ W _ L _ H _ -
            {_U, _X, _1, _0, _X, _X, _1, _0, _X};
    /**
     * and-gate
     */
    public static final Signal[][] and_tt = {
            // U _ X _ 0 _ 1 _ Z _ W _ L _ H _ -
            {_U, _U, _0, _U, _U, _U, _0, _U, _U}, // U
            {_U, _X, _0, _X, _X, _X, _0, _X, _X}, // X
            {_0, _0, _0, _0, _0, _0, _0, _0, _0}, // 0
            {_U, _X, _0, _1, _X, _X, _0, _1, _X}, // 1
            {_U, _X, _0, _X, _X, _X, _0, _X, _X}, // Z
            {_U, _X, _0, _X, _X, _X, _0, _X, _X}, // W
            {_0, _0, _0, _0, _0, _0, _0, _0, _0}, // L
            {_U, _X, _0, _1, _X, _X, _0, _1, _X}, // H
            {_U, _X, _0, _X, _X, _X, _0, _X, _X} // -
    };
    /**
     * or-gate
     */
    public static final Signal[][] or_tt = {
            // U _ X _ 0 _ 1 _ Z _ W _ L _ H _ -
            {_U, _U, _U, _1, _U, _U, _U, _1, _U}, // U
            {_U, _X, _X, _1, _X, _X, _X, _1, _X}, // X
            {_U, _X, _0, _1, _X, _X, _0, _1, _X}, // 0
            {_1, _1, _1, _1, _1, _1, _1, _1, _1}, // 1
            {_U, _X, _X, _1, _X, _X, _X, _1, _X}, // Z
            {_U, _X, _X, _1, _X, _X, _X, _1, _X}, // W
            {_U, _X, _0, _1, _X, _X, _0, _1, _X}, // L
            {_1, _1, _1, _1, _1, _1, _1, _1, _1}, // H
            {_U, _X, _X, _1, _X, _X, _X, _1, _X} // -
    };
    /**
     * xor-gate
     */
    public static final Signal[][] xor_tt = {
            // U _ X _ 0 _ 1 _ Z _ W _ L _ H _ -
            {_U, _U, _U, _U, _U, _U, _U, _U, _U}, // U
            {_U, _X, _X, _X, _X, _X, _X, _X, _X}, // X
            {_U, _X, _0, _1, _X, _X, _0, _1, _X}, // 0
            {_U, _X, _1, _0, _X, _X, _1, _0, _X}, // 1
            {_U, _X, _X, _X, _X, _X, _X, _X, _X}, // Z
            {_U, _X, _X, _X, _X, _X, _X, _X, _X}, // W
            {_U, _X, _0, _1, _X, _X, _0, _1, _X}, // L
            {_U, _X, _1, _0, _X, _X, _1, _0, _X}, // H
            {_U, _X, _X, _X, _X, _X, _X, _X, _X} // -
    };
    /**
     * npn-transistor collector output for emitter and base pins
     */
    public static final Signal[][] npn_tt = {
            // U _ X _ 0 _ 1 _ Z _ W _ L _ H _ - =B // E:
            {_U, _U, _Z, _U, _Z, _U, _Z, _U, _U}, // U
            {_U, _X, _Z, _X, _Z, _X, _Z, _X, _X}, // X
            {_U, _X, _Z, _0, _Z, _X, _Z, _0, _X}, // 0
            {_U, _X, _Z, _Z, _Z, _X, _Z, _Z, _X}, // 1
            {_U, _X, _Z, _Z, _Z, _X, _Z, _Z, _X}, // Z
            {_U, _X, _Z, _W, _Z, _X, _Z, _W, _X}, // W
            {_U, _X, _Z, _L, _Z, _X, _Z, _L, _X}, // L
            {_U, _X, _Z, _Z, _Z, _X, _Z, _Z, _X}, // H
            {_U, _X, _Z, _X, _Z, _X, _Z, _X, _X} // -
    };

    /**
     * pnp-transistor collector output for emitter and base pins
     */
    public static final Signal[][] pnp_tt = {
            // U _ X _ 0 _ 1 _ Z _ W _ L _ H _ - =B // E:
            {_U, _U, _U, _Z, _Z, _U, _U, _Z, _U}, // U
            {_U, _X, _X, _Z, _Z, _X, _X, _Z, _X}, // X
            {_U, _X, _Z, _Z, _Z, _X, _Z, _Z, _X}, // 0
            {_U, _X, _1, _Z, _Z, _X, _1, _Z, _X}, // 1
            {_U, _X, _Z, _Z, _Z, _X, _Z, _Z, _X}, // Z
            {_U, _X, _W, _Z, _Z, _X, _W, _Z, _X}, // W
            {_U, _X, _Z, _Z, _Z, _X, _Z, _Z, _X}, // L
            {_U, _X, _H, _Z, _Z, _X, _H, _Z, _X}, // H
            {_U, _X, _X, _Z, _Z, _X, _X, _Z, _X}, // -
    };
    /**
     * anode output for anode and cathode pins on s fiofr
     */
    public static final Signal[][] anode_tt = { // outputs lower of two pins
            // U _ X _ 0 _ 1 _ Z _ W _ L _ H _ - =C // A:

            {_U, _U, _U, _0, _U, _U, _U, _U, _U}, // U
            {_U, _W, _X, _Z, _Z, _Z, _Z, _Z, _Z}, // X
            {_U, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z}, // 0
            {_0, _X, _X, _Z, _Z, _Z, _Z, _Z, _Z}, // 1
            {_U, _X, _0, _Z, _Z, _W, _L, _Z, _Z}, // Z
            {_U, _X, _0, _Z, _Z, _W, _W, _Z, _Z}, // W
            {_U, _X, _0, _Z, _Z, _Z, _Z, _Z, _Z}, // L
            {_0, _X, _0, _Z, _Z, _W, _W, _Z, _Z}, // H
            {_U, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z} // -

    };
    /**
     * cathode output for anode and cathode pins on s fiofr
     */
    public static final Signal[][] cathode_tt = { // outputs higher of two pins
            // U _ X _ 0 _ 1 _ Z _ W _ L _ H _ - =C // A:

            {_U, _U, _U, _0, _U, _U, _U, _U, _U}, // U
            {_U, _W, _X, _Z, _X, _X, _X, _X, _Z}, // X
            {_U, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z}, // 0
            {_0, _X, _X, _Z, _1, _1, _1, _1, _Z}, // 1
            {_U, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z}, // Z
            {_U, _Z, _Z, _Z, _W, _W, _W, _W, _Z}, // W
            {_U, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z}, // L
            {_0, _Z, _Z, _Z, _H, _W, _W, _Z, _Z}, // H
            {_U, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z} // -
    };
}
