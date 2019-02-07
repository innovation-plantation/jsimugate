package jsimugate;

/**
 * Output stream that can be disabled by turning off debug.
 */
public class Log {
    public static boolean debug = true; // turns on System.out.print... for log... calls.

    /**
     * Print a blank line if debugging.
     */
    public static void println() {
        if (debug) System.out.println();
    }

    /**
     * Print a line containing a string if debugging.
     *
     * @param s
     */
    public static void println(String s) {
        if (debug) System.out.println(s);
    }

    /**
     * Print a string if debugging
     *
     * @param s
     */
    public static void print(String s) {
        if (debug) System.out.print(s);
    }

    /**
     * Perform formatted output with a format string and three floating point arguments if debugging.
     * This could probably be generalized but not worth it being used only in one place.
     *
     * @param format
     * @param f0
     * @param f1
     * @param f2
     */
    public static void printf(String format, float f0, float f1, float f2) {
        if (debug) System.out.printf(format, f0, f1, f2);
    }

}
