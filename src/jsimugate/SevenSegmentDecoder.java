package jsimugate;

import static jsimugate.Signal._X;

public class SevenSegmentDecoder extends Box {
    final int lookup[] = {63, 6, 91, 79, 102, 109, 125, 7, 127, 111, // 0-9
            119, 124, 57, 94, 121, 113 // a-f
    };

    public SevenSegmentDecoder() {
        label = "7DEC";
        for (int i = 0; i < 4; i++) {
            addPin(wPins.addPinVertically()).left(30).translate(-width - 30, 0);
        }
        resize();
        for (int i = 0; i < 7; i++) {
            addPin(ePins.addPinVertically()).right(30).translate(width + 30, 0);
        }
        resizeWithPadding(2, 0);
    }

    public void operate() {
        if (wPins.goodValue()) {
            int value = wPins.getValue();
            ePins.setValue(lookup[value]);
        } else {
            for (int i = 0; i < 7; i++) {
                ePins.pins.get(i).setOutValue(_X);
            }
        }
    }
}
