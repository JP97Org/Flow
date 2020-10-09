package org.jojo.flow.model.flowChart.modules;

import java.awt.Point;

public class StdOutputPinGR extends ModulePinGR {
    public StdOutputPinGR(final Point position, final String iconText, final int height, final int width) {
        super(position, iconText, height, width);
        setPinOrientation(PinOrientation.RIGHT);
    }
}
