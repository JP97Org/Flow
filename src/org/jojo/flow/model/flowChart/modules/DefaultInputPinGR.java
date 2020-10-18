package org.jojo.flow.model.flowChart.modules;

import java.awt.Point;

import org.jojo.flow.model.api.IDefaultInputPinGR;
import org.jojo.flow.model.api.PinOrientation;

public class DefaultInputPinGR extends ModulePinGR implements IDefaultInputPinGR {
    public DefaultInputPinGR(final Point position, final String iconText,
            final int height, final int width) {
        super(position, iconText, height, width);
        setPinOrientation(PinOrientation.LEFT);
    }
}
