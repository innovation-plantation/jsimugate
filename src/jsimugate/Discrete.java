package jsimugate;

import java.util.Scanner;

/**
 * Derive discrete parts from this class to avoid the ability to change the technology
 * to something like open-collector rather than push-pull. For push-pull technology,
 * the output is what the part tells it to be, without being automatically further weakened
 * by alternate tech.
 */
public class Discrete extends Part {
    String opposite = null;

    /**
     * override to prevent changing from default
     */
    public Discrete asTech(Tech tech) {
        this.tech = Tech.PUSH_PULL;
        return this;
    }

    public Part reversePolarity() {
        if (opposite == null) return this;
        String s = toString().replaceAll(this.getClass().getSimpleName(), opposite);

        Discrete that = (Discrete) Part.fromScanner(new Scanner(s), null);

        that.pins = pins;
        that.children = children;
        for (Symbol child : children) child.parent = that;
        return that;
    }
}
