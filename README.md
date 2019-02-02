# jsimugate

Here's a digital logic circuit simulator I just threw together the last couple of weeks. 
It is intended to be used for teaching and learning computer arcitecture.

![image](https://user-images.githubusercontent.com/26174810/51083679-11d2b980-16c2-11e9-988f-6ef0710f2336.png)

Components are idealized,using augmented std_logic signals so a few details are unrealistic
such as unbounded fanout, diodes having an unrealistic memory capability when connected in a loop, and there is
a single time-unit delay for all parts without regard to their internal complexity. 

A wide range of parts are supported from ALU & RAM for building processors down to transistors and diodes for building gates and PLAs.
The agumented (extra weaker values added to) std_logic is specifically for demonstrating PLA design, as it requires a strong pullup 
and a weak pulldown, and it was considered best not to alter the begavior of std_logic values to make H stronger than L. 
So "Yes, No,and Maybe" values have been added as weaker variations of  "High, Low, and Weak".

To create a part, drag it from its parts bin. To recycle it, drag selected parts back to any bin. To add or remove inversion bubbles,
click where they belong. To increase or decrease a selected part (the number of pins or speed of the clock), type + or -. 
You can also increase or decrease the size in which a part is drawn by holding down Alt while typing.

Drag from pin to pin to connect or disconnect a wire between them.

A range of wires can be routed together. Simply route the first wire, then either
* triple-click to route the remaining wires or
* double-click another pin to route wires up to that pin

Right-click a part to apply DeMorgan's theorem on it, or to switch to open collector or other technology type.

Hold ctrl while moving selected parts to duplicate them. When storing a circuit, highlight what part of the circuit you want to store
before storing to a file, and when you load back to the file, the current circuit will remain
while the loaded circuit will be highlighted so you can move it where you want it. 

Double-click a ROM to modify the program. Addresses and data are all hexadecimal.
Square brackets surrounding address values distinguish addresses from data.

Zooming and panning works naturally with a space mouse. 
Just push in the direction that you want the viewer to move in three dimensions. 

You can also zoom and out in by holding the Ctrl key while pressing +/-, or while scrolling the mouse wheel.
Zooming is centered around the mouse, so panning can be done by zooming.
To pan, zoom out with the mouse on one side of the screen, and zoom back in with the mouse on the opposite side of the screen.

Simugate, written in Python served as a prototype for jSimugate.

Visit the Wiki at https://github.com/innovation-plantation/jsimugate/wiki

or download and run at https://github.com/innovation-plantation/jsimugate/raw/master/jsimugate.jar
