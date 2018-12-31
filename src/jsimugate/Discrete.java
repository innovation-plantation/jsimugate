package jsimugate;

public class Discrete extends Part {
	public Discrete() {
		this(0, 0);
	}

	public Discrete(double x, double y) {
		super(x, y);
	}

	/**
	 * override to prevent changing from default
	 */
	public Discrete asTech(Tech tech) {
		this.tech = Tech.PUSH_PULL;
		return this;
	}
}
