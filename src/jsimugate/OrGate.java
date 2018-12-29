package jsimugate;

public class OrGate extends Gate {
	public OrGate() {
		this(0, 0);
	}

	public OrGate(double x, double y) {
		super(x, y);
		opposite = AndGate.class.getSimpleName();
	}

	public void reshape(int n) {
		setShape(Artwork.orShape(n));
	}

	public Signal function() {
		return Signal._0;
	};

	public Signal function(Signal a, Signal b) {
		return Logic.or_tt[a.ordinal()][b.ordinal()];
	}

	public void updateLabel() {
		label = output.inverted ? "NOR" : "OR";
	}
}
