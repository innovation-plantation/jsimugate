package jsimugate;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.KeyboardFocusManager;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.filechooser.FileNameExtensionFilter;

import jsimugate.Part.Tech;

public class JSimuGate extends JPanel implements MouseListener, MouseMotionListener, ComponentListener {
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
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
			if (e.getID() == KeyEvent.KEY_PRESSED) switch (e.getKeyChar()) {
			case 'd':
				Log.println(circuit.toString());
			case 'n':
				Net.dump();
				break;
			case 'r':
				Numbered.renumber();
			case 'w':
				for (Wire wire : circuit.wires) System.out.println(wire);
			case '+':
				for (Part part : circuit.parts) if (part.isSelected()) {
					if (e.isAltDown()) part.transform.scale(2, 2);
					else part.increase();
				}
				break;
			case '-':
				for (Part part : circuit.parts) if (part.isSelected()) {
					if (e.isAltDown()) part.transform.scale(.5, .5);
					else part.decrease();
				}
				break;
			default:
				int step = 4;
				switch (e.getKeyCode()) {
				case KeyEvent.VK_UP:
					for (Part part : circuit.parts) {
						if (part.isSelected()) {
							part.transform.scale(1, -1);
						}
					}
					break;
				case KeyEvent.VK_DOWN:
					for (Part part : circuit.parts) {
						if (part.isSelected()) {
							part.transform.scale(-1, 1);
						}
					}
					break;
				case KeyEvent.VK_LEFT:
					step = -step; // fall-through
				case KeyEvent.VK_RIGHT:
					if (e.isControlDown()) step *= 3;
					if (e.isShiftDown()) step /= 2;
					for (Part part : circuit.parts) {
						if (part.isSelected()) {
							if (e.isAltDown()) part.transform.setToTranslation(part.transform.getTranslateX(),
									part.transform.getTranslateY());
							else part.transform.rotate(Math.PI / step);
						}
					}
					break;
				}
			}
			for (Part part:circuit.parts) if (part.isSelected()) part.processChar(Character.toUpperCase(e.getKeyChar()));
			repaint();
			return false;
		});

		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addComponentListener(this);

		circuit.bins.add(new PartsBin(200,50,new InConnector()));
		circuit.bins.add(new PartsBin(250,50,new OutConnector()));
		circuit.bins.add(new PartsBin(300,50,new Clk()));
		
		circuit.bins.add(new PartsBin(50, 50, new MajorityGate().not()));
		circuit.bins.add(new PartsBin(50, 100, new AndGate()));
		circuit.bins.add(new PartsBin(50, 150, new OrGate()));
		circuit.bins.add(new PartsBin(50, 200, new XorGate()));

		circuit.bins.add(new PartsBin(100, 50, new MajorityGate()));
		circuit.bins.add(new PartsBin(100, 100, new AndGate().not()));
		circuit.bins.add(new PartsBin(100, 150, new OrGate().not()));
		circuit.bins.add(new PartsBin(100, 200, new XorGate().not()));

		circuit.bins.add(new PartsBin(50, 250,new NPNTransistor()));
		circuit.bins.add(new PartsBin(100, 250, new PullupResistor()));

		circuit.bins.add(new PartsBin(50, 300, new MajorityGate().not().asTech(Tech.OC)));
		circuit.bins.add(new PartsBin(50, 350, new AndGate().asTech(Tech.OC)));
		circuit.bins.add(new PartsBin(50, 400, new OrGate().asTech(Tech.OC)));
		circuit.bins.add(new PartsBin(50, 450, new XorGate().asTech(Tech.OC)));

		circuit.bins.add(new PartsBin(100, 300, new MajorityGate().asTech(Tech.OC)));
		circuit.bins.add(new PartsBin(100, 350, new AndGate().not().asTech(Tech.OC)));
		circuit.bins.add(new PartsBin(100, 400, new OrGate().not().asTech(Tech.OC)));
		circuit.bins.add(new PartsBin(100, 450, new XorGate().not().asTech(Tech.OC)));


		circuit.bins.add(new PartsBin(50,500,new PNPTransistor()));
		circuit.bins.add(new PartsBin(100, 500, new PulldownResistor()));
		updateImageSize();
		new javax.swing.Timer(10, e -> {
			Net.operateAll();
			for (Part part : circuit.parts) part.operate();
			repaint();
		}).start();
	}

	private void updateImageSize() {
		size = getSize();
		image = createImage(size.width, size.height);
		if (image != null) graphics = image.getGraphics();
	}

	public void update(Graphics g) {
		paint(g);
	}

	public void paint(Graphics g) {
		if (graphics == null) return;
		graphics.clearRect(0, 0, size.width, size.height);
		render((Graphics2D) graphics);
		g.drawImage(image, 0, 0, this);
	}

	public void render(Graphics2D g) {
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		circuit.render(g);

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

		for (PartsBin bin : circuit.bins) {
			bin.draw(g);
		}
	}

	static File file = new File("circuit.logic");

	public static void main(String[] args) throws InstantiationException, IllegalAccessException, SecurityException {
		JSimuGate panel = new JSimuGate();
		JFrame frame = new JFrame("jSimuGate");
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		frame.setSize(1280, 1024);
		frame.add(panel);
		panel.init();

		JMenuBar bar = new JMenuBar();
		JMenu fileMenu = createFileMenu(panel);
		bar.add(fileMenu);
		frame.setJMenuBar(bar);

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

	public static JMenu createFileMenu(JSimuGate panel) {

		JMenu fileMenu = new JMenu("File");
		JMenuItem menuItem;

		menuItem = new JMenuItem("Open...");
		menuItem.addActionListener(event -> {
			JFileChooser choice = new JFileChooser(file);
			choice.setFileFilter(new FileNameExtensionFilter("jSimuGate Circuits (.logic)", "logic"));
			if (choice.showOpenDialog(panel) == JFileChooser.APPROVE_OPTION) {
				if (choice.getSelectedFile().exists()) {
					try {
						Scanner scan = new Scanner(choice.getSelectedFile());
						panel.circuit.fromScanner(scan);
					} catch (FileNotFoundException ex) {
						JOptionPane.showMessageDialog(panel, ex.getMessage());
					}
				} else {
					JOptionPane.showMessageDialog(panel, "File " + file + " does not exist");
				}
			} ;
		});
		fileMenu.add(menuItem);

		menuItem = new JMenuItem("Save as...");
		menuItem.addActionListener(event -> {
			JFileChooser choice = new JFileChooser(file);
			choice.setFileFilter(new FileNameExtensionFilter("jSimuGate Circuits (.logic)", "logic"));
			if (choice.showSaveDialog(panel) == JFileChooser.APPROVE_OPTION) {

				if (choice.getSelectedFile().exists()) {
					if (JOptionPane.showConfirmDialog(panel,
							"Overwrite " + choice.getSelectedFile().getName() + "?") != JOptionPane.YES_OPTION)
						return;
					System.out.println("OVERWRITING");
				}
				file = choice.getSelectedFile();
				System.out.println("Save as " + file.getAbsolutePath());
				try {
					PrintWriter printWriter = new PrintWriter(file.getAbsolutePath(), "UTF-8");
					Numbered.renumber();
					printWriter.write(panel.circuit.toString());
					printWriter.close();
				} catch (FileNotFoundException | UnsupportedEncodingException ex) {
					JOptionPane.showMessageDialog(panel, ex.getMessage());
				}
			} ;
		});
		fileMenu.add(menuItem);

		menuItem = new JMenuItem("Save");
		menuItem.addActionListener(event -> {
			System.out.println("Save" + file.getAbsolutePath());
			try {
				PrintWriter printWriter = new PrintWriter(file.getAbsolutePath(), "UTF-8");
				Numbered.renumber();
				printWriter.write(panel.circuit.toString());
				printWriter.close();
			} catch (FileNotFoundException | UnsupportedEncodingException ex) {
				JOptionPane.showMessageDialog(panel, ex.getMessage());
			}
		});
		fileMenu.add(menuItem);

		return fileMenu;
	}

	@Override public void componentHidden(ComponentEvent e) {}

	@Override public void componentMoved(ComponentEvent e) {}

	@Override public void componentResized(ComponentEvent e) {
		updateImageSize();
	}

	@Override public void componentShown(ComponentEvent e) {}

	@SuppressWarnings("serial") @Override public void mouseClicked(MouseEvent e) {
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
			if (topHit != null) {
				Component display = this;
				JPopupMenu menu = new javax.swing.JPopupMenu("Part Menu");
				menu.add(new JMenuItem("Convert (DeMorgan)") {
					{
						addActionListener(e -> {
							for (Part part : circuit.parts) {
								if (part.isSelected()) circuit.parts.set(circuit.parts.indexOf(part), part.convert());
							}
							display.repaint();
						});
					}
				});
				for (Tech tech : Tech.values()) {
					menu.add(new JMenuItem(tech.description) {
						{
							addActionListener(e -> {
								for (Part part : circuit.parts) {
									if (part.isSelected())
										circuit.parts.set(circuit.parts.indexOf(part), part.asTech(tech));
								}
								display.repaint();
							});
						}
					});
				}

				menu.show(this, e.getX(), e.getY());
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

		// if there's no hit, check for bins
		if (topHit == null) for (PartsBin bin : circuit.bins) {
			if (bin.at(e.getPoint())) {
				for (Part part : circuit.parts) {
					// unselect everything else
					part.setSelected(false);
				}
				Part part = bin.produce(e.getX(), e.getY());
				part.setSelected(true);
				circuit.parts.add(part);
				repaint();
				return;
			}
		}

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
							Log.println("FOUND " + oldWire + oldWire.src + oldWire.dst);
							Net.disconnect(oldWire);
							circuit.wires.remove(oldWire);
						} else {
							circuit.wires.add(protoWire.to(pin));
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
		if (lasso != null) {
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
			return;
		}

		for (PartsBin bin : circuit.bins) {
			if (bin.at(e.getPoint())) {
				for (Part part : circuit.parts) {
					if (part.isSelected()) for (Pin pin : part.pins) {
						circuit.wires.removeIf(wire -> wire.src == pin || wire.dst == pin);
					}
				}
				circuit.parts.removeIf(part -> part.isSelected());
			}
		}
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
			// Not creating a lasso. Moving or Copying parts.

			if (recentMouseEvent.getID() == MouseEvent.MOUSE_PRESSED) {
				// if (!recentMouseEvent.isControlDown())
				{
					// Moving
					if (!recentMouseEvent.isShiftDown()) {
						// Add whatever is under the mouse to the selection unless CTRL or SHIFT was
						// pressed
						for (Part part : circuit.parts) {
							if (part.at(recentMouseEvent.getPoint())) {
								part.setSelected(true);
							}
						}
					}
				}
				// else
				if (recentMouseEvent.isControlDown()) {
					// Copying
					String string = "";
					for (Part part : circuit.parts) {
						if (part.isSelected()) {
							string += part.toString();
							if (recentMouseEvent.isShiftDown()) part.selecting = true;
							part.setSelected(false);
						}
					}
					for (Wire wire : circuit.wires) string += wire.toString();
					circuit.fromString(string);

				}
			}

			int dx = e.getX() - recentMouseEvent.getX();
			int dy = e.getY() - recentMouseEvent.getY();
			AffineTransform delta = AffineTransform.getTranslateInstance(dx, dy);
			for (Part part : circuit.parts) if (part.isSelected()) part.transform.preConcatenate(delta);
		}
		repaint();
		recentMouseEvent = e;
	}

	@Override public void mouseMoved(MouseEvent e) {
		recentMouseEvent = e;
	}

}
