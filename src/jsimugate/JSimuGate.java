package jsimugate;

import jsimugate.Part.Tech;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.awt.Toolkit.getDefaultToolkit;
import static java.awt.event.KeyEvent.*;

/**
 * User interface for circuit simulation. This could be an Applet by changing JPanel to JApplet or Applet, etc.
 */
public class JSimuGate extends Panel implements MouseListener, MouseMotionListener, ComponentListener {
    private static final long serialVersionUID = 1L;
    Circuit circuit = new Circuit().withStandardBins();
    private Dimension size;
    private Image image;
    private Graphics graphics;
    private static final Stroke lassoStroke = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,
            new float[]{9, 2}, 0);
    MouseEvent recentMouseEvent = null;
    private Wire protoWire = null;
    Pin recentSrc = null, recentDst = null;
    private static Point2D.Double lassoBegin = null;
    private static Rectangle2D.Double lasso = null;
    static File file = new File("circuit.logic"); // set the default file name and path
    static JFrame frame = new JFrame("jSimuGate 0.3");
    static final AffineTransform identity = new AffineTransform();
    static double scaleUnit = Math.sqrt(Math.sqrt(Math.sqrt(2))), inverseScaleUnit = 1 / scaleUnit;

    /**
     * Initialize the GUI. Turn on the event listeners and place the part bins on the display.
     */
    public void init() {
        this.addMouseWheelListener(e -> {
            if (e.isControlDown()) {
                int x = e.getX(), y = e.getY();
                AffineTransform t = AffineTransform.getTranslateInstance(x, y);
                for (int i = 0; i < e.getWheelRotation(); i++) t.scale(inverseScaleUnit, inverseScaleUnit);
                for (int j = 0; j > e.getWheelRotation(); j--) t.scale(scaleUnit, scaleUnit);
                t.translate(-x, -y);
                for (Part part : circuit.parts) {
                    if (part.isSelected() || !e.isShiftDown()) part.transform.preConcatenate(t);
                }
            }
            else if (e.isShiftDown()) {
                for (Part part : circuit.parts) {
                    if (part.isSelected()) {
                        for (int i = 0; i < e.getWheelRotation(); i++) part.decrease();
                        for (int j = 0; j > e.getWheelRotation(); j--) part.increase();
                    }
                }
            }
        });
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            if (e.getID() == KeyEvent.KEY_PRESSED) {
                switch (e.getKeyChar()) {
                    case '+':
                        if (e.isControlDown()) {
                            int x = recentMouseEvent.getX(), y = recentMouseEvent.getY();
                            circuit.scale(scaleUnit,x,y);
                            break;
                        }
                        for (Part part : circuit.parts)
                            if (part.isSelected()) {
                                if (e.isAltDown()) part.transform.scale(2, 2);
                                else part.increase();
                            }
                        break;
                    case '-':
                        if (e.isControlDown()) {
                            int x = recentMouseEvent.getX(), y = recentMouseEvent.getY();
                            circuit.scale(inverseScaleUnit,x,y);
                            break;
                        }
                        for (Part part : circuit.parts)
                            if (part.isSelected()) {
                                if (e.isAltDown()) part.transform.scale(.5, .5);
                                else part.decrease();
                            }
                        break;

                    default:
                        int step = 4;
                        switch (e.getKeyCode()) {
                            case VK_UP:
                                for (Part part : circuit.parts) {
                                    if (part.isSelected()) {
                                        part.transform.scale(1, -1);
                                    }
                                }
                                break;
                            case VK_DOWN:
                                for (Part part : circuit.parts) {
                                    if (part.isSelected()) {
                                        part.transform.scale(-1, 1);
                                    }
                                }
                                break;
                            case VK_LEFT:
                                step = -step; // fall-through
                            case VK_RIGHT:
                                if (e.isControlDown()) step *= 3;
                                if (e.isShiftDown()) step /= 2;
                                for (Part part : circuit.parts) {
                                    if (part.isSelected()) {
                                        if (e.isAltDown())
                                            part.transform.setToTranslation(part.transform.getTranslateX(),
                                                    part.transform.getTranslateY());
                                        else part.transform.rotate(Math.PI / step);
                                    }
                                }
                                break;
                            case VK_DELETE:
                                circuit.removeSelectedParts();
                                break;
                            case VK_A: {
                                if (e.isControlDown()) {
                                    for (Part part : circuit.parts) part.setSelected(true);
                                }
                                break;
                            }
                        }
                }
                Log.println("key down " + e.getKeyChar());
                if (!e.isAltDown() && !e.isControlDown()) {
                    for (Part part : circuit.parts) {
                        if (part.isSelected()) part.processChar(Character.toUpperCase(e.getKeyChar()));
                    }
                }
            } else if (e.getID() == KeyEvent.KEY_RELEASED) {
                Log.println("key up");
                for (Part part : circuit.parts) {
                    if (part.isSelected()) part.processChar('\0');
                }
            }
            repaint();
            return false;
        });

        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addComponentListener(this);

        updateImageSize();
        circuit.startup(() -> repaint()); // repaint this component whenever the circuit is updated
        File spaceNavigator = new File("/dev/hidraw2");
        if (spaceNavigator.exists()&&spaceNavigator.canRead()) {
            try {
                FileInputStream stream = new FileInputStream(spaceNavigator);
                byte[] bytes=new byte[7];
                ByteBuffer packet = ByteBuffer.allocateDirect(bytes.length);
                packet.order(ByteOrder.LITTLE_ENDIAN);
                new Thread(() -> {
                    for (;;) {
                        try {
                            if (stream.read(bytes) < 0) continue;
                            packet.rewind();
                            packet.put(bytes);
                            packet.rewind();
                            if (packet.get()!=1) continue;
                            int dx=packet.getShort(),dy=packet.getShort(),dz=packet.getShort();
                            System.out.printf("%04d %04d %04d \n",dx,dy,dz);
                            int x=getWidth()/2,y=getHeight()/2;
                            circuit.scale(1+dz*.0001,x,y);
                            circuit.translate(-dx*.05,-dy*.05);
                        } catch (IOException ex) {
                        }
                    }
                }).start();
            } catch (IOException ex) {}

        }
    }

    /**
     * Whenever there is a resize event, or when the window is created, this should be called to resize
     * the image that is used for double-buffering the display.
     */
    private void updateImageSize() {
        size = getSize();
        if (size.width < 1) size.width = 1;
        if (size.height < 1) size.height = 1;
        image = createImage(size.width, size.height);
        if (image != null) graphics = image.getGraphics();
    }

    /**
     * Just calls paint so that this class can be Panel, JPanel, Applet, or JApplet and it will work for any.
     * This is what Applets and Panels wold clear the display if this is not overwritten with a direct call to paint.
     *
     * @param g
     */
    public void update(Graphics g) {
        paint(g);
    }

    /**
     * Perform double buffering for the rendering.
     */
    public void paint(Graphics g) {
        if (graphics == null) return;
        graphics.clearRect(0, 0, size.width, size.height);
        render((Graphics2D) graphics);
        g.drawImage(image, 0, 0, this);
    }

    /**
     * Like paint, render everything on the display
     *
     * @param g
     */
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

    /**
     * Start the circuit simulation program
     */
    public static void mainProgram(String[] args) {
        JSimuGate panel = new JSimuGate();

        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (0 != JOptionPane.showConfirmDialog(null, "Quit now?")) return;
                System.exit(0);
            }
        });
        // Change the icon image
        ImageIcon img = new ImageIcon("classes/artifacts/jsimugate_jar/innovation-plantation.png");
        frame.setIconImage(img.getImage());
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

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            mainProgram(args);
        });
    }

    /**
     * Create the file menu
     *
     * @param panel
     * @return the created menu
     */
    public static JMenu createFileMenu(JSimuGate panel) {

        JMenu fileMenu = new JMenu("File");
        JMenuItem menuItem;

        menuItem = new JMenuItem("New");
        menuItem.addActionListener(event -> {
            if (0 != JOptionPane.showConfirmDialog(null, "Clear existing circuit?")) return;
            Net.nets.clear();
            PinGroup.pinGroups.clear();
            panel.circuit = new Circuit().withStandardBins();
            panel.circuit.startup(() -> panel.repaint());
        });
        fileMenu.add(menuItem);

        menuItem = new JMenuItem("Load... (add to existing circuit)");
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
            }
        });
        fileMenu.add(menuItem);

        menuItem = new JMenuItem("Open... (replace existing circuit)");
        menuItem.addActionListener(event -> {
            JFileChooser choice = new JFileChooser(file);
            choice.setFileFilter(new FileNameExtensionFilter("jSimuGate Circuits (.logic)", "logic"));
            if (choice.showOpenDialog(panel) == JFileChooser.APPROVE_OPTION) {
                if (choice.getSelectedFile().exists()) {
                    try {
                        Scanner scan = new Scanner(choice.getSelectedFile());
                        Net.nets.clear();
                        PinGroup.pinGroups.clear();
                        panel.circuit = new Circuit().withStandardBins();
                        panel.circuit.fromScanner(scan);
                        panel.circuit.startup(() -> panel.repaint());
                    } catch (FileNotFoundException ex) {
                        JOptionPane.showMessageDialog(panel, ex.getMessage());
                    }
                } else {
                    JOptionPane.showMessageDialog(panel, "File " + file + " does not exist");
                }
            }
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
                String filename = appendLogicToFilename(file.getAbsolutePath());
                System.out.println("Save as " + filename);
                try {
                    PrintWriter printWriter = new PrintWriter(filename, "UTF-8");
                    Numbered.renumber();
                    printWriter.write(panel.circuit.toString());
                    printWriter.close();
                } catch (FileNotFoundException | UnsupportedEncodingException ex) {
                    JOptionPane.showMessageDialog(panel, ex.getMessage());
                }
            }
        });
        fileMenu.add(menuItem);

        menuItem = new JMenuItem("Save");
        menuItem.addActionListener(event -> {
            String filename = appendLogicToFilename(file.getAbsolutePath());
            System.out.println("Save" + filename);
            try {
                PrintWriter printWriter = new PrintWriter(filename, "UTF-8");
                Numbered.renumber();
                printWriter.write(panel.circuit.toString());
                printWriter.close();
            } catch (FileNotFoundException | UnsupportedEncodingException ex) {
                JOptionPane.showMessageDialog(panel, ex.getMessage());
            }
        });
        fileMenu.add(menuItem);

        menuItem = new JMenuItem("Exit");
        menuItem.addActionListener(event -> {
            getDefaultToolkit().getSystemEventQueue().postEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
        });
        fileMenu.add(menuItem);

        return fileMenu;
    }

    /**
     * unused event
     *
     * @param e
     */
    @Override
    public void componentHidden(ComponentEvent e) {
    }

    /**
     * unused event
     *
     * @param e
     */
    @Override
    public void componentMoved(ComponentEvent e) {
    }

    /**
     * unused event
     *
     * @param e
     */
    @Override
    public void componentResized(ComponentEvent e) {
        updateImageSize();
    }

    /**
     * unused event
     *
     * @param e
     */
    @Override
    public void componentShown(ComponentEvent e) {
    }

    @SuppressWarnings("serial")
    /**
     * Handle mouse clicks.
     *  - Clicking on the spot of an inverter adds or removes it from a part.
     *  - Right-clicking on a part brings up a context menu.
     *  - Otherwise turns off all selection except part being clicked on
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        switch (e.getClickCount()) {
            case 2:
                for (Part part : circuit.parts) {
                    for (Pin pin : part.pins) {
                        if (pin.at(e.getPoint())) {
                            PinGroup src = PinGroup.groupOf(recentSrc);
                            PinGroup dst = PinGroup.groupOf(recentDst);
                            PinGroup target = PinGroup.groupOf(pin);
                            if (src == null) return;
                            if (dst == null) return;
                            if (target == null) return;
                            int iSrc = src.pins.indexOf(recentSrc);
                            int iDst = dst.pins.indexOf(recentDst);
                            int iTarget = target.pins.indexOf(pin);
                            if (target == src) {
                                for (int i = iSrc + 1, j = iDst + 1; i <= iTarget && i < src.size() && j < dst.size(); i++, j++) {
                                    addOrRemoveWire(src.pins.get(i), dst.pins.get(j));
                                }
                                for (int i = iSrc - 1, j = iDst - 1; i >= iTarget && i >= 0 && j >= 0; i--, j--) {
                                    addOrRemoveWire(src.pins.get(i), dst.pins.get(j));
                                }
                            } else if (target == dst) {
                                for (int i = iSrc + 1, j = iDst + 1; j <= iTarget && i < src.size() && j < dst.size(); i++, j++) {
                                    addOrRemoveWire(src.pins.get(i), dst.pins.get(j));
                                }
                                for (int i = iSrc - 1, j = iDst - 1; j >= iTarget && i >= 0 && j >= 0; i--, j--) {
                                    addOrRemoveWire(src.pins.get(i), dst.pins.get(j));
                                }

                            }
                            return;
                        }
                    }
                    if (part.at(e.getPoint())) {
                        part.processDoubleClick();
                    }
                }
                return;
            case 3:
                PinGroup src = PinGroup.groupOf(recentSrc);
                PinGroup dst = PinGroup.groupOf(recentDst);
                if (src == null) return;
                if (dst == null) return;
                int iSrc = src.pins.indexOf(recentSrc);
                int iDst = dst.pins.indexOf(recentDst);
                for (int i = iSrc + 1, j = iDst + 1; i < src.size() && j < dst.size(); i++, j++) {
                    addOrRemoveWire(src.pins.get(i), dst.pins.get(j));
                }
                for (int i = iSrc - 1, j = iDst - 1; i >= 0 && j >= 0; i--, j--) {
                    addOrRemoveWire(src.pins.get(i), dst.pins.get(j));
                }
                return;
        }
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
                                if (part.isSelected()) {
                                    Part newPart = part.convert();
                                    newPart.setSelected(true);
                                    circuit.parts.set(circuit.parts.indexOf(part), newPart);

                                }
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
                                    if (part.isSelected()) {
                                        circuit.parts.set(circuit.parts.indexOf(part), part.asTech(tech));
                                    }
                                }
                                display.repaint();
                            });
                        }
                    });
                }
                menu.add(new JMenuItem("Reverse Polarity") {
                    {
                        addActionListener(e -> {
                            for (Part part : circuit.parts) {
                                if (part.isSelected()) {
                                    Part newPart = part.reversePolarity();
                                    newPart.setSelected(true);
                                    circuit.parts.set(circuit.parts.indexOf(part), newPart);
                                }
                            }
                            display.repaint();
                        });
                    }
                });
                menu.show(this, e.getX(), e.getY());
            }
        }

        // clicking on nothing should deselect everything.
        if (topHit == null) for (Part part : circuit.parts) part.setSelected(part.selecting = false);
        repaint();
        recentMouseEvent = e;
    }

    /**
     * unused event
     *
     * @param e
     */

    @Override
    public void mouseEntered(MouseEvent e) {
        recentMouseEvent = e;
    }

    /**
     * unused event
     *
     * @param e
     */

    @Override
    public void mouseExited(MouseEvent e) {
        recentMouseEvent = e;
    }

    /**
     * For now: There's no ESC functionality, and lassoing always adds to the
     * selection. This might be just fine.
     * <p>
     * Ultimately, we'd like to try it this ways: Press:: - No keys when starting:
     * set selection - Ctrl or Shift: toggle selection, but select again upon drag -
     * Miss: start lasso Release:: - Add lasso to selection Drag:: - If Lasso --
     * Shift or Ctrl+Shift: toggle selection -- Ctrl: add to selection -- No keys
     * when starting: set selection - Else if hit when starting -- If nothing hit is
     * selected, then select it -- If ctrl is pressed create a selected copy, and
     * unselect original -- Move, if shift then constrained -- If ESC return to
     * position
     */
    @Override
    public void mousePressed(MouseEvent e) {

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
            // if there's no modifier keys, clear the selection
            if (!e.isAltDown() && !e.isControlDown() && !e.isShiftDown()) {
                for (Part part : circuit.parts) part.setSelected(false);
            }

            // for no hits, begin lasso
            lassoBegin = new Point2D.Double(e.getX(), e.getY());
            lasso = new Rectangle2D.Double(e.getX(), e.getY(), 0, 0);
        }
        repaint();
        recentMouseEvent = e;
    }

    void addOrRemoveWire(Pin src, Pin dst) {
        Wire oldWire = Net.findWire(src, dst);
        if (oldWire != null) {
            Log.println("FOUND " + oldWire + oldWire.src + oldWire.dst);
            Net.disconnect(oldWire);
            circuit.wires.remove(oldWire);
        } else {
            circuit.wires.add(new Wire(src, dst));
        }
    }

    /**
     * Handle releasing of the mouse button.
     * Complete wire connection if one is started.
     * Otherwise complete the lasso selection if lassoing
     * Otherwise remove the selected parts if dropped into a parts bin
     *
     * @param e
     */
    @Override
    public void mouseReleased(MouseEvent e) {
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
                        addOrRemoveWire(recentSrc = protoWire.src, recentDst = pin);
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
                circuit.removeSelectedParts();
                break;
            }
        }
        repaint();
        recentMouseEvent = e;
    }


    /**
     * Handle mouse dragging:
     * wiring connections, lassoing parts, moving or copying parts.
     *
     * @param e
     */
    @Override
    public void mouseDragged(MouseEvent e) {
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

    /**
     * unused event
     *
     * @param e
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        recentMouseEvent = e;
    }

    static String appendLogicToFilename(String filename) {
        if (filename.matches(".*\\.(logic)?|\".*\"")) return filename;
        return filename + ".logic";
    }

}
