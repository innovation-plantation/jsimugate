package jsimugate;

/**
 * Derive discrete parts from this class to avoid the ability to change the technology
 * to something like open-collector rather than push-pull. For push-pull technology,
 * the output is what the part tells it to be, without being automatically further weakened
 * by alternate tech.
 */
public class Discrete extends Part {
    /**
     * override to prevent changing from default
     */
    public Discrete asTech(Tech tech) {
        this.tech = Tech.PUSH_PULL;
        return this;
    }
}
