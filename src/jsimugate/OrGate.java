package jsimugate;

public class OrGate extends Gate {
	public OrGate(double x, double y) {
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
	
	public void updateLabel() {
		label = output.inverted ? "NOR" : "OR";
	}
	
	public Part convert() {
		/**
		 * Pilfer resources from this for that as with move semantics. 
		 * 
		 * After return, caller is expected to use that instead of this, 
		 * setting whatever pointers used this to point to that instead. 
		 */
		
	    AndGate that = new AndGate(0, 0);
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
