package jsimugate;

public class Debuggable {
	static boolean debug; // turns on System.out.print... for log... calls.
	
	static void logline() {
		if (debug) System.out.println();
	}
	static void logline(String s) {
		if (debug) System.out.print(s);
	}
	static void log(String s) {
		if (debug) System.out.print(s);
	}
	static void logf(String format,float f0,float f1,float f2) {
		if (debug) System.out.printf(format, f0,f1,f2);
	};
}
