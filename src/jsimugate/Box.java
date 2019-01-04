package jsimugate;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class Box extends Part {
    int height=4,width=4;

    PinGroup ePins=new PinGroup();
    PinGroup sPins=new PinGroup();
    PinGroup wPins=new PinGroup();
    PinGroup nPins=new PinGroup();


    public Box() {
        resize(4,4);
        int[] a={1,2,3};

    }

    /**
     * Derived classes are could be created like new Box().resize(10,20);
     * @param w width to accommodate w pins
     * @param h height to accommodate h pins
     * @return
     */
    public Box resize(int w,int h) {
        w++;h++;
        setShape(new Rectangle2D.Double(-10*w,-10*h,20*w,20*h));
        return this;
    }

    /**
     * Resize to accommodate the pins list.
     * @return
     */
    public Box resize() {
        int w = Math.max(nPins.size(),sPins.size());
        int h = Math.max(ePins.size(),wPins.size());
        return resize(w,h);
    }
    /**
     * Resize to accommodate the pins list.
     * @return
     */
    public Box resizeWithPadding(int horizontal_pad,int vertical_pad) {
        int w = Math.max(nPins.size(),sPins.size());
        int h = Math.max(ePins.size(),wPins.size());
        return resize(w+horizontal_pad,h+vertical_pad);
    }
}
