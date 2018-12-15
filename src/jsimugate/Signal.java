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

		Gauge(Stroke fgStroke, Stroke bgStroke) {
			fg = fgStroke;
			bg = bgStroke;
		}
	}
};

public enum Signal implements SignalConstants {

	_U('U', new Color(0x00FFFF), Color.white, Gauge.THICK), _X('X', new Color(0xFFA000), Color.white, Gauge.THICK),
	_0('0', new Color(0x404040), Color.white, Gauge.THICK), _1('1', new Color(0xC00000), Color.white, Gauge.THICK),
	_Z('Z', new Color(0x8880FF), Color.white, Gauge.THIN), _W('W', new Color(0xFFa000), Color.white, Gauge.THIN),
	_L('L', new Color(0x404040), Color.white, Gauge.THIN), _H('H', new Color(0xc00000), Color.white, Gauge.THIN),
	_D('-', new Color(0x00FF00), Color.white, Gauge.THIN);

	private char value;
	private Color fgColor;
	private Color bgColor;
	private Stroke fgStroke;
	private Stroke bgStroke;
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
		if (bgColor != null) {
			g.setStroke(bgStroke);
			g.setColor(bgColor);
			g.draw(shape);
		}
		g.setStroke(fgStroke);
		g.setColor(fgColor);
		g.draw(shape);
	}

	public Signal resolve(Signal other) {
		return Logic.resolve_tt[ordinal()][other.ordinal()];
	}

	public static Signal resolve(Signal... signals) {
		Signal result = _Z;
		for (Signal signal : signals) result = result.resolve(signal);
		return result;
	}
}
