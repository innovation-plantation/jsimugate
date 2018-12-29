package jsimugate;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;

interface SignalConstants {
	enum Gauge {
		THICK(new BasicStroke(7), new BasicStroke(9)), THIN(new BasicStroke(3), new BasicStroke(5));
		Stroke fg, bg;
		public static final Color defaultInsulationColor = new Color(0xFF, 0xFF, 0xFF, 0xC0);

		Gauge(Stroke fgStroke, Stroke bgStroke) {
			fg = fgStroke;
			bg = bgStroke;
		}
	}
};

public enum Signal implements SignalConstants {
	_U('U', new Color(0x00, 0xFF, 0xFF), Gauge.defaultInsulationColor, Gauge.THICK),
	_X('X', new Color(0xFF, 0xA0, 0x00), Gauge.defaultInsulationColor, Gauge.THICK),
	_0('0', new Color(0x40, 0x40, 0x40), Gauge.defaultInsulationColor, Gauge.THICK),
	_1('1', new Color(0xC0, 0x00, 0x00), Gauge.defaultInsulationColor, Gauge.THICK),
	_Z('Z', new Color(0x88, 0x80, 0xFF), Gauge.defaultInsulationColor, Gauge.THIN),
	_W('W', new Color(0xFF, 0xa0, 0x00), Gauge.defaultInsulationColor, Gauge.THIN),
	_L('L', new Color(0x40, 0x40, 0x40), Gauge.defaultInsulationColor, Gauge.THIN),
	_H('H', new Color(0xC0, 0x00, 0x00), Gauge.defaultInsulationColor, Gauge.THIN),
	_D('-', new Color(0x00, 0xFF, 0x00), Gauge.defaultInsulationColor, Gauge.THIN);

	public char value;
	Color fgColor;
	Color bgColor;
	Stroke fgStroke;
	Stroke bgStroke;
	final boolean hi, lo, good, bad;

	Signal(char c, Color fgColor, Color bgColor, Gauge gauge) {
		this.value = c;
		this.fgColor = fgColor;
		this.bgColor = bgColor;
		this.fgStroke = gauge.fg;
		this.bgStroke = gauge.bg;
		this.hi = value == '1' || value == 'H';
		this.lo = value == '0' || value == 'L';
		this.good = hi || lo;
		this.bad = !good;
	}

	void trace(Graphics2D g, Shape shape) {
		Stroke restore = g.getStroke();
		if (bgColor != null) {
			g.setStroke(bgStroke);
			g.setColor(bgColor);
			g.draw(shape);
		}
		g.setStroke(fgStroke);
		g.setColor(fgColor);
		g.draw(shape);
		g.setStroke(restore);
	}

	public Signal resolve(Signal other) {
		return Logic.resolve_tt[ordinal()][other.ordinal()];
	}

	public static Signal resolve(Signal... signals) {
		Signal result = _Z;
		for (Signal signal : signals) result = result.resolve(signal);
		return result;
	}

	public Signal not() {
		return Logic.not_tt[ordinal()];
	}

	public char getChar() {
		return value;
	}

}
