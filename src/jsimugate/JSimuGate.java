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
			new XorGate(150, 250), new MajorityGate(225, 300), new Part(50, 250) };
	private Dimension size;
	private Image image;
	private Graphics graphics;
	private static final Stroke lassoStroke=new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9,2}, 0);
	MouseEvent recentMouseEvent = null;
	private static Point2D.Double lassoBegin=null;
	private static Rectangle2D.Double lasso=null;

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
		
		if (lasso!=null) {
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

	@Override public void mousePressed(MouseEvent e) {
		Part topSelectedPart=null;
		if (e.isControlDown()) for (Part part : parts) {
			if (part.at(e.getPoint())) part.selected = !part.selected;
		}
		else {
			for (Part part : parts) {
				part.selected = false; 
		    }
			
			for (Part part : parts) {
				if (part.at(e.getPoint())) {
					if (topSelectedPart!=null) topSelectedPart.selected=false;
					part.selected = true;
					topSelectedPart = part;
				}	
			}
		}
        if (topSelectedPart == null) {
        	lassoBegin = new Point2D.Double(e.getX(),e.getY());
            lasso = new Rectangle2D.Double(e.getX(),e.getY(),0,0);
        }	
		repaint();
		recentMouseEvent = e;
	}

	@Override public void mouseReleased(MouseEvent e) {
		recentMouseEvent = e;
	}

	@Override public void mouseDragged(MouseEvent e) {
		if (lasso != null) {
			if (e.getX()<lassoBegin.getX()) {
				lasso.x = e.getX();
				lasso.width = lassoBegin.getX()-e.getX();
			} else {
				lasso.x = lassoBegin.getX();
				lasso.width = e.getX()-lassoBegin.getX();
			}
			if (e.getY()<lassoBegin.getY()) {
				lasso.y = e.getY();
				lasso.height = lassoBegin.getY()-e.getY();
			} else {
				lasso.y = lassoBegin.getY();
				lasso.height = e.getY()-lassoBegin.getY();
			}
		} else if (recentMouseEvent != null) {
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
