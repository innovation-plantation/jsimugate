package jsimugate;

public class Log {
	public static boolean debug; // turns on System.out.print... for log... calls.

	public static void println() {
		if (debug) System.out.println();
	}

	public static void println(String s) {
		if (debug) System.out.println(s);
	}

	public static void print(String s) {
		if (debug) System.out.print(s);
	}

	public static void printf(String format, float f0, float f1, float f2) {
		if (debug) System.out.printf(format, f0, f1, f2);
	};
}
