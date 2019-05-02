package jsimugate;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import static jsimugate.Signal.*;


public class PortServer extends Box {
    Thread thread;
    Socket sock;
    ServerSocket ss;
    InputStream is;
    OutputStream os;
    int port;
    boolean acquiring = false;
    Exception prevException;
    Pin rx, tx, rdyPin;
    Signal oldRx=_U,oldTx=_U;

    private boolean logException(Exception e) {
        if (prevException.equals(e)) return false;
        prevException = e;
        System.err.println(e.getMessage());
        return false;
    }

    private boolean portIsOpen() {
        if (port == 0 || acquiring) return false;
        if (ss == null) {
            try {
                ss = new ServerSocket(port);
            } catch (IOException e) {
                return logException(e);
            }
        }
        if (ss.getLocalPort() != port) { /* change port */
            try {
                if (sock != null) sock.close();
            } catch (IOException e) {
                logException(e);
            } finally {
                sock = null;
            }
            try {
                if (ss != null) ss.close();
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
        if (sock == null) return false;
        if (!sock.isConnected()) {
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

    private boolean readyToRead() {
        if (!portIsOpen()) return false;
        if (is == null) try {
            is = sock.getInputStream();
        } catch (IOException e) {
            logException(e);
            return false;
        }
        try {
            if (is.available() == 0 ) return false;
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
            return is.read();
        } catch (IOException e) {
            logException(e);
        }
        return null;
    }

    private void write(int value) {
        if (!readyToWrite()) return;
        try {
            os.write(value);
        } catch (IOException e) {
            logException(e);
        }
    }

    public PortServer() {
        super();
        this.label="TCP/IP";
        this.port = 0;
        rdyPin = addPinN();
        rx = addPinS();
        tx = addPinS();
        resize();
        addPinsWE(8);
        resize();
    }

    /**
     * Upon double-clicking, accept new port for the device
     * This device listens as a server if the input is of the form of a simple port number in the range 0-65535.
     * ToDo: if input is of the form time.nist.gov:13 then parse it and open a client socket.
     *
     * The bytes on the left pins are strobed into the part with the bottom left control signal.
     * The bytes on the right pins are strobed out of the part with the bottom right control signal.
     * The top control signal indicates when data is ready to be strobed out.
     */
    public void processDoubleClick() {
        String newPort = JOptionPane.showInputDialog(null, "Enter new port:",
                "Port number", 1);

        if (newPort == null || !newPort.matches("[0-9]{1,5}")) return;
        int newPortNumber = Integer.parseInt(newPort);
        if (newPortNumber > 65535) return;
        port = newPortNumber;
        label = "Port "+newPort;
    }


    public String getDetails() {
        return Integer.toString(port);
    }

    public void setDetails(String details) {
        Scanner scan = new Scanner(details);
        port = scan.nextInt();
        label = (port==0?"TCP/IP":details);
    }


    public void operate() {
        Signal newRx=rx.getInValue();
        Signal newTx=tx.getInValue();
        boolean avail = readyToRead();
        rdyPin.setOutValue(avail?_1:_0);
        if (oldRx.lo && newRx.hi) {
            Integer value = read();
            if (value != null) ePins.setValue(value);
            else ePins.setValue(0);
        }
        if (oldTx.lo && newTx.hi) {
            int value=wPins.getValue();
            write(value);
        }
        oldRx=newRx;
        oldTx=newTx;
    }

}
