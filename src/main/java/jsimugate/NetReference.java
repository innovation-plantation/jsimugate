package jsimugate;

import javax.swing.*;
import java.util.*;

public class NetReference extends Part {

    static Map<String,Set<Pin>> refs= new TreeMap<String,Set<Pin>>();

    /**
     * This creates an invisible wire to other net reference point that have the same label
    */

    public NetReference() {
        name = "REF";
        label = "";
        this.labelOffset = 20;
        this.setShape(Artwork.ReferenceShape());
        this.hitbox = this.shape.getBounds();
        this.addPin(new Pin(0,0));
    }

    /**
     * When the label changes to the new name, the wire is associated with all other points that have the same new name.
     * @param newName
     */
    public void rename(String newName) {
        // Remove wire routing from other NetReference nodes
        // Add routing to all other NetReference nodes with the same name

        // It's not necessary to connect all pins to one another, as long as there's at least one path
        // but since it's quick and easy to code, do it and optimize later if it's slow.
        // The potential future optimization would need to ensure that all pins in the set are connected somehow,
        // though not via all possible paths.

        // first disconnect all invisible wires from this pin
        if (!label.equals("")) {
            System.out.println("OLD LABEL"+label);
            Circuit.circuit.removeHiddenWiresFromPin(pins.get(0));
            refs.get(label).remove(pins.get(0));
        }
        this.label = newName;
        if (label.equals("")) return;

        // next connect this pin to all in set with new name
        // and add to set with this name

        //  if there's none, create one with this pin only
        if (!refs.containsKey(newName)) {
            refs.put(newName,new HashSet<Pin>());
        }
        // otherwise, connect this to all pins in the set
        else for (Pin pin : refs.get(newName)) Circuit.circuit.wires.add(new Wire(pin, pins.get(0)).asHidden());

        // and add it to the set
        refs.get(newName).add(pins.get(0));

        System.out.println(refs.size()+" "+newName+" "+refs.get(newName).size());

    }

    /**
     * Upon double-clicking, accept new text for the label
     */
    public void processDoubleClick() {
        String newLabel = JOptionPane.showInputDialog(null, "Enter new reference label:",
                "Reference Label", 1);

        if (newLabel == null || newLabel.matches(" *")) return;
        rename(newLabel);

    }

    /**
     * Override setDetails serialize extra information
     * beyond the transform, type, pins, and technology.
     * @param details
     */
    public void setDetails(String details) {
        rename(details);
    }

    /**
     * Override getDetails deserialize extra information
     * beyond the transform, type, pins, and technology.
     */
    public String getDetails() {
        return label;
    }

    public void operate() {
        super.operate();
        setColor(pins.get(0).color);
    }
}
