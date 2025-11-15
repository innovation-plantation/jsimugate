package jsimugate;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.regex.MatchResult;

import static jsimugate.Signal.*;

public class PortServer extends Box {
    Thread thread;
    Socket sock;
    ServerSocket ss;
    InputStream is;
    OutputStream os;
    int port;
    boolean acquiring = false;
    boolean lost = true;  // TODO: This should go true when connection is lost. Fix it.
    Exception prevException;
    Pin rx, tx, connPin, lostPin, rdyPin;
    Signal oldRx = _U, oldTx = _U, oldConn = _U;
    String destAddr = null;
    private String host = null;
    private String portLabel = null;

    private boolean logException(Exception e) {
        if (e.equals(prevException)) return false;
        prevException = e;
        System.err.println(e.getMessage());
        return false;
    }
    final long SECOND = 1000000000;
    long MIN_TIMEOUT = SECOND*5;  // less than 5 seconds gets you kicked out of https://tf.nist.gov/tf-cgi/servers.cgi
    long MAX_TIMEOUT = SECOND*20; // time-a-wwv.nist.gov:13
    long timeout = MIN_TIMEOUT;
    long expiration=0;

    void setExpiration() {
        expiration = System.nanoTime()+timeout;
    }
    boolean expired() {
        return System.nanoTime() > expiration;
    }
    void increaseTimeout() {
        timeout <<=1;
        if (timeout > MAX_TIMEOUT) timeout=MAX_TIMEOUT;
    }
    void  decreaseTimeout() {
        timeout >>=1;
        if (timeout < MIN_TIMEOUT) timeout=MIN_TIMEOUT ;
    }

    /**
     * Intended that this be called upon failure that needs increasing timeouts,
     * returns true if need to wait longer for timeout. Increases timeout for next time if expired.
     */
    boolean failDelay() {
        if (!expired()) return true;
        increaseTimeout();
        return false;
    }
    private boolean portIsOpen() {
        if (port == 0 || acquiring) return false;
        if (host == null) {
            if (ss == null) {
                if (lost) return false;
                try {
                    ss = new ServerSocket(port);
                } catch (IOException e) {
                    setLost(true);
                    return logException(e);
                }
            }
            if (ss.getLocalPort() != port) { /* change port */
                try {
                    if (sock != null) {
                        sock.close();
                    }
                } catch (IOException e) {
                    logException(e);
                } finally {
                    sock = null;
                    setLost(true);
                }
                try {
                    if (ss != null)  {
                        ss.close();
                        setLost(true);
                    }
                } catch (IOException e) {
                    logException(e);
                } finally {
                    ss = null;
                }
                return false;
            }
            if (acquiring) return false;
            if (sock == null) {
                is = null;
                Thread thread = new Thread(() -> {
                    acquiring = true;
                    try {
                        sock = ss.accept();
                    } catch (IOException e) {
                        logException(e);
                    } finally {
                        acquiring = false;
                    }
                });
                thread.start();
            }
        } else { // client
            if (sock == null) {
                if (lost || failDelay()) return false;
                try {
                    sock = new Socket(host, port);
                } catch (IOException e) {
                    logException(e);
                }
                ;
            }
        }
        if (sock == null) return false;
        if (!sock.isConnected()) {
            setLost(true);
            try {
                sock.close();
            } catch (IOException e) {
                logException(e);
            }
            sock = null;
            return false;
        }
        return true;
    }
    private void setLost(boolean newValue){
        lost = newValue;
        lostPin.setOutValue(lost?_1:_0);
    }
    private boolean readyToRead() {
        if (!portIsOpen()) return false;
        if (is == null) try {
            is = sock.getInputStream();
        } catch (IOException e) {
            logException(e);
            return false;
        }
        try {
            if (is.available() == 0) return false;
        } catch (IOException e) {
            logException(e);
            return false;
        }
        return true;
    }

    private boolean readyToWrite() {
        if (!portIsOpen()) return false;
        if (os == null) try {
            os = sock.getOutputStream();
        } catch (IOException e) {
            logException(e);
            return false;
        }
        return true;
    }

    private Integer read() {
        if (!readyToRead()) return null;
        try {
            decreaseTimeout();
            return is.read();
        } catch (IOException e) {
            logException(e);
        }
        return null;
    }

    private void write(int value) {
        if (!readyToWrite()) return;
        try {
            decreaseTimeout();
            os.write(value);
        } catch (IOException e) {
            logException(e);
        }
    }

    public PortServer() {
        super();
        rdyPin = addPinN();
        lostPin = addPinN();
        rx = addPinS();
        connPin = addPinS();
        tx = addPinS();
        resize();
        addPinsWE(8);
        resize();
        this.setDetails("0");
    }

    /**
     * Upon double-clicking, accept new port for the device
     * This device listens as a server if the input is of the form of a simple port number in the range 0-65535.
     * ToDo: if input is of the form time.nist.gov:13 then parse it and open a client socket.
     * <p>
     * The bytes on the left pins are strobed into the part with the bottom left control signal.
     * The bytes on the right pins are strobed out of the part with the bottom right control signal.
     * The top control signal indicates when data is ready to be strobed out.
     */
    public void processDoubleClick() {
        String newPort = JOptionPane.showInputDialog(null, "Enter new port:",
                "Port number", 1);
        updateConnection(newPort);
    }


    private void updateConnection(String newPort) {
        if (newPort == null) return;
        Scanner scan = new Scanner(newPort);
        int newPortNumber = 0;
        String destAddr = null;
        if (newPort.matches("[0-9]{1,5}")) {
            newPortNumber = Integer.parseInt(newPort);
        } else if (newPort.matches(".*:[0-9]{1,5}")) {
            scan.findInLine("(.*):([0-9]{1,5})");
            MatchResult result = scan.match();
            destAddr = result.group(1);
            newPortNumber = Integer.parseInt(result.group(2));
        } else return;
        System.out.println("New Port Number");
        System.out.println(newPortNumber);
        if (newPortNumber > 65535) return;
        port = newPortNumber;
        portLabel = newPort;
        if (port == 0) {
            label = "TCP/IP";
            host = null;
            return;
        }
        label = "";
        host = destAddr;
    }


    public void operate() {
        Signal newRx = rx.getInValue();
        Signal newTx = tx.getInValue();
        Signal newConn = connPin.getInValue();
        boolean avail = readyToRead();
        rdyPin.setOutValue(avail ? _1 : _0);
        lostPin.setOutValue(lost ? _1:_0);
        if (oldRx.lo && newRx.hi) {
            Integer value = read();
            if (value != null) ePins.setValue(value);
            else ePins.setValue(0);
        }
        if (oldTx.lo && newTx.hi) {
            int value = wPins.getValue();
            write(value);
        }
        if (oldConn.lo && newConn.hi) {
            setLost(false);
        }
        oldRx = newRx;
        oldTx = newTx;
        oldConn = newConn;
    }

    public void drawAtOrigin(java.awt.Graphics2D g) {
        super.drawAtOrigin(g);
        g.drawString("LOST", -35, -75);
        g.drawString("AVAIL", 5, -75);
        g.drawString("SES", -12, 78);
        g.drawString("TX", -30, 78);
        g.drawString("RX", 15, 78);
        g.rotate(Math.PI);
        g.drawString("V", 17, -82);
        g.drawString("V", -3, -82);
        g.drawString("V", -23, -82);
        g.rotate(-Math.PI * .5);
        if (port == 0) {
            g.drawString("TCP/IP Port", -50, 0);
            return;
        }
        g.drawString(host != null ? "TCP/IP Host Port" : "TCP/IP Client Port", -50, 0);
        g.drawString(portLabel, -50, 15);
        if (sock!=null) {
            g.drawString("= "+sock.getRemoteSocketAddress().toString(),-50,25);
        }
    }
}
