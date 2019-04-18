package jsimugate;

import jsimugate.Part.Tech;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Scanner;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.awt.Toolkit.getDefaultToolkit;
import static java.awt.event.KeyEvent.*;

/**
 * User interface for circuit simulation. This could be an Applet by changing JPanel to JApplet or Applet, etc.
 */
public class JSimuGate extends Panel implements MouseListener, MouseMotionListener, ComponentListener {
    static String version = "jSimuGate 0.86";
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
    static File savedFile;
    static File otherFile;
    static JFrame frame = new JFrame(version);
    static final AffineTransform identity = new AffineTransform();
    static double scaleUnit = Math.sqrt(Math.sqrt(Math.sqrt(2))), inverseScaleUnit = 1 / scaleUnit;
    static JMenuItem saveMenuItem = null;
    Stack<String> undoStack = new Stack<String>();
    Stack<String> redoStack = new Stack<String>();

    public void snapshot() {
        String s = circuit.toString();
        if (undoStack.empty() || !undoStack.peek().equals(s)) {
            redoStack.empty();
            undoStack.push(s);
        }
    }

    public void undo() {
        System.gc();
        Log.println("u:"+undoStack.size()+" R:"+redoStack.size()+"...");
        String s1 = circuit.toString();
        redoStack.push(s1);
        String s="";
        for (;;) {
            s = "";
            if (undoStack.isEmpty()) break;
            s = undoStack.pop();
            if (!s.equals(s1)) break;
        }
        if (s1.equals(s)) return;
        Log.println("u:"+undoStack.size()+" R:"+redoStack.size());
        PinGroup.pinGroups.clear();
        circuit.shutdown();
        circuit = new Circuit().withStandardBins();
        circuit.startup(false,() -> repaint());
        circuit.fromString(s);
        repaint();
        System.gc();
    }

    public void redo() {
        System.gc();
        Log.println("r:"+redoStack.size()+" u:"+undoStack.size()+"...");
        String s0 = circuit.toString();
        undoStack.push(s0);
        String s="";
        for (;;) {
            if (redoStack.isEmpty()) return;
            s = redoStack.pop();
            if (!s.equals(s0)) break;
        }
        if (s0.equals(s)) return;
        Log.println("r:"+redoStack.size()+" u:"+undoStack.size());
        PinGroup.pinGroups.clear();
        circuit = new Circuit().withStandardBins();
        circuit.startup(false,() -> repaint());
        circuit.fromString(s);
        repaint();
        System.gc();
    }

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
            } else if (e.isShiftDown()) {
                for (Part part : circuit.parts) {
                    if (part.isSelected()) {
                        for (int i = 0; i < e.getWheelRotation(); i++) part.decrease();
                        for (int j = 0; j > e.getWheelRotation(); j--) part.increase();
                    }
                }
            }
            snapshot();
        });
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            if (!hasFocus()) return false;
            if (e.getID() == KeyEvent.KEY_PRESSED) {
                switch (e.getKeyChar()) {
                    case '+':
                        snapshot();
                        if (e.isControlDown()) {
                            int x = recentMouseEvent.getX(), y = recentMouseEvent.getY();
                            circuit.scale(scaleUnit, x, y);
                            break;
                        }
                        for (Part part : circuit.parts)
                            if (part.isSelected()) {
                                if (e.isAltDown()) part.transform.scale(2, 2);
                                else part.increase();
                            }
                        break;
                    case '-':
                        snapshot();
                        if (e.isControlDown()) {
                            int x = recentMouseEvent.getX(), y = recentMouseEvent.getY();
                            circuit.scale(inverseScaleUnit, x, y);
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
                            case VK_LEFT:
                                snapshot();
                                step = -step; // fall-through
                            case VK_RIGHT:
                                snapshot();
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
                        }
                }
                Log.println("key down " + e.getKeyChar());
                if (!e.isAltDown() && !e.isControlDown()) {
                    snapshot();
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
        circuit.startup(true,() -> repaint()); // repaint this component whenever the circuit is updated
        File spaceNavigator = new File("/dev/hidraw2");
        if (spaceNavigator.exists() && spaceNavigator.canRead()) {
            try {
                FileInputStream stream = new FileInputStream(spaceNavigator);
                byte[] bytes = new byte[7];
                ByteBuffer packet = ByteBuffer.allocateDirect(bytes.length);
                packet.order(ByteOrder.LITTLE_ENDIAN);
                new Thread(() -> {
                    for (; ; ) {
                        try {
                            if (stream.read(bytes) < 0) continue;
                            packet.rewind();
                            packet.put(bytes);
                            packet.rewind();
                            if (packet.get() != 1) continue;
                            int dx = packet.getShort(), dy = packet.getShort(), dz = packet.getShort();
                            System.out.printf("%04d %04d %04d \n", dx, dy, dz);
                            int x = getWidth() / 2, y = getHeight() / 2;
                            circuit.scale(1 + dz * .0001, x, y);
                            circuit.translate(-dx * .05, -dy * .05);
                        } catch (IOException ex) {
                        }
                    }
                }).start();
            } catch (IOException ex) {
            }

        }
    }

    private void flop_parts() {
        snapshot();
        for (Part part : circuit.parts) {
            if (part.isSelected()) {
                part.transform.scale(-1, 1);
            }
        }
    }

    private void flip_parts() {
        snapshot();
        for (Part part : circuit.parts) {
            if (part.isSelected()) {
                part.transform.scale(1, -1);
            }
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
        JMenu editMenu = createEditMenu(panel);
        bar.add(editMenu);
        JMenu simMenu = createSimMenu(panel);
        bar.add(simMenu);
        JMenu helpMenu = createHelpMenu(panel);
        bar.add(helpMenu);
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

    static int nextPasteOffset = 0;

    public static JMenu createEditMenu(JSimuGate panel) {
        JMenu menu = new JMenu("Edit");
        JMenuItem menuItem;

        menuItem = new JMenuItem("Undo");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK));
        menuItem.addActionListener(event -> {
            panel.undo();
            panel.unlselectAll();
        });
        menu.add(menuItem);


        menuItem = new JMenuItem("Cut");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK));
        menuItem.addActionListener(event -> {
            panel.snapshot();
            nextPasteOffset = 0;
            StringSelection text = new StringSelection(panel.copySelection());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(text, text);
            panel.circuit.removeSelectedParts();
            panel.snapshot();
        });
        menu.add(menuItem);


        menuItem = new JMenuItem("Copy");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK));
        menuItem.addActionListener(event -> {
            nextPasteOffset = 1;
            StringSelection text = new StringSelection(panel.copySelection());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(text, text);
        });
        menu.add(menuItem);


        menuItem = new JMenuItem("Paste");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK));
        menuItem.addActionListener(event -> {
            panel.snapshot();
            try {
                Clipboard systemClipboard = getDefaultToolkit().getSystemClipboard();
                Transferable data = systemClipboard.getContents(null);
                String string = (String)data.getTransferData(DataFlavor.stringFlavor);
                string = shiftSerializedPart(string,nextPasteOffset*50,nextPasteOffset*50);
                nextPasteOffset++;
                panel.unlselectAll();
                panel.circuit.fromString(string);
            } catch (IOException | UnsupportedFlavorException e) {}
            panel.snapshot();

        });
        menu.add(menuItem);

        menuItem = new JMenuItem("Delete");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        menuItem.addActionListener(event -> {
            panel.snapshot();
            panel.circuit.removeSelectedParts();
            panel.snapshot();
        });
        menu.add(menuItem);

        menuItem = new JMenuItem("Redo");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyEvent.CTRL_DOWN_MASK));
        menuItem.addActionListener(event -> {
            panel.redo();
            panel.unlselectAll();
        });
        menu.add(menuItem);

        menuItem = new JMenuItem("Flip parts");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0));
        menuItem.addActionListener(event -> {
            panel.flip_parts();
        });
        menu.add(menuItem);

        menuItem = new JMenuItem("Flop parts");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0));
        menuItem.addActionListener(event -> {
            panel.flop_parts();
        });
        menu.add(menuItem);

        menuItem = new JMenuItem("Select All");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_DOWN_MASK));
        menuItem.addActionListener(event -> {
            for (Part part : panel.circuit.parts) part.setSelected(true);
        });
        menu.add(menuItem);


        return menu;
    }

    public static JMenu createSimMenu(JSimuGate panel) {

        JMenu menu = new JMenu("Simulation");
        JMenuItem menuItem;

        menuItem = new JMenuItem("Pause");
        menuItem.addActionListener(event -> {
            panel.circuit.pause();
        });
        menu.add(menuItem);

        menuItem = new JMenuItem("Run");
        menuItem.addActionListener(event -> {
            panel.circuit.resume();
        });
        menu.add(menuItem);

        return menu;
    }

    public static JMenu createHelpMenu(JSimuGate panel) {

        JMenu menu = new JMenu("Help");
        JMenuItem menuItem;

        menuItem = new JMenuItem("About");
        menuItem.addActionListener(event -> {
            JOptionPane.showMessageDialog(panel, version + " by Dr. Ted Shaneyfelt 2019\nhttps://github.com/innovation-plantation/jsimugate");
        });
        menu.add(menuItem);

        menuItem = new JMenuItem("Help");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        menuItem.addActionListener(event -> {
            try {
                Desktop.getDesktop().browse(new URI("https://github.com/innovation-plantation/jsimugate/wiki"));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(panel, version + " Documentation is available online at\nhttps://github.com/innovation-plantation/jsimugate/wiki");
            }
        });
        menu.add(menuItem);

        return menu;
    }

    /**
     * Create the file menu
     *
     * @param panel
     * @return the created menu
     */
    public static JMenu createFileMenu(JSimuGate panel) {
        try {
            savedFile = otherFile = new File(new File("circuit.logic").getCanonicalPath()); // set the default file name and path
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

        JMenu fileMenu = new JMenu("File");
        JMenuItem menuItem;

        menuItem = new JMenuItem("New");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));
        menuItem.addActionListener(event -> {
            panel.snapshot();
            if (0 != JOptionPane.showConfirmDialog(null, "Clear existing circuit?")) return;
            Net.nets.clear();
            PinGroup.pinGroups.clear();
            panel.circuit = new Circuit().withStandardBins();
            panel.circuit.startup(true,() -> panel.repaint());
            panel.snapshot();
        });
        fileMenu.add(menuItem);

        menuItem = new JMenuItem("Load... (add to existing circuit)");
        menuItem.addActionListener(event -> {
            panel.snapshot();
            JFileChooser choice = new JFileChooser(otherFile);
            choice.setSelectedFile(otherFile);
            choice.setFileFilter(new FileNameExtensionFilter("jSimuGate Circuits (.logic)", "logic"));
            if (choice.showOpenDialog(panel) == JFileChooser.APPROVE_OPTION) {
                if (choice.getSelectedFile().exists()) {
                    try {
                        Scanner scan = new Scanner(choice.getSelectedFile(),"utf-8");
                        panel.circuit.fromScanner(scan);
                    } catch (FileNotFoundException ex) {
                        JOptionPane.showMessageDialog(panel, ex.getMessage());
                    }
                } else {
                    JOptionPane.showMessageDialog(panel, "File " + otherFile + " does not exist");
                }
            }
            panel.snapshot();
        });
        fileMenu.add(menuItem);

        menuItem = new JMenuItem("Open... (replace existing circuit)");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
        menuItem.addActionListener(event -> {
            panel.snapshot();
            JFileChooser choice = new JFileChooser(savedFile);
            choice.setSelectedFile(savedFile);
            choice.setFileFilter(new FileNameExtensionFilter("jSimuGate Circuits (.logic)", "logic"));
            if (choice.showOpenDialog(panel) == JFileChooser.APPROVE_OPTION) {
                savedFile = choice.getSelectedFile();
                saveMenuItem.setText("Save " + savedFile.getName());
                if (savedFile.exists()) {
                    try {
                        Scanner scan = new Scanner(choice.getSelectedFile(),"utf-8");
                        Net.nets.clear();
                        PinGroup.pinGroups.clear();
                        panel.circuit = new Circuit().withStandardBins();
                        panel.circuit.fromScanner(scan);
                        panel.unlselectAll();
                        panel.circuit.startup(true,() -> panel.repaint());
                    } catch (FileNotFoundException ex) {
                        JOptionPane.showMessageDialog(panel, ex.getMessage());
                    }
                } else {
                    JOptionPane.showMessageDialog(panel, "File " + savedFile + " does not exist");
                }
            }
            panel.snapshot();
        });
        fileMenu.add(menuItem);

        menuItem = new JMenuItem("Save as...");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.SHIFT_DOWN_MASK|KeyEvent.CTRL_DOWN_MASK));
        menuItem.addActionListener(event -> {
            JFileChooser choice = new JFileChooser(savedFile);
            choice.setSelectedFile(savedFile);
            choice.setFileFilter(new FileNameExtensionFilter("jSimuGate Circuits (.logic)", "logic"));
            if (choice.showSaveDialog(panel) == JFileChooser.APPROVE_OPTION) {

                if (choice.getSelectedFile().exists()) {
                    if (JOptionPane.showConfirmDialog(panel,
                            "Overwrite " + choice.getSelectedFile().getName() + "?") != JOptionPane.YES_OPTION)
                        return;
                }
                savedFile = choice.getSelectedFile();
                String filename = appendLogicToFilename(savedFile.getAbsolutePath());
                savedFile = new File(filename);
                saveMenuItem.setText("Save " + savedFile.getName());
                try {
                    PrintWriter printWriter = new PrintWriter(savedFile, "UTF-8");
                    Numbered.renumber();
                    printWriter.write(panel.circuit.toString());
                    printWriter.close();
                } catch (FileNotFoundException | UnsupportedEncodingException ex) {
                    JOptionPane.showMessageDialog(panel, ex.getMessage());
                }


            }
        });
        fileMenu.add(menuItem);

        menuItem = saveMenuItem = new JMenuItem("Save " + savedFile.getName());
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
        menuItem.addActionListener(event -> {
            String filename = appendLogicToFilename(savedFile.getAbsolutePath());
            savedFile = new File(filename);


            System.out.println("Save" + savedFile.getAbsolutePath());
            try {
                PrintWriter printWriter = new PrintWriter(savedFile, "UTF-8");
                Numbered.renumber();
                printWriter.write(panel.circuit.toString());
                printWriter.close();
            } catch (FileNotFoundException | UnsupportedEncodingException ex) {
                JOptionPane.showMessageDialog(panel, ex.getMessage());
            }


        });
        fileMenu.add(menuItem);

        menuItem = new JMenuItem("Quit");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_DOWN_MASK));
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
        snapshot();
        try {
            switch (e.getClickCount()) {
                case 2:

                    for (Part part : circuit.parts) {
                        for (Pin pin : part.pins) {
                            if (pin.at(e.getPoint())) {
                                if (recentSrc == null || recentDst == null) return;
                                PinGroup src = PinGroup.groupOf(recentSrc);
                                PinGroup dst = PinGroup.groupOf(recentDst);
                                PinGroup target = PinGroup.groupOf(pin);
                                if (target == null) return;
                                int iTarget = target.pins.indexOf(pin);
                                if (src == null && dst == null) return;
                                // if one end is not a group, duplicate its part for each pin in group
                                Part template = null;
                                PinGroup group = null;
                                Pin templatePin = null;
                                if (src == null) {
                                    template = (Part) recentSrc.parent;
                                    group = dst;
                                    templatePin = recentDst;
                                }
                                if (dst == null) {
                                    template = (Part) recentDst.parent;
                                    group = src;
                                    templatePin = recentSrc;
                                }
                                if (template != null) {
                                    if (template.pins.size() != 1) return; // only duplicate single-pin parts.
                                    int i = group.pins.indexOf(templatePin);
                                    for (int j = i + 1; j <= iTarget && j < group.size(); j++) {
                                        Pin jPin = group.pins.get(j);
                                        if (Net.directConnections(jPin).size() > 0) continue;
                                        Part newPart = replicatePart(template, templatePin.gTransform, jPin.gTransform);
                                        addOrRemoveWire(jPin, newPart.pins.get(0));
                                    }
                                    for (int j = i - 1; j >= iTarget && j >= 0; j--) {
                                        Pin jPin = group.pins.get(j);
                                        if (Net.directConnections(jPin).size() > 0) continue;
                                        Part newPart = replicatePart(template, templatePin.gTransform, jPin.gTransform);
                                        addOrRemoveWire(jPin, newPart.pins.get(0));
                                    }
                                    return;
                                }
                                int iSrc = src.pins.indexOf(recentSrc);
                                int iDst = dst.pins.indexOf(recentDst);
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
                    if (src == null && dst == null) return;
                    // if one end is not a group, duplicate its part for each pin in group
                    Part template = null;
                    PinGroup group = null;
                    Pin templatePin = null;
                    if (src == null) {
                        template = (Part) recentSrc.parent;
                        group = dst;
                        templatePin = recentDst;
                    }
                    if (dst == null) {
                        template = (Part) recentDst.parent;
                        group = src;
                        templatePin = recentSrc;
                    }
                    if (template != null) {
                        if (template.pins.size() != 1) return; // only duplicate single-pin parts.
                        for (Pin p : group.pins) {  // wire each pin to corresponding duplicated part
                            if (Net.directConnections(p).size() > 0) continue;
                            addOrRemoveWire(p, replicatePart(template, templatePin.gTransform, p.gTransform).pins.get(0));
                        }
                        return;
                    }
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
                    if (topHit instanceof Gate)
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
                    if (!(topHit instanceof Discrete || topHit instanceof Bus))
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
                    if (topHit instanceof Discrete)
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
        } finally {
            snapshot();
        }
    }

    /**
     * Make a copy of the part offset by dx,dy
     *
     * @param part original part
     * @param dx   distance to move in x direction from original part
     * @param dy   distance to move in y direction from original part
     * @return new part
     */
    private Part replicatePart(Part part, double dx, double dy) {
        // duplicate template
        Part newPart = part.dup(0, 0);
        newPart.transform.setTransform(part.gTransform);
        circuit.parts.add(newPart);
        newPart.transform.preConcatenate(AffineTransform.getTranslateInstance(dx, dy));
        return newPart;
    }

    /**
     * Make a copy of a part, offset by the distance between the transforms a and b
     * e.g. for a pullup connected to one pin to be replicated to the locations of other pins
     * call with a the transform of the one pin, and b for each of the other pins that needs a duplicate pullup.
     *
     * @param part
     * @param a    source transform (typically pin connected to existing part)
     * @param b    dest transform (typically pin to be connected to new part)
     * @return newly created part
     */
    private Part replicatePart(Part part, AffineTransform a, AffineTransform b) {
        // find dx and dy between p and templatePin
        double dx = b.getTranslateX() - a.getTranslateX();
        double dy = b.getTranslateY() - a.getTranslateY();
        return replicatePart(part, dx, dy);
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
            if (e.isShiftDown()) {
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
        snapshot();
        try {
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
                return;
            }

            for (PartsBin bin : circuit.bins) {
                if (bin.at(e.getPoint())) {
                    circuit.removeSelectedParts();
                    break;
                }
            }

        }
        finally {
            snapshot();
            repaint();
            recentMouseEvent = e;
        }
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

                // Begin moving or copying
                // If there's two or more in the same spot, only grab them all if
                // they're already all selected or if SHIFT is down.
                // This makes it more natural to discover when one part is hidden below another.
                if (recentMouseEvent.isShiftDown()) {
                    // Add whatever else is under the mouse to the selection if SHIFT was
                    // pressed
                    for (Part part : circuit.parts) {
                        if (part.at(recentMouseEvent.getPoint())) {
                            part.setSelected(true);
                        }
                    }
                }


                if (recentMouseEvent.isControlDown()) {
                    String string = copySelection();
                    unlselectAll();
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

    private String copySelection() {
        // Copying
        String string = "";
        for (Part part : circuit.parts) {
            if (part.isSelected()) {
                string += part.toString();
                if (recentMouseEvent.isShiftDown()) part.selecting = true;

            }
        }
        for (Wire wire : circuit.wires) string += wire.toString();
        return string;
    }

    private void unlselectAll() {
        for (Part part : circuit.parts) if (part.isSelected())  part.setSelected(false);
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

    /**
     * Takes a string, if it's a serialized part, shifts it by dx,dy
     * @param string - Serialized part string
     * @param dx - Amount to shift horizontally
     * @param dy - Amount to shift vertically
     * @return - String encoded with part shifted
     */
    static String shiftSerializedPart(String string,double dx,double dy) {
        StringBuffer result=new StringBuffer();
        Pattern pattern = Pattern.compile("(PART:\\[\\[[0-9. ]+,[0-9. ]+,)([0-9. ]+)(] *, *\\[[0-9. ]+,[0-9. ]+,)([0-9. ]+)(]])");
        Matcher match = pattern.matcher(string);
        while (match.find()) {
            double x = Double.valueOf(match.group(2)) + dx;
            double y = Double.valueOf(match.group(4)) + dy;
            match.appendReplacement(result, match.group(1)+x+match.group(3)+y+match.group(5));
        }
        match.appendTail(result);
        return result.toString();
    }
}
