package jsimugate;

import java.awt.*;
import java.awt.geom.GeneralPath;

/**
 * Implementation of TTL with an NPN Multi-Emitter Transistor (MET). 
 * The emitters are input pins, and the exposed collector is an output.
 * If the combined emitters are all lower than the base or not driven, 
 * then its output is delivered to the collector,
 * otherwise the collector is not driven.
 */
public class NPNMultiTransistor extends NPNTransistor  {
    {
    	color = Signal._L.fgColor;
		morphName = NPNTransistor.class.getSimpleName();
		opposite = PNPMultiTransistor.class.getSimpleName();
	}
	public NPNMultiTransistor() {
		super();
		addInput();
	}
	/**
     * request to decrease the number of pins on the part
     */
	public void decrease() {
		if (emitters.pins.size()>1) removeInput(); 
		else morph().removeInput();
	}
	/**
     * request to increase the number of pins on the part
     */
	public void increase() {
		addInput();
	}

}
