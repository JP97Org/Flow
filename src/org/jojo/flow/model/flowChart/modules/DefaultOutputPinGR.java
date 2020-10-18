package org.jojo.flow.model.flowChart.modules;

import java.awt.Point;

import org.jojo.flow.model.api.IDefaultOutputPinGR;
import org.jojo.flow.model.api.PinOrientation;

public class DefaultOutputPinGR extends ModulePinGR implements IDefaultOutputPinGR {
    public DefaultOutputPinGR(final Point position, final String iconText, final int height, final int width) {
        super(position, iconText, height, width);
        setPinOrientation(PinOrientation.RIGHT);
    }
}
