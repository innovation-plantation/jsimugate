package jsimugate;

public class OutConnector extends Part {
	Pin pin;

	public OutConnector() {
		this(0, 0);
	}

	public OutConnector(double x, double y) {
		super(x, y);
		setShape(Artwork.ConnectorShape());
		addPin(pin = new Pin(-60, 0).left(30));
		this.name = "OUTPUT";
	}

	public void operate() {
		Signal value = pin.getInValue();
		label = "OUTPUT=" + value.getChar();
		this.color = value.fgColor;
	}
}
