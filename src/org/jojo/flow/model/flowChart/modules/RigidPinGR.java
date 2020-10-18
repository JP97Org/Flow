package org.jojo.flow.model.flowChart.modules;

import java.awt.Point;

import org.jojo.flow.model.api.IRigidPinGR;
import org.jojo.flow.model.api.PinOrientation;

public class RigidPinGR extends ModulePinGR implements IRigidPinGR {
    public RigidPinGR(final Point position, final String iconText,
                final int height, final int width) {
        super(position, iconText, height, width);
        setPinOrientation(PinOrientation.DOWN);
    }
}
