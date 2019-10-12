package jsimugate;

import javax.swing.*;
import java.awt.*;

public class NetReference extends Part {

    // TODO: Make a map of string to list of netreferences

    public NetReference() {
        name = "REF";
        label = "";
        this.labelOffset = 20;
        this.setShape(Artwork.ReferenceShape());
        this.hitbox = this.shape.getBounds();
        this.addPin(new Pin(0,0));
    }

    /**
     * Upon double-clicking, accept new text for the label
     */
    public void processDoubleClick() {
        String newLabel = JOptionPane.showInputDialog(null, "Enter new reference label:",
                "Reference Label", 1);

        if (newLabel == null || newLabel.matches(" *")) return;
        label = newLabel;
        //TODO: Wire routing
        // Remove wire routing from other NetReference nodes
        // Add routing to all other NetReference nodes with the same name
    }


}
