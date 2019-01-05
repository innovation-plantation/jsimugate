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
            // U _X _ 0 _ 1 _ Z _ W _ L _ H _ - _ M _ F _ T
            {_U, _U, _U, _U, _U, _U, _U, _U, _U, _U, _U, _U}, // U
            {_U, _X, _X, _X, _X, _X, _X, _X, _X, _X, _X, _X}, // X
            {_U, _X, _0, _X, _0, _0, _0, _0, _X, _0, _0, _0}, // 0
            {_U, _X, _X, _1, _1, _1, _1, _1, _X, _1, _1, _1}, // 1
            {_U, _X, _0, _1, _Z, _W, _L, _H, _X, _M, _N, _Y}, // Z
            {_U, _X, _0, _1, _W, _W, _W, _W, _X, _W, _W, _W}, // W
            {_U, _X, _0, _1, _L, _W, _L, _W, _X, _L, _L, _L}, // L
            {_U, _X, _0, _1, _H, _W, _W, _H, _X, _H, _H, _H}, // H
            {_U, _X, _X, _X, _X, _X, _X, _X, _X, _X, _X, _X}, // -
            {_U, _X, _0, _1, _M, _W, _L, _H, _X, _M, _M, _M}, // M
            {_U, _X, _0, _1, _N, _W, _L, _H, _X, _M, _N, _M}, // F
            {_U, _X, _0, _1, _Y, _W, _L, _H, _X, _M, _M, _Y}, // T
    };
// ** IF T/F/M were stronger than W, we'd use this table instead:
//    public static final Signal[][] resolve_tt = {
//            // U _X _ 0 _ 1 _ Z _ W _ L _ H _ - _ M _ F _ T
//            {_U, _U, _U, _U, _U, _U, _U, _U, _U, _U, _U, _U}, // U
//            {_U, _X, _X, _X, _X, _X, _X, _X, _X, _X, _X, _X}, // X
//            {_U, _X, _0, _X, _0, _0, _0, _0, _X, _0, _0, _0}, // 0
//            {_U, _X, _X, _1, _1, _1, _1, _1, _X, _1, _1, _1}, // 1
//            {_U, _X, _0, _1, _Z, _W, _L, _H, _X, _M, _N, _Y}, // Z
//            {_U, _X, _0, _1, _W, _W, _W, _W, _X, _M, _N, _Y}, // W
//            {_U, _X, _0, _1, _L, _W, _L, _W, _X, _M, _N, _Y}, // L
//            {_U, _X, _0, _1, _H, _W, _W, _H, _X, _M, _N, _Y}, // H
//            {_U, _X, _X, _X, _X, _X, _X, _X, _X, _X, _X, _X}, // -
//            {_U, _X, _0, _1, _M, _M, _M, _M, _X, _M, _M, _M}, // M
//            {_U, _X, _0, _1, _N, _N, _N, _N, _X, _M, _N, _M}, // F
//            {_U, _X, _0, _1, _Y, _Y, _Y, _Y, _X, _M, _M, _Y}, // T
//    };
    /**
     * buffer
     */
    public static final Signal[] buf_tt =
            // U _X _ 0 _ 1 _ Z _ W _ L _ H _ - _ M _ F _ T
            {_U, _X, _0, _1, _X, _X, _0, _1, _X, _X, _0, _1};
    /**
     * not-gate
     */
    public static final Signal[] not_tt =
            // U _X _ 0 _ 1 _ Z _ W _ L _ H _ - _ M _ F _ T
            {_U, _X, _1, _0, _X, _X, _1, _0, _X, _X, _1, _0};
    /**
     * and-gate
     */
    public static final Signal[][] and_tt = {
            // U _X _ 0 _ 1 _ Z _ W _ L _ H _ - _ M _ F _ T
            {_U, _U, _0, _U, _U, _U, _0, _U, _U, _U, _0, _U}, // U
            {_U, _X, _0, _X, _X, _X, _0, _X, _X, _X, _0, _X}, // X
            {_0, _0, _0, _0, _0, _0, _0, _0, _0, _0, _0, _0}, // 0
            {_U, _X, _0, _1, _X, _X, _0, _1, _X, _X, _0, _1}, // 1
            {_U, _X, _0, _X, _X, _X, _0, _X, _X, _X, _0, _X}, // Z
            {_U, _X, _0, _X, _X, _X, _0, _X, _X, _X, _0, _X}, // W
            {_0, _0, _0, _0, _0, _0, _0, _0, _0, _0, _0, _0}, // L
            {_U, _X, _0, _1, _X, _X, _0, _1, _X, _X, _0, _1}, // H
            {_U, _X, _0, _X, _X, _X, _0, _X, _X, _X, _0, _X}, // -
            {_U, _X, _0, _X, _X, _X, _0, _X, _X, _X, _0, _X}, // M
            {_0, _0, _0, _0, _0, _0, _0, _0, _0, _0, _0, _0}, // F
            {_U, _X, _0, _1, _X, _X, _0, _1, _X, _X, _0, _1}, // T
    };
    /**
     * or-gate
     */
    public static final Signal[][] or_tt = {
            // U _X _ 0 _ 1 _ Z _ W _ L _ H _ - _ M _ F _ T
            {_U, _U, _U, _1, _U, _U, _U, _1, _U, _U, _U, _1}, // U
            {_U, _X, _X, _1, _X, _X, _X, _1, _X, _X, _X, _1}, // X
            {_U, _X, _0, _1, _X, _X, _0, _1, _X, _X, _0, _1}, // 0
            {_1, _1, _1, _1, _1, _1, _1, _1, _1, _1, _1, _1}, // 1
            {_U, _X, _X, _1, _X, _X, _X, _1, _X, _X, _X, _1}, // Z
            {_U, _X, _X, _1, _X, _X, _X, _1, _X, _X, _X, _1}, // W
            {_U, _X, _0, _1, _X, _X, _0, _1, _X, _X, _0, _1}, // L
            {_1, _1, _1, _1, _1, _1, _1, _1, _1, _1, _1, _1}, // H
            {_U, _X, _X, _1, _X, _X, _X, _1, _X, _X, _X, _1}, // -
            {_U, _X, _X, _1, _X, _X, _X, _1, _X, _X, _X, _1}, // M
            {_U, _X, _0, _1, _X, _X, _0, _1, _X, _X, _0, _1}, // F
            {_1, _1, _1, _1, _1, _1, _1, _1, _1, _1, _1, _1}, // T
    };
    /**
     * xor-gate
     */
    public static final Signal[][] xor_tt = {
            // U _X _ 0 _ 1 _ Z _ W _ L _ H _ - _ M _ F _ T
            {_U, _U, _U, _U, _U, _U, _U, _U, _U, _U, _U, _U}, // U
            {_U, _X, _X, _X, _X, _X, _X, _X, _X, _X, _X, _X}, // X
            {_U, _X, _0, _1, _X, _X, _0, _1, _X, _X, _0, _1}, // 0
            {_U, _X, _1, _0, _X, _X, _1, _0, _X, _X, _1, _0}, // 1
            {_U, _X, _X, _X, _X, _X, _X, _X, _X, _X, _X, _X}, // Z
            {_U, _X, _X, _X, _X, _X, _X, _X, _X, _X, _X, _X}, // W
            {_U, _X, _0, _1, _X, _X, _0, _1, _X, _X, _0, _1}, // L
            {_U, _X, _1, _0, _X, _X, _1, _0, _X, _X, _1, _0}, // H
            {_U, _X, _X, _X, _X, _X, _X, _X, _X, _X, _X, _X}, // -
            {_U, _X, _X, _X, _X, _X, _X, _X, _X, _X, _X, _X}, // M
            {_U, _X, _0, _1, _X, _X, _0, _1, _X, _X, _0, _1}, // F
            {_U, _X, _1, _0, _X, _X, _1, _0, _X, _X, _1, _0}, // T
    };
    /**
     * npn-transistor collector output for emitter and base pins
     */
    public static final Signal[][] npn_tt = {
            // U _X _ 0 _ 1 _ Z _ W _ L _ H _ - _ M _ F _ T=B // E:
            {_U, _U, _Z, _U, _Z, _U, _Z, _U, _U, _U, _Z, _U}, // U
            {_U, _X, _Z, _X, _Z, _X, _Z, _X, _X, _X, _Z, _X}, // X
            {_U, _X, _Z, _0, _Z, _X, _Z, _0, _X, _X, _Z, _0}, // 0
            {_U, _X, _Z, _Z, _Z, _X, _Z, _Z, _X, _X, _Z, _Z}, // 1
            {_U, _X, _Z, _Z, _Z, _X, _Z, _Z, _X, _X, _Z, _Z}, // Z
            {_U, _X, _Z, _W, _Z, _X, _Z, _W, _X, _X, _Z, _W}, // W
            {_U, _X, _Z, _L, _Z, _X, _Z, _L, _X, _X, _Z, _L}, // L
            {_U, _X, _Z, _Z, _Z, _X, _Z, _Z, _X, _X, _Z, _Z}, // H
            {_U, _X, _Z, _X, _Z, _X, _Z, _X, _X, _X, _Z, _X}, // -
            {_U, _X, _Z, _M, _Z, _X, _Z, _M, _X, _X, _Z, _M}, // M
            {_U, _X, _Z, _N, _Z, _X, _Z, _N, _X, _X, _Z, _N}, // F
            {_U, _X, _Z, _Z, _Z, _X, _Z, _Z, _X, _X, _Z, _Z}, // T
    };

    /**
     * pnp-transistor collector output for emitter and base pins
     */
    public static final Signal[][] pnp_tt = {
            // U _X _ 0 _ 1 _ Z _ W _ L _ H _ - _ M _ F _ T=B // E:
            {_U, _U, _U, _Z, _Z, _U, _U, _Z, _U, _U, _U, _Z}, // U
            {_U, _X, _X, _Z, _Z, _X, _X, _Z, _X, _X, _X, _Z}, // X
            {_U, _X, _Z, _Z, _Z, _X, _Z, _Z, _X, _X, _Z, _Z}, // 0
            {_U, _X, _1, _Z, _Z, _X, _1, _Z, _X, _X, _1, _Z}, // 1
            {_U, _X, _Z, _Z, _Z, _X, _Z, _Z, _X, _X, _Z, _Z}, // Z
            {_U, _X, _W, _Z, _Z, _X, _W, _Z, _X, _X, _W, _Z}, // W
            {_U, _X, _Z, _Z, _Z, _X, _Z, _Z, _X, _X, _Z, _Z}, // L
            {_U, _X, _H, _Z, _Z, _X, _H, _Z, _X, _X, _H, _Z}, // H
            {_U, _X, _X, _Z, _Z, _X, _X, _Z, _X, _X, _X, _Z}, // -
            {_U, _X, _M, _Z, _Z, _X, _M, _Z, _X, _X, _M, _Z}, // M
            {_U, _X, _Z, _Z, _Z, _X, _Z, _Z, _X, _X, _Z, _Z}, // F
            {_U, _X, _Y, _Z, _Z, _X, _Y, _Z, _X, _X, _Y, _Z}, // T
    };
    /**
     * anode output for anode and cathode pins on a diode
     */
    //PROBLEM - UNSTABLE WITH X OR W OUTOUTS WHEN DIODES ARE IN PARALLEL
    public static final Signal[][] anode_tt = { // outputs lower of two pins
            // U _X _ 0 _ 1 _ Z _ W _ L _ H _ - _ M _ F _ T=C // A:
            {_U, _U, _U, _U, _U, _U, _U, _U, _U, _U, _U, _U}, // U
            {_U, _Z, _W, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z}, // X
            {_U, _Z, _0, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z}, // 0
            {_U, _Z, _X, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z}, // 1
            {_U, _Z, _0, _Z, _Z, _Z, _L, _Z, _Z, _Z, _Z, _Z}, // Z
            {_U, _Z, _0, _Z, _Z, _Z, _L, _Z, _Z, _Z, _Z, _Z}, // W
            {_U, _Z, _0, _Z, _Z, _Z, _L, _Z, _Z, _Z, _Z, _Z}, // L
            {_U, _Z, _0, _Z, _Z, _Z, _W, _Z, _Z, _Z, _Z, _Z}, // H
            {_U, _Z, _0, _Z, _Z, _Z, _L, _Z, _Z, _Z, _N, _Z}, // -
            {_U, _Z, _0, _Z, _Z, _Z, _L, _Z, _Z, _Z, _N, _Z}, // M
            {_U, _Z, _0, _Z, _Z, _Z, _L, _Z, _Z, _Z, _N, _Z}, // F
            {_U, _Z, _0, _Z, _Z, _Z, _L, _Z, _Z, _Z, _M, _Z}, // T

    };
    /**
     * cathode output for anode and cathode pins on a diode
     */
    //PROBLEM - UNSTABLE WITH X OR W OUTOUTS WHEN DIODES ARE IN PARALLEL
    public static final Signal[][] cathode_tt = { // outputs higher of two pins
            // U _X _ 0 _ 1 _ Z _ W _ L _ H _ - _ M _ F _ T=C // A:
            {_U, _U, _U, _U, _U, _U, _U, _U, _U, _U, _U, _U}, // U
            {_U, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z}, // X
            {_U, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z}, // 0
            {_U, _W, _X, _1, _1, _1, _1, _1, _1, _1, _1, _1}, // 1
            {_U, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z}, // Z
            {_U, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z}, // W
            {_U, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z}, // L
            {_U, _Z, _Z, _Z, _H, _H, _W, _H, _H, _H, _H, _H}, // H
            {_U, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z}, // -
            {_U, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z}, // M
            {_U, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z}, // F
            {_U, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Y, _Y, _M, _Y}, // T
    };
}
