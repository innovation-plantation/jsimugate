package jsimugate;

public class InConnector extends Part {

	Pin pin;
	Signal value=Signal._Z;

	public InConnector() {
		this(0, 0);
	}

	public InConnector(double x, double y) {
		super(x, y);
		setShape(Artwork.ConnectorShape());
		addPin(pin = new Pin(85, 0).right(30));
		this.name = "INPUT";
	}

	public void operate() {
		pin.setOutValue(value);
		label = "INPUT=" + value.getChar();
		this.color = value.fgColor;
	}

	public void processChar(char ch) {
		for (Signal s : Signal.values()) if (ch == s.getChar()) value = s;
	}
}
