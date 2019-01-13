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

Right-click a part to apply DeMorgan's theorem on it, or to switch to open collector or other technology type.

Hold ctrl while moving selected parts to duplicate them. When storing a circuit, highlight what part of the circuit you want to store
before storing to a file, and when you load back to the file, the current circuit will remain
while the loaded circuit will be highlighted so you can move it where you want it. 

One feature to be added is a quick method for connecting busses. 
There is also no panning or zooming of the display yet.
There is also no input/out device and no ROM or method of programming ROMs yet.
If you need the missing features now, you can use simugate instead of jsimugate.
simugate is the puthon version of the program.
But be warned: The two will not have compatible formats for saving parts, though
they are both text formats so it should be possible to convert by hand if absolutely necessary. 
Also be aware that simugate is much slower than jsimugate for larger circuits, and the GUI is quite different. 
In essence, simugate served as a prototype for jsimugate.

Visit the Wiki at https://github.com/innovation-plantation/jsimugate/wiki

or download and run at https://github.com/innovation-plantation/jsimugate/raw/master/jsimugate.jar
