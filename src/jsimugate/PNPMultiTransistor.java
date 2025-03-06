package jsimugate;

import java.awt.*;
import java.awt.geom.GeneralPath;

/**
 * Implementation of TTL with a PNP Multi-Emitter Transistor (MET). 
 * The emitters are input pins, and the of the exposed collector is an output.
 * If the combined emitters are all higher than the base or not driven, 
 * then its output is delivered to the collector,
 * otherwise the collector is not driven. 
 */
public class PNPMultiTransistor extends PNPTransistor  {

	{
    	this.color = Signal._H.fgColor;
		morphName = PNPTransistor.class.getSimpleName();
		opposite = NPNMultiTransistor.class.getSimpleName();
	}
	public PNPMultiTransistor(){
		super();
		increase();
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
