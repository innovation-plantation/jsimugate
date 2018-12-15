package jsimugate;

import java.awt.Color;

public class Inversion extends Symbol {

	boolean inverted;
	public Inversion(double x, double y) {
		super(x, y);
		setShape(Artwork.bubbleShape());
		setVisible(false);
	}

	public void setVisible(boolean visible) {
		inverted = visible;
		if (visible) {
			setFill(Color.white);
			setColor(Color.black);
		} else {
			setFill(null);
			setColor(null);

		}
	}

}
