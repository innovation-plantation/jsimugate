package jsimugate;

import java.util.ArrayList;
import java.util.List;

public class Gate extends Part {

	List<Pin> inputs = new ArrayList<Pin>();
	Pin output;

	public Gate(double x, double y) {
		super(x, y);
		reshape(0);
		addPin(output = new Pin(80, 0).right(40));
		addInput();
		addInput();
		setSelected(false);
	}
	
	protected void addInput() {
		int n = inputs.size();
		reshape(n + 1);
		switch (n) {
		case 0:
			inputs.add(addPin(new Pin(-80, 0).left(40)));
			break;
		case 1:
			inputs.get(0).transform.translate(0, -20);
			inputs.add(addPin(new Pin(-80, 20).left(40)));
			break;
		case 2:
			inputs.get(1).transform.translate(0, -20);
			inputs.add(addPin(new Pin(-80, 20).left(40)));
			break;
		default:
			for (Pin i : inputs) i.translate(0, -10);
			inputs.add(addPin(new Pin(-80, n * 10).left(40)));
		}
		updateLabel();
	}

	protected void removeInput() {
		int n = inputs.size();
		if (n < 1) return;
		Pin victim = inputs.get(n - 1);
		// if wires attached return now without removing victim.
		if (Net.directConnections(victim).size()>0) return;
		reshape(n - 1);
		inputs.remove(removePin(victim));
		switch (n) {
		case 1:
			break;
		case 2:
			inputs.get(0).transform.translate(0, 20);
			break;
		case 3:
			inputs.get(1).transform.translate(0, 20);
			break;
		default:
			for (Pin i : inputs) i.translate(0, 10);
		}
		updateLabel();
	}

	public Signal function() {
		return Signal._U;
	};

	public Signal function(Signal a, Signal b) {
		return Signal._U;
	}

	public void operate() {
		Signal result = function();
		for (Pin i : inputs) result = function(result, i.getInValue());
		output.setOutValue(result);
	}

	public void increase() {
		addInput();
	}

	public void decrease() {
		removeInput();
	}
	
	public Gate not() {
		output.toggleInversion();
		return this;
	}
}
