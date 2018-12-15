package jsimugate;

public class OrGate extends Gate {
	public OrGate(float x, float y) {
		super(x, y);
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
}
