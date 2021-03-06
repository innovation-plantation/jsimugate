package jsimugate;

import javax.swing.*;
import java.awt.*;
import java.util.Scanner;
import java.util.concurrent.ConcurrentSkipListMap;

import static jsimugate.Signal._X;
import static jsimugate.Signal._Z;

public class ROMemory extends Box {
    Pin wClkIn, rdEnaIn;
    private ConcurrentSkipListMap<Long, Integer> qSave = new ConcurrentSkipListMap<Long, Integer>();
    Signal prevClk = _X;


    /**
     * Construct a new ROM part. Label, pins created here.
     */
    public ROMemory() {
        name = "ROM";
        resize(1, 8);
        rdEnaIn = addPinS();
        addPinsE(8);
        addPinsW(8);
    }

    /**
     * Allow the part to be constructed with pre-programmed ROM data
     * @param data
     */
    public ROMemory(String data) {
        this();
        this.setDetails(data);
    }

    /**
     * Add RAM-specific pin labeling to the part
     *
     * @param g graphics context for drawing
     */
    public void drawAtOrigin(Graphics2D g) {
        super.drawAtOrigin(g);
        g.drawString("R", -5, height - 5);
        g.drawString("A", -width + 5, 5);
        g.drawString("D", width - 15, 5);
        g.drawString("ROM", -15, -15);
    }

    /**
     * Operate the the memory interface to the I/O pins. This gets called continuously at regular intervals.
     */
    public void operate() {
        Signal rd = rdEnaIn.getInValue();
        Long sel = wPins.getLongValue();
        boolean selValid = wPins.goodValue();
        if (rd.hi) {
            Integer value = qSave.get(sel);
            if (selValid) {
                if (value != null) ePins.setValue(value);
                else for (int i = 0; i < 8; i++) ePins.pins.get(i).setOutValue(Signal._U); // read uninitialized rom
            } else for (int i = 0; i < 8; i++) ePins.pins.get(i).setOutValue(Signal._X);
        } else for (int i = 0; i < 8; i++) ePins.pins.get(i).setOutValue(_Z);
    }

    /**
     * deserialize
     *
     * @param details formatted like [addr] xx xx xx
     */
    public void setDetails(String details) {
        Scanner scan = new Scanner(details);
        long addr = 0;
        while (scan.hasNext()) {
            if (scan.hasNextInt(16)) {
                scan.findInLine(" *\\ *([0-9A-Fa-f][0-9A-Fa-f]?) *");
                int data = Integer.parseInt(scan.match().group(1), 16);
                Log.println("Addr: " + addr + "  data:" + data);
                qSave.put(addr, data);
                addr++;
            } else {
                if (scan.findInLine(" *\\[ *([0-9A-Fa-f]+) *\\]") != null) {
                    addr = Long.parseUnsignedLong(scan.match().group(1), 16);
                    Log.println("ADDR:" + addr);
                } else {
                    Log.println("Goofy ROM data at " + addr + ": " + scan.next());
                }
            }
        }
        Log.println();
    }

    /**
     * deserialize
     *
     * @return details formatted like [f0] de ad be ef ca fe
     */
    public String getDetails() {
        Long nextAddr = null;
        String result = "";
        for (long addr : qSave.keySet()) {
            if (qSave.get(addr) != null) {
                if (nextAddr == null || addr != nextAddr || (nextAddr & 0xF) == 0) {
                    result += " [" + Long.toHexString(addr) + "]";
                }
                result += " " + Integer.toHexString(qSave.get(addr));
                nextAddr = addr + 1L;
            }
        }
        return result;
    }

    /**
     * Edit the ROM program on double click
     */
    public void processDoubleClick() {
        JTextArea textarea = new JTextArea(getDetails().replaceAll("\\[", "\n\\[") + "       \n\n\n\n");
        JPanel panel = new JPanel();
        JScrollPane scroller = new JScrollPane(textarea);
        panel.add(scroller);
        textarea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        if (JOptionPane.showConfirmDialog(null, panel, "ROM Program",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != 0) return;

        String newProgram = textarea.getText();
        if (newProgram == null) return;
        qSave.clear();
        setDetails(newProgram.replaceAll("\n", " "));
    }

    /**
     * Grow the address bus
     */
    public void increase() {
        if (wPins.size() < 63) addPinW();
        resize();
    }

    /**
     * Shrink the address bus
     */
    public void decrease() {
        removePin(wPins.removePinVertically());
        resize();
    }
}
