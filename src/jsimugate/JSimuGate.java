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
import java.util.Map;
import java.util.Scanner;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jsimugate.Part.Tech;

public class JSimuGate extends Applet implements MouseListener, MouseMotionListener, KeyListener, ComponentListener {
	private static final long serialVersionUID = 1L;
	Circuit circuit = new Circuit(new ArrayList<Part>(), new ArrayList<PartsBin>(), new ArrayList<Wire>());
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

		circuit.bins.add(new PartsBin(100, 50, new MajorityGate(0, 0).not()));  
		circuit.parts.add(new AndGate(100, 150));
		circuit.parts.add(new OrGate(100, 250));
		circuit.parts.add(new XorGate(100, 350));
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

		for (Part part : circuit.parts) {
			part.operate();
			part.draw(g);
			for (Pin pin : part.pins) {
				pin.setInValue(Signal._Z);
			}
		}

		for (Wire wire : circuit.wires) wire.draw(g);

		if (lasso != null) {
			g.setColor(Color.blue);
			Stroke restore = g.getStroke();
			g.setStroke(lassoStroke);
			g.draw(lasso);
			g.setStroke(restore);
		}

		if (protoWire != null) {
			protoWire.value = protoWire.src.getOutValue();
			protoWire.draw(g);
		}
		
		for (PartsBin bin:circuit.bins) {
			bin.draw(g);
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
		for (Part part : circuit.parts) {
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
		for (Part part : circuit.parts) if (part.at(e.getPoint())) topHit = part;

		// RightClick?
		if (e.getButton() == MouseEvent.BUTTON3) {
			if (topHit == null) {
				circuit.parts.add(new AndGate(e.getX(), e.getY()));
				circuit.parts.add(new AndGate(400, 400));
			} else {
				circuit.parts.set(circuit.parts.indexOf(topHit), topHit.convert());
			}
		}

		// clicking on nothing should deselect everything.
		if (topHit == null) for (Part part : circuit.parts) part.setSelected(part.selecting = false);
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
		for (Part part : circuit.parts) {
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
		for (Part part : circuit.parts) if (part.at(e.getPoint())) topHit = part;

		if (topHit != null) {
			// if there was a hit with ctrl or shift down: toggle it
			if (e.isControlDown() || e.isShiftDown()) {
				topHit.setSelected(!topHit.isSelected());
			} else {
				if (!topHit.isSelected()) {
					// pick only this part
					for (Part part : circuit.parts) part.setSelected(false);
					topHit.setSelected(true);
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
			for (Part part : circuit.parts) {
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
							circuit.wires.remove(oldWire);
						} else {
							protoWire.to(pin);
							circuit.wires.add(protoWire);
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
		for (Part part : circuit.parts) {
			if (part.selecting) {
				part.setSelected(true);
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
			for (Part part : circuit.parts) {
				part.selecting = part.at(lasso);
			}
		} else if (recentMouseEvent != null) {
			// Not creating a lasso. Moving parts.
			if (recentMouseEvent.getID() == MouseEvent.MOUSE_PRESSED && !recentMouseEvent.isControlDown()
					&& !recentMouseEvent.isShiftDown()) {
				// Add whatever is under the mouse to the selection unless CTRL or SHIFT was
				// pressed
				for (Part part : circuit.parts) {
					if (part.at(recentMouseEvent.getPoint())) part.setSelected(true);
				}
			}
			int dx = e.getX() - recentMouseEvent.getX();
			int dy = e.getY() - recentMouseEvent.getY();
			for (Part part : circuit.parts) if (part.isSelected()) part.transform.translate(dx, dy);
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
			for (Part part : circuit.parts) if (part.isSelected()) part.increase();
			break;
		case '-':
			for (Part part : circuit.parts) if (part.isSelected()) part.decrease();
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
			String s = circuit.toString();
			circuit.fromString(s);
			repaint();
		case 'r':
			Numbered.renumber();

		}
	}

}
