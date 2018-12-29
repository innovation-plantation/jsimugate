package jsimugate;

public class OutConnector extends Part {
	Pin pin;
	
	public OutConnector(double x, double y) {
		super(x, y);
		setShape(Artwork.outConnectorShape());
		addPin(pin = new Pin(-80, 0).left(40));
	}

	public void operate() {
		Signal value = pin.getInValue();
		label = Character.toString(value.getChar());
		this.color = value.fgColor;
	}
}
