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
            // U _X _ 0 _ 1 _ Z _ W _ L _ H _ - _ M _ N _ Y
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
            {_U, _X, _0, _1, _N, _W, _L, _H, _X, _M, _N, _M}, // N
            {_U, _X, _0, _1, _Y, _W, _L, _H, _X, _M, _M, _Y}, // Y
    };

    /**
     * buffer
     */
    public static final Signal[] buf_tt =
            // U _X _ 0 _ 1 _ Z _ W _ L _ H _ - _ M _ N _ Y
            {_U, _X, _0, _1, _X, _X, _0, _1, _X, _X, _0, _1};
    /**
     * not-gate
     */
    public static final Signal[] not_tt =
            // U _X _ 0 _ 1 _ Z _ W _ L _ H _ - _ M _ N _ Y
            {_U, _X, _1, _0, _X, _X, _1, _0, _X, _X, _1, _0};
    /**
     * and-gate
     */
    public static final Signal[][] and_tt = {
            // U _X _ 0 _ 1 _ Z _ W _ L _ H _ - _ M _ N _ Y
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
            {_0, _0, _0, _0, _0, _0, _0, _0, _0, _0, _0, _0}, // N
            {_U, _X, _0, _1, _X, _X, _0, _1, _X, _X, _0, _1}, // Y
    };
    /**
     * or-gate
     */
    public static final Signal[][] or_tt = {
            // U _X _ 0 _ 1 _ Z _ W _ L _ H _ - _ M _ N _ Y
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
            {_U, _X, _0, _1, _X, _X, _0, _1, _X, _X, _0, _1}, // N
            {_1, _1, _1, _1, _1, _1, _1, _1, _1, _1, _1, _1}, // Y
    };
    /**
     * xor-gate
     */
    public static final Signal[][] xor_tt = {
            // U _X _ 0 _ 1 _ Z _ W _ L _ H _ - _ M _ N _ Y
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
            {_U, _X, _0, _1, _X, _X, _0, _1, _X, _X, _0, _1}, // N
            {_U, _X, _1, _0, _X, _X, _1, _0, _X, _X, _1, _0}, // Y
    };
    /**
     * npn-transistor collector output for emitter and base pins
     */
    public static final Signal[][] npn_tt = {
            // U _X _ 0 _ 1 _ Z _ W _ L _ H _ - _ M _ N _ Y=B // E:
            {_U, _U, _Z, _U, _U, _U, _Z, _U, _U, _U, _Z, _U}, // U
            {_U, _X, _Z, _X, _X, _X, _Z, _X, _X, _X, _Z, _X}, // X
            {_U, _X, _Z, _0, _0, _X, _Z, _0, _X, _X, _Z, _0}, // 0
            {_Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z}, // 1
            {_Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z}, // Z
            {_U, _X, _Z, _W, _W, _X, _Z, _W, _X, _X, _Z, _W}, // W
            {_U, _X, _Z, _L, _L, _X, _Z, _L, _X, _X, _Z, _L}, // L
            {_Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z}, // H
            {_U, _X, _Z, _X, _X, _X, _Z, _X, _X, _X, _Z, _X}, // -
            {_U, _X, _Z, _M, _M, _X, _Z, _M, _X, _X, _Z, _M}, // M
            {_U, _X, _Z, _N, _N, _X, _Z, _N, _X, _X, _Z, _N}, // N
            {_Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z}, // Y
    };
 
    /**
     * pnp-transistor collector output for emitter and base pins
     */
    public static final Signal[][] pnp_tt = {
            // U _X _ 0 _ 1 _ Z _ W _ L _ H _ - _ M _ N _ Y=B // E:
            {_U, _U, _U, _Z, _U, _U, _U, _Z, _U, _U, _U, _Z}, // U
            {_U, _X, _X, _Z, _X, _X, _X, _Z, _X, _X, _X, _Z}, // X
            {_Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z}, // 0
            {_U, _X, _1, _Z, _1, _X, _1, _Z, _X, _X, _1, _Z}, // 1
            {_Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z}, // Z
            {_U, _X, _W, _Z, _W, _X, _W, _Z, _X, _X, _W, _Z}, // W
            {_Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z}, // L
            {_U, _X, _H, _Z, _H, _X, _H, _Z, _X, _X, _H, _Z}, // H
            {_U, _X, _X, _Z, _X, _X, _X, _Z, _X, _X, _X, _Z}, // -
            {_U, _X, _M, _Z, _M, _X, _M, _Z, _X, _X, _M, _Z}, // M
            {_Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z}, // N
            {_U, _X, _Y, _Z, _Y, _X, _Y, _Z, _X, _X, _Y, _Z}, // Y
    };

    /*
     * Combine emitters on NPN MET  XWM > Z
     * priority: 0>L>N > X>W>M >Z>D> Y>H>1 >U  
     */
    public static final Signal[][] met_npn_tt = { // COMBINE EMITTERS
            // U _X _ 0 _ 1 _ Z _ W _ L _ H _ - _ M _ N _ Y=E // E:
            {_U, _X, _0, _1, _Z, _W, _L, _H, _D, _M, _N, _Y}, // U
            {_X, _X, _0, _X, _X, _X, _L, _X, _X, _X, _N, _X}, // X 
            {_0, _0, _0, _0, _0, _0, _0, _0, _0, _0, _0, _0}, // 0
            {_1, _X, _0, _1, _Z, _W, _L, _H, _D, _M, _N, _Y}, // 1
            {_Z, _X, _0, _Z, _Z, _W, _L, _Z, _Z, _M, _N, _Z}, // Z
            {_W, _X, _0, _W, _W, _W, _L, _W, _W, _W, _N, _W}, // W
            {_L, _L, _0, _L, _L, _L, _L, _L, _L, _L, _L, _L}, // L
            {_H, _X, _0, _H, _Z, _W, _L, _H, _D, _M, _N, _Y}, // H
            {_D, _X, _0, _D, _Z, _W, _L, _D, _D, _M, _N, _D}, // -
            {_M, _X, _0, _M, _M, _W, _L, _M, _M, _M, _N, _M}, // M
            {_N, _N, _0, _N, _N, _N, _L, _N, _N, _N, _N, _N}, // N
            {_Y, _X, _0, _Y, _Z, _W, _L, _Y, _D, _M, _N, _Y}, // Y
    };
    
    /*
     * Combine emitters on PNP MET
     * priority: 1>H>Y > X>W>M >Z>D> N>L>0 >U 
     */
    public static final Signal[][] met_pnp_tt = { // COMBINE EMITTERS
            // U _X _ 0 _ 1 _ Z _ W _ L _ H _ - _ M _ N _ Y=E // E:
            {_U, _X, _0, _1, _Z, _W, _L, _H, _D, _M, _N, _Y}, // U
            {_X, _X, _X, _1, _X, _X, _X, _H, _X, _X, _X, _Y}, // X 
            {_0, _X, _0, _1, _Z, _W, _L, _H, _D, _M, _N, _Y}, // 0
            {_1, _1, _1, _1, _1, _1, _1, _1, _1, _1, _1, _1}, // 1
            {_Z, _X, _Z, _1, _Z, _W, _Z, _H, _Z, _M, _Z, _Y}, // Z
            {_W, _X, _W, _1, _W, _W, _W, _H, _W, _W, _W, _Y}, // W
            {_L, _X, _L, _1, _Z, _W, _L, _H, _D, _M, _N, _Y}, // L
            {_H, _H, _H, _1, _H, _H, _H, _H, _H, _H, _H, _H}, // H
            {_D, _X, _D, _1, _Z, _W, _D, _H, _D, _X, _D, _Y}, // -
            {_M, _X, _M, _1, _M, _W, _M, _H, _X, _M, _M, _Y}, // M
            {_N, _X, _N, _1, _Z, _W, _N, _H, _D, _M, _N, _Y}, // N
            {_Y, _Y, _Y, _1, _Y, _Y, _Y, _H, _Y, _Y, _Y, _Y}, // Y
    };
    
    /**
     * anode output for anode and cathode pins on a diode
     */
    //PROBLEM - UNSTABLE WITH X OR W OUTOUTS WHEN DIODES ARE IN PARALLEL
    public static final Signal[][] anode_tt = { // outputs lower of two pins
            // U _X _ 0 _ 1 _ Z _ W _ L _ H _ - _ M _ N _ Y=C // A:
            {_Z, _Z, _U, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z}, // U
            {_Z, _Z, _W, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z}, // X
            {_Z, _Z, _0, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z}, // 0
            {_Z, _Z, _X, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z}, // 1
            {_Z, _Z, _0, _Z, _Z, _Z, _L, _Z, _Z, _Z, _Z, _Z}, // Z
            {_Z, _Z, _0, _Z, _Z, _Z, _L, _Z, _Z, _Z, _Z, _Z}, // W
            {_Z, _Z, _0, _Z, _Z, _Z, _L, _Z, _Z, _Z, _Z, _Z}, // L
            {_Z, _Z, _0, _Z, _Z, _Z, _W, _Z, _Z, _Z, _Z, _Z}, // H
            {_Z, _Z, _0, _Z, _Z, _Z, _L, _Z, _Z, _Z, _N, _Z}, // -
            {_Z, _Z, _0, _Z, _Z, _Z, _L, _Z, _Z, _Z, _N, _Z}, // M
            {_Z, _Z, _0, _Z, _Z, _Z, _L, _Z, _Z, _Z, _N, _Z}, // N
            {_Z, _Z, _0, _Z, _Z, _Z, _L, _Z, _Z, _Z, _M, _Z}, // Y

    };
    /**
     * cathode output for anode and cathode pins on a diode
     */
    //PROBLEM - UNSTABLE WITH X OR W OUTOUTS WHEN DIODES ARE IN PARALLEL
    public static final Signal[][] cathode_tt = { // outputs higher of two pins
            // U _X _ 0 _ 1 _ Z _ W _ L _ H _ - _ M _ N _ Y=C // A:
            {_Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z}, // U
            {_Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z}, // X
            {_Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z}, // 0
            {_U, _W, _X, _1, _1, _1, _1, _1, _1, _1, _1, _1}, // 1
            {_Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z}, // Z
            {_Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z}, // W
            {_Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z}, // L
            {_Z, _Z, _Z, _Z, _H, _H, _W, _H, _H, _H, _H, _H}, // H
            {_Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z}, // -
            {_Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z}, // M
            {_Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z}, // N
            {_Z, _Z, _Z, _Z, _Z, _Z, _Z, _Z, _Y, _Y, _M, _Y}, // Y
    };
}
