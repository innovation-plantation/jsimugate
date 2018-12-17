package jsimugate;

import java.applet.Applet;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class JSimuGate extends Applet implements MouseListener, MouseMotionListener {
	private static final long serialVersionUID = 1L;
	Part parts[] = { new AndGate(50, 100), new XorGate(150, 50), new OrGate(225, 100), new AndGate(50, 300),
			new XorGate(150, 250), new MajorityGate(225, 300)/* , new Part(50, 250) */ };
	private Dimension size;
	private Image image;
	private Graphics graphics;
	private static final Stroke lassoStroke = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,
			new float[] { 9, 2 }, 0);
	MouseEvent recentMouseEvent = null;
	private static Point2D.Double lassoBegin = null;
	private static Rectangle2D.Double lasso = null;

	public void init() {
		updateImageSize();
		this.addMouseListener(this);
		this.addMouseMotionListener(this);

	}

	private void updateImageSize() {
		size = getSize();
		image = createImage(size.width, size.height);
		graphics = image.getGraphics();
	}

	public void update(Graphics g) {
		graphics.clearRect(0, 0, size.width, size.height);
		paint(graphics);
		g.drawImage(image, 0, 0, this);
	}

	public void paint(Graphics g1D) {
		Graphics2D g = (Graphics2D) g1D;
		for (Part part : parts) part.operate();

		for (Symbol part : parts) part.draw(g);

		if (lasso != null) {
			g.setColor(Color.blue);
			g.setStroke(lassoStroke);
			g.draw(lasso);
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
		frame.setSize(500, 500);
		frame.add(panel);
		frame.setVisible(true);
		// frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		panel.init();
	}


	@Override public void mouseClicked(MouseEvent e) {
		// clicking on nothing should deselect everything.
		Part topHit = null;
		for (Part part:parts) if (part.at(e.getPoint())) topHit=part;
		if (topHit==null) for (Part part:parts) part.selected=part.selecting=false;
		recentMouseEvent = e;
	}

	@Override public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		recentMouseEvent = e;
	}

	@Override public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		recentMouseEvent = e;
	}

	/**
	 * For now: There's no ESC functionality, and lassoing always adds to the selection.
	 * This might be just fine. 
     *
	 * Ultimately, we'd like to try it this ways:
	 * Press:: - No keys when starting: set selection - Ctrl or Shift: toggle
	 * selection, but select again upon drag - Miss: start lasso Release:: - Add
	 * lasso to selection Drag:: - If Lasso -- Shift or Ctrl+Shift: toggle selection
	 * -- Ctrl: add to selection -- No keys when starting: set selection - Else if
	 * hit when starting -- If nothing hit is selected, then select it -- If ctrl is
	 * pressed create a selected copy, and unselect original -- Move, if shift then
	 * constrained -- If ESC return to position
	 */
	@Override public void mousePressed(MouseEvent e) {
		// if clicking on a selected part, don't unselect anything
		Part topHit = null;
		for (Part part : parts) if (part.at(e.getPoint())) topHit = part;
		
		if (topHit!=null) {
			// if there was a hit with ctrl or shift down: toggle it		
			if (e.isControlDown() || e.isShiftDown()) {
				topHit.selected = !topHit.selected;
			} else {
				if (!topHit.selected) {
					// pick only this part
					for (Part part : parts) part.selected = false;
					topHit.selected = true;
				}
				// if clicked part is already selected, with no modifier keys, don't change the selection.
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

}
