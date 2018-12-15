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
			if (i.value.bad) {
				output.value = Signal._X;
				return;
			}
			if (i.value.hi) hi++;
			if (i.value.lo) lo++;
		}
		if (hi > lo) output.value = Signal._1;
		else if (lo > hi) output.value = Signal._0;
		else output.value = Signal._X;
	}
}
