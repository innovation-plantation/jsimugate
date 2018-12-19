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
	public void updateLabel() {
		label = output.inverted ? "XNOR" : "XOR";
	}
	public Part convert() {
		for (Pin pin:inputs) {
			if (pin.inverted) {
				pin.toggleInversion();
				output.toggleInversion();
			}
		}
		return this;
	}
}
