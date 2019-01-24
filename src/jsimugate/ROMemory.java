package jsimugate;

import javax.swing.*;
import java.awt.*;
import java.util.Scanner;

import static jsimugate.Signal._X;
import static jsimugate.Signal._Z;

public class ROMemory extends Box {
    Pin wClkIn, rdEnaIn;
    private Integer[] qSave = new Integer[256];
    Signal prevClk = _X;


    /**
     * Construct a new ROM part. Label, pins created here.
     */
    public ROMemory() {
        name = "ROM";
        for (int i = 0; i < 8; i++) {
            addPin(wPins.addPinVertically()).left(30).translate(-width - 30, 0);
            addPin(ePins.addPinVertically()).right(30).translate(width + 30, 0);
        }
        resize();
        rdEnaIn = addPin(sPins.addPinHorizontally()).down(30).translate(0, height + 30);
        resize();
    }
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
        g.drawString("R",  - 5, height - 5);
        g.drawString("A", -width + 5, 5);
        g.drawString("D", width - 15, 5);
        g.drawString("ROM", -15, -15);
    }

    /**
     * Operate the the memory interface to the I/O pins. This gets called continuously at regular intervals.
     */
    public void operate() {
        Signal rd = rdEnaIn.getInValue();
        int sel = wPins.getValue();
        boolean selValid = wPins.goodValue();
        if (rd.hi) {
            Integer value = qSave[sel];
            if (selValid && value != null) ePins.setValue(value);
            else for (int i = 0; i < 8; i++) ePins.pins.get(i).setOutValue(_X);
        } else for (int i = 0; i < 8; i++) ePins.pins.get(i).setOutValue(_Z);
    }
    /**
     * deserialize
     *
     * @param details formatted like [addr] xx xx xx
     */
    public void setDetails(String details) {
        Scanner scan = new Scanner(details);
        int addr = 0;
        while (scan.hasNext()) {
            if (scan.hasNextInt(16) ) {
                scan.findInLine(" *\\ *([0-9A-Fa-f][0-9A-Fa-f]?) *");
                int data = Integer.parseInt(scan.match().group(1), 16);
                System.out.println("Addr: " + addr + "  data:" + data);
                try {
                    qSave[addr] = data;
                    addr++;
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.err.println("ROM Address out of bounds");
                }
            } else {
                if (scan.findInLine(" *\\[ *([0-9A-Fa-f]+) *\\]")!=null) {
                    addr = Integer.parseInt(scan.match().group(1), 16);
                    System.out.println("ADDR:" + addr);
                }
            }
        }
        System.out.println();
    }

    /**
     * deserialize
     *
     * @return details formatted like 0Hz or 0Sec if value>1
     */
    public String getDetails() {
        boolean needAddr=true;
        String result="";
        for (int addr=0;addr<0x100;addr++) {
            if (qSave[addr]==null) needAddr=true;
            else {
                if (needAddr) {
                    result += " ["+Integer.toHexString(addr)+"]";
                    needAddr = false;
                }
                result += " " + Integer.toHexString(qSave[addr]);
            }
        }
        return result;
    }

    /**
     * Edit the ROM program on double click
     */
    public void processDoubleClick(){
        JTextArea textarea = new JTextArea(10,20);
        JPanel panel = new JPanel();
        panel.add(textarea);
        textarea.setText(getDetails().replaceAll("\\[","\n\\["));
        if (JOptionPane.showConfirmDialog(null, panel, "ROM Program",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE)!=0) return;
        String newProgram = textarea.getText();
        if (newProgram==null) return;
        setDetails(newProgram.replaceAll("\n"," "));
    }
}
