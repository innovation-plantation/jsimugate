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
	
	public void updateLabel() {
		label = output.inverted ? "NAND" : "AND";
	}

	public Part convert() {
		/**
		 * Pilfer resources from this for that as with move semantics. 
		 * 
		 * After return, caller is expected to use that instead of this, 
		 * setting whatever pointers used this to point to that instead. 
		 */
		
	    OrGate that = new OrGate(0, 0);
	    that.inputs = inputs;
	    that.output = output;
	    that.pins = pins;
	    that.transform = transform;
	    that.children = children;
		for (Symbol child : children) child.parent = that;
		for (Pin pin:pins) pin.toggleInversion();
		return that;
	}
}
