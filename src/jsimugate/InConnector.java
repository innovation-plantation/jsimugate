package jsimugate;

public class InConnector extends Part {

	Pin pin;
	
	public InConnector(double x, double y) {
		super(x, y);
		setShape(Artwork.inConnectorShape());
		addPin(pin = new Pin(80, 0).right(40));
		this.name="INPUT";
	}
	
	public void operate() {
		Signal value = pin.getOutValue();
		label = "INPUT="+value.getChar();
		this.color = value.fgColor;
	}
	
	public void processChar(char ch) {
		for (Signal s :Signal.values()) if (ch==s.getChar()) pin.setOutValue(s);
	}
}
