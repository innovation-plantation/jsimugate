package jsimugate;

public class OutConnector extends Part {
	Pin pin;

	public OutConnector() {
		this(0, 0);
	}

	public OutConnector(double x, double y) {
		super(x, y);
		setShape(Artwork.outConnectorShape());
		addPin(pin = new Pin(-80, 0).left(40));
		this.name = "OUTPUT";
	}

	public void operate() {
		Signal value = pin.getInValue();
		label = "OUTPUT=" + value.getChar();
		this.color = value.fgColor;
	}
}
