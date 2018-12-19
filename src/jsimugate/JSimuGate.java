package jsimugate;

import java.applet.Applet;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jsimugate.Part.Tech;

public class JSimuGate extends Applet implements MouseListener, MouseMotionListener, KeyListener, ComponentListener {
	private static final long serialVersionUID = 1L;
	List<Part> parts = new ArrayList<Part>();
	ArrayList<Wire> wires = new ArrayList<Wire>();
	private Dimension size;
	private Image image;
	private Graphics graphics;
	private static final Stroke lassoStroke = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,
			new float[] { 9, 2 }, 0);
	MouseEvent recentMouseEvent = null;
	private Wire protoWire = null;
	private static Point2D.Double lassoBegin = null;
	private static Rectangle2D.Double lasso = null;

	public void init() {
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addKeyListener(this);
		this.addComponentListener(this);

		parts.add(new MajorityGate(100, 50).not());
		parts.add(new AndGate(100, 150));
		parts.add(new OrGate(100, 250));
		parts.add(new XorGate(100, 350));
		updateImageSize();
	}

	private void updateImageSize() {
		size = getSize();
		image = createImage(size.width, size.height);
		if (image != null) graphics = image.getGraphics();
	}

	public void update(Graphics g) {
		graphics.clearRect(0, 0, size.width, size.height);
		paint(graphics);
		g.drawImage(image, 0, 0, this);
	}

	public void paint(Graphics g1D) {
		Graphics2D g = (Graphics2D) g1D;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Net.operateAll();

		for (Part part : parts) {
			part.operate();
			part.draw(g);
			for (Pin pin : part.pins) {
				pin.setInValue(Signal._Z);
			}
		}

		for (Wire wire : wires) wire.draw(g);

		if (lasso != null) {
			g.setColor(Color.blue);
			g.setStroke(lassoStroke);
			g.draw(lasso);
		}

		if (protoWire != null) {
			protoWire.value = protoWire.src.getOutValue();
			protoWire.draw(g);
		}
	}

	public static void main(String[] args) throws InstantiationException, IllegalAccessException, SecurityException {
		Applet panel = new JSimuGate();
		Frame frame = new Frame("jSimuGate");
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		frame.setSize(640, 480);
		frame.add(panel);
		panel.init();
		frame.setVisible(true);

		Pattern wxh = Pattern.compile("([0-9]+)x([0-9]+)");
		for (String s : args) {
			Matcher match = wxh.matcher(s);
			if (match.matches()) {
				int x = Integer.parseInt(match.group(1));
				int y = Integer.parseInt(match.group(2));
				frame.setSize(x, y);
			}
			if (s.equals("--fullscreen")) frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		}

	}

	@Override public void componentHidden(ComponentEvent e) {}

	@Override public void componentMoved(ComponentEvent e) {}

	@Override public void componentResized(ComponentEvent e) {
		updateImageSize();
	}

	@Override public void componentShown(ComponentEvent e) {}

	@Override public void mouseClicked(MouseEvent e) {
		// clicking on inverter should invert it
		for (Part part : parts) {
			for (Pin pin : part.pins) {
				if (pin.bubble != null) {
					if (pin.bubble.at(e.getPoint())) {
						pin.toggleInversion();
						repaint();
						return;
					}
				}
			}
		}

		// clicking on something or nothing?
		Part topHit = null;
		for (Part part : parts) if (part.at(e.getPoint())) topHit = part;

		// RightClick?
		if (e.getButton() == MouseEvent.BUTTON3) {
			if (topHit == null) {
				parts.add(new AndGate(e.getX(), e.getY()));
				parts.add(new AndGate(400, 400));
			} else {
				parts.set(parts.indexOf(topHit), topHit.convert());
			}
		}

		// clicking on nothing should deselect everything.
		if (topHit == null) for (Part part : parts) part.selected = part.selecting = false;
		repaint();
		recentMouseEvent = e;
	}

	@Override public void mouseEntered(MouseEvent e) {
		recentMouseEvent = e;
	}

	@Override public void mouseExited(MouseEvent e) {
		recentMouseEvent = e;
	}

	/**
	 * For now: There's no ESC functionality, and lassoing always adds to the
	 * selection. This might be just fine.
	 *
	 * Ultimately, we'd like to try it this ways: Press:: - No keys when starting:
	 * set selection - Ctrl or Shift: toggle selection, but select again upon drag -
	 * Miss: start lasso Release:: - Add lasso to selection Drag:: - If Lasso --
	 * Shift or Ctrl+Shift: toggle selection -- Ctrl: add to selection -- No keys
	 * when starting: set selection - Else if hit when starting -- If nothing hit is
	 * selected, then select it -- If ctrl is pressed create a selected copy, and
	 * unselect original -- Move, if shift then constrained -- If ESC return to
	 * position
	 */
	@Override public void mousePressed(MouseEvent e) {

		// first check for pins
		for (Part part : parts) {
			for (Pin pin : part.pins) {
				if (pin.at(e.getPoint())) {
					protoWire = new Wire(pin, new Pin(e.getX(), e.getY()));
					repaint();
					recentMouseEvent = e;
					return;
				}
			}
		}

		// if clicking on a selected part, don't unselect anything
		Part topHit = null;
		for (Part part : parts) if (part.at(e.getPoint())) topHit = part;

		if (topHit != null) {
			// if there was a hit with ctrl or shift down: toggle it
			if (e.isControlDown() || e.isShiftDown()) {
				topHit.selected = !topHit.selected;
			} else {
				if (!topHit.selected) {
					// pick only this part
					for (Part part : parts) part.selected = false;
					topHit.selected = true;
				}
				// if clicked part is already selected, with no modifier keys, don't change the
				// selection.
			}
		}

		// if there's no hit, start dragging
		if (topHit == null) {
			// for no hits, begin lasso
			lassoBegin = new Point2D.Double(e.getX(), e.getY());
			lasso = new Rectangle2D.Double(e.getX(), e.getY(), 0, 0);
		}
		repaint();
		recentMouseEvent = e;
	}

	@Override public void mouseReleased(MouseEvent e) {
		// If landed on a pin, connect it
		if (protoWire != null) {
			Net.disconnect(protoWire);
			for (Part part : parts) {
				for (Pin pin : part.pins) {
					if (pin.at(e.getPoint())) {
						if (pin == protoWire.src) {
							// Don't connect pin to self
							break;
						}
						// connect or disconnect
						Wire oldWire = Net.findWire(protoWire.src, pin);
						if (oldWire != null) {
							System.out.println("FOUND " + oldWire + oldWire.src + oldWire.dst);
							Net.disconnect(oldWire);
							wires.remove(oldWire);
						} else {
							protoWire.to(pin);
							wires.add(protoWire);
						}
					}
				}
			}
			protoWire = null;
			repaint();
			recentMouseEvent = e;
			return;
		}

		// Add whatever is in the lasso to the selection
		for (Part part : parts) {
			if (part.selecting) {
				part.selected = true;
				part.selecting = false;
			}
		}
		lasso = null;
		lassoBegin = null;
		repaint();
		recentMouseEvent = e;
	}

	@Override public void mouseDragged(MouseEvent e) {
		if (protoWire != null) {
			protoWire.dst.transform.setToTranslation(e.getX(), e.getY());
			repaint();
			recentMouseEvent = e;
			return;
		}

		if (lasso != null) {
			// Adjust the lasso rectangle. calculate new upper left corner, width, and
			// height values.
			if (e.getX() < lassoBegin.getX()) {
				lasso.x = e.getX();
				lasso.width = lassoBegin.getX() - e.getX();
			} else {
				lasso.x = lassoBegin.getX();
				lasso.width = e.getX() - lassoBegin.getX();
			}
			if (e.getY() < lassoBegin.getY()) {
				lasso.y = e.getY();
				lasso.height = lassoBegin.getY() - e.getY();
			} else {
				lasso.y = lassoBegin.getY();
				lasso.height = e.getY() - lassoBegin.getY();
			}
			for (Part part : parts) {
				part.selecting = part.at(lasso);
			}
		} else if (recentMouseEvent != null) {
			// Not creating a lasso. Moving parts.
			if (recentMouseEvent.getID() == MouseEvent.MOUSE_PRESSED && !recentMouseEvent.isControlDown()
					&& !recentMouseEvent.isShiftDown()) {
				// Add whatever is under the mouse to the selection unless CTRL or SHIFT was
				// pressed
				for (Part part : parts) {
					if (part.at(recentMouseEvent.getPoint())) part.selected = true;
				}
			}
			int dx = e.getX() - recentMouseEvent.getX();
			int dy = e.getY() - recentMouseEvent.getY();
			for (Part part : parts) if (part.selected) part.transform.translate(dx, dy);
		}
		repaint();
		recentMouseEvent = e;
	}

	@Override public void mouseMoved(MouseEvent e) {
		recentMouseEvent = e;
	}

	@Override public void keyPressed(KeyEvent e) {
		switch (e.getKeyChar()) {
		case '+':
			for (Part part : parts) if (part.selected) part.increase();
			break;
		case '-':
			for (Part part : parts) if (part.selected) part.decrease();
			break;
		}
		repaint();
	}

	@Override public void keyReleased(KeyEvent e) {}

	@Override public void keyTyped(KeyEvent e) {
		switch (e.getKeyChar()) {
		case 'n':
			Net.dump();
			break;
		case ' ':
			String s = toString();
			System.out.println(s);
			System.out.println("---");
			fromString(s);
		case 'r':
			Numbered.renumber();

		}
	}

	public String toString() {
		String s = "";
		for (Part part : parts) {
			s += "PART:" + part.transform.toString().split("Transform")[1] + part.sn() + "(" + part.pins.size()
					+ " PINS:";
			for (Pin pin : part.pins) {
				s += pin.inverted ? " -" : " +";
				s += pin.sn();
			}
			s += ") ";
			if (part.tech != Tech.DEFAULT) s += part.tech;
			s += "\n";
		}
		for (Wire wire : wires) {
			s += "WIRE: " + wire.src.sn() + " - " + wire.dst.sn() + "\n";
		}
		return s;
	}

	public void fromString(String s) {
		final String t_rule = "\\[ *\\[ *([-0-9.]+) *, *([-0-9.]+) *, *([-0-9.]+) *\\] *, *\\[ *([-0-9.]+) *, *([-0-9.]+) *, *([-0-9.]+) *\\] *\\]";
		final String part_prepins_rule = "PART: *" + t_rule + " *([A-Za-z_0-9]+)#([0-9]+)\\(([0-9]+) PINS:";
		final Pattern part_pin_pattern = Pattern.compile("([-+])Pin#([0-9]+)");
		final Pattern part_pattern = Pattern.compile(part_prepins_rule);
		final Pattern wire_pattern = Pattern.compile("WIRE: *Pin#([0-9]+) *- *Pin#([0-9]+)");
		Scanner scan = new Scanner(s);
		Map<Integer, Pin> construction = new HashMap<Integer, Pin>();
		while (scan.hasNextLine()) {
			if (scan.findInLine(part_pattern) != null) {
				MatchResult result = scan.match();
				System.out.print("PART AT");
				float m00 = Float.parseFloat(result.group(1));
				float m01 = Float.parseFloat(result.group(2));
				float m02 = Float.parseFloat(result.group(3));
				float m10 = Float.parseFloat(result.group(4));
				float m11 = Float.parseFloat(result.group(5));
				float m12 = Float.parseFloat(result.group(6));
				System.out.printf("\n%7.2f %7.2f %7.2f  ",m00,m01,m02);
				System.out.printf("\n%7.2f %7.2f %7.2f  ",m10,m11,m12);
				// Inconsistent order of parameters in AffineTransform toString and constructor!
				AffineTransform t = new AffineTransform(m00, m10, m01, m11, m02 + 150, m12 + 50);
				String partName = result.group(7);
				int partNumber = Integer.parseInt(result.group(8));
				int pinCount = Integer.parseInt(result.group(9));
				try {
					System.out.println("jsimugate." + partName);
					Part newPart = (Part) Class.forName("jsimugate." + partName)
							.getConstructor(double.class, double.class).newInstance(200, 200);
					newPart.transform.setTransform(t);
					while (newPart.pins.size() > pinCount) newPart.decrease();
					while (newPart.pins.size() < pinCount) newPart.increase();
					parts.add(newPart);
					System.out.print(partName + partNumber + " with " + pinCount + " pins:");

					
					for (int pinIndex = 0;scan.findInLine(part_pin_pattern) != null;pinIndex++) {
						MatchResult pinResult = scan.match();
						boolean invertPin = pinResult.group(1).equals("-");
						int pinNumber = Integer.parseInt(pinResult.group(2));
						if (invertPin) System.out.print(" NOT");
						System.out.print(" pin" + pinNumber);
						Pin pin = newPart.pins.get(pinIndex);
						if (invertPin) pin.toggleInversion();
						construction.put(pinNumber, pin);
					}
					scan.findInLine("\\) *([^ ]*)");
					String techString=scan.match().group(1);
					Tech tech=Tech.DEFAULT;
					if (!techString.isEmpty()) tech = Tech.valueOf(techString);
					System.out.println(" TECH " + tech);
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.out.println(scan.nextLine());
			} else if (scan.findInLine(wire_pattern) != null) {
				MatchResult result = scan.match();
				int a=Integer.parseInt(result.group(1));
				int b=Integer.parseInt(result.group(2));
				System.out.println("WIRE pin" + a + " to pin" + b);
				scan.nextLine();
				wires.add(new Wire(construction.get(a),construction.get(b)));
			} else {
				System.err.println("No match reading data: " + scan.nextLine());
			}
		}
		scan.close();
		repaint();
	}
}
