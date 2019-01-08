# jsimugate

Here's a digital logic circuit simulator I just threw together the last couple of weeks. 
It is intended to be used for teaching and learning computer arcitecture.

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

Hold ctrl while moving selected parts to duplicate them. When storing a circuit, highlight what part of the circuit you want to store
before storing to a file, and when you load back to the file, the current circuit will remain while the loaded circuit will be highlighted
so you can move it where you want it. 

There is no panning or zooming of the display yet, so be careful to not drop parts beyond the limits of the window, 
or you may not see them again. There is also no input/out device and no ROM or method of programming ROMs yet, so if you need that
capability now, use simugate instead of jsimugate, but be warned: The two will not have compatible formats for saving parts, and 
simugate is much slower than jsimugate for larger circuits. 

Anbother feature I hope to add is a quick method for connecting busses. 
