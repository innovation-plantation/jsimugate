package jsimugate;

public class XorGate extends Gate {
	public XorGate(double x, double y) {
		super(x, y);
	}

	public void reshape(int n) {
		setShape(Artwork.xorShape(n));
	}

	public Signal function() {
		return Signal._0;
	};

	public Signal function(Signal a, Signal b) {
		return Logic.xor_tt[a.ordinal()][b.ordinal()];
	}
}
