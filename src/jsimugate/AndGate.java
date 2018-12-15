package jsimugate;

public class AndGate extends Gate {

	public AndGate(double x, double y) {
		super(x, y);
	}

	public void reshape(int n) {
		setShape(Artwork.andShape(n));
	}

	public Signal function() {
		return Signal._1;
	};

	public Signal function(Signal a, Signal b) {
		return Logic.and_tt[a.ordinal()][b.ordinal()];
	}
}
