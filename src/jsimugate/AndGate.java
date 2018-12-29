package jsimugate;

public class AndGate extends Gate {

	public AndGate() {
		this(0, 0);
	}

	public AndGate(double x, double y) {
		super(x, y);
		opposite = OrGate.class.getSimpleName();
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

	public void updateLabel() {
		label = output.inverted ? "NAND" : "AND";
	}
}
