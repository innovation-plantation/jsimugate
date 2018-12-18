package jsimugate;

import static jsimugate.Signal.*;

public class Logic {
	public static final Signal[][] resolve_tt = {
			// U _ X _ 0 _ 1 _ Z _ W _ L _ H _ -
			{ _U, _U, _U, _U, _U, _U, _U, _U, _U }, // U
			{ _U, _X, _X, _X, _X, _X, _X, _X, _X }, // X
			{ _U, _X, _0, _X, _0, _0, _0, _0, _X }, // 0
			{ _U, _X, _X, _1, _1, _1, _1, _1, _X }, // 1
			{ _U, _X, _0, _1, _Z, _W, _L, _H, _X }, // Z
			{ _U, _X, _0, _1, _W, _W, _W, _W, _X }, // W
			{ _U, _X, _0, _1, _L, _W, _L, _W, _X }, // L
			{ _U, _X, _0, _1, _H, _W, _W, _H, _X }, // H
			{ _U, _X, _X, _X, _X, _X, _X, _X, _X } // -
	};
	public static final Signal buf_tt[] =
			// U _ X _ 0 _ 1 _ Z _ W _ L _ H _ -
			{ _U, _X, _0, _1, _X, _X, _0, _1, _X };
	public static final Signal not_tt[] =
			// U _ X _ 0 _ 1 _ Z _ W _ L _ H _ -
			{ _U, _X, _1, _0, _X, _X, _1, _0, _X };

	public static final Signal and_tt[][] = {
			// U _ X _ 0 _ 1 _ Z _ W _ L _ H _ -
			{ _U, _U, _0, _U, _U, _U, _0, _U, _U }, // U
			{ _U, _X, _0, _X, _X, _X, _0, _X, _X }, // X
			{ _0, _0, _0, _0, _0, _0, _0, _0, _0 }, // 0
			{ _U, _X, _0, _1, _X, _X, _0, _1, _X }, // 1
			{ _U, _X, _0, _X, _X, _X, _0, _X, _X }, // Z
			{ _U, _X, _0, _X, _X, _X, _0, _X, _X }, // W
			{ _0, _0, _0, _0, _0, _0, _0, _0, _0 }, // L
			{ _U, _X, _0, _1, _X, _X, _0, _1, _X }, // H
			{ _U, _X, _0, _X, _X, _X, _0, _X, _X } // -
	};

	public static final Signal or_tt[][] = {
			// U _ X _ 0 _ 1 _ Z _ W _ L _ H _ -
			{ _U, _U, _U, _1, _U, _U, _U, _1, _U }, // U
			{ _U, _X, _X, _1, _X, _X, _X, _1, _X }, // X
			{ _U, _X, _0, _1, _X, _X, _0, _1, _X }, // 0
			{ _1, _1, _1, _1, _1, _1, _1, _1, _1 }, // 1
			{ _U, _X, _X, _1, _X, _X, _X, _1, _X }, // Z
			{ _U, _X, _X, _1, _X, _X, _X, _1, _X }, // W
			{ _U, _X, _0, _1, _X, _X, _0, _1, _X }, // L
			{ _1, _1, _1, _1, _1, _1, _1, _1, _1 }, // H
			{ _U, _X, _X, _1, _X, _X, _X, _1, _X } // -
	};

	public static final Signal xor_tt[][] = {
			// U _ X _ 0 _ 1 _ Z _ W _ L _ H _ -
			{ _U, _U, _U, _U, _U, _U, _U, _U, _U }, // U
			{ _U, _X, _X, _X, _X, _X, _X, _X, _X }, // X
			{ _U, _X, _0, _1, _X, _X, _0, _1, _X }, // 0
			{ _U, _X, _1, _0, _X, _X, _1, _0, _X }, // 1
			{ _U, _X, _X, _X, _X, _X, _X, _X, _X }, // Z
			{ _U, _X, _X, _X, _X, _X, _X, _X, _X }, // W
			{ _U, _X, _0, _1, _X, _X, _0, _1, _X }, // L
			{ _U, _X, _1, _0, _X, _X, _1, _0, _X }, // H
			{ _U, _X, _X, _X, _X, _X, _X, _X, _X } // -
	};
}
