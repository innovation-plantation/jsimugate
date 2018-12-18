package jsimugate;

public class MajorityGate extends Gate {
	public MajorityGate(double x, double y) {
		super(x, y);
	}

	public void reshape(int n) {
		setShape(Artwork.majorityShape(n));
	}

	public void operate() {
		int hi = 0, lo = 0;
		for (Pin i : inputs) {
			if (i.in_value.bad) {
				output.setOutValue(Signal._X);
				return;
			}
			if (i.in_value.hi) hi++;
			if (i.in_value.lo) lo++;
		}
		if (hi > lo) output.setOutValue(Signal._1);
		else if (lo > hi) output.setOutValue(Signal._0);
		else output.setOutValue(Signal._X);
	}
}
