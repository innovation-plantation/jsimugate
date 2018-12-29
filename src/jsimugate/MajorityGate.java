package jsimugate;

public class MajorityGate extends Gate {

	public MajorityGate() {
		this(0, 0);
	}

	public MajorityGate(double x, double y) {
		super(x, y);
		removeInput();
	}

	public void reshape(int n) {
		setShape(Artwork.majorityShape(n));
	}

	public void operate() {
		int hi = 0, lo = 0;
		for (Pin i : inputs) {
			if (i.getInValue().bad) {
				output.setOutValue(Signal._X);
				return;
			}
			if (i.getInValue().hi) hi++;
			if (i.getInValue().lo) lo++;
		}
		if (hi > lo) output.setOutValue(Signal._1);
		else if (lo > hi) output.setOutValue(Signal._0);
		else output.setOutValue(Signal._X);
	}

	public void increase() {
		addInput();
		if (inputs.size() % 2 == 0) addInput();
	}

	public void decrease() {
		if (inputs.size() > 1) removeInput();
		if (inputs.size() % 2 == 0) removeInput();
	}

	public void updateLabel() {
		label = inputs.size() > 1 ? "MAJ" + inputs.size() : output.inverted ? "NOT" : "BUFFER";
	}

	public Part convert() {
		for (Pin pin : pins) pin.toggleInversion();
		return this;
	}
}
