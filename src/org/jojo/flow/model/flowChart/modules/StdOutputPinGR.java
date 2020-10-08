package org.jojo.flow.model.flowChart.modules;

import java.awt.Point;

import org.jojo.flow.model.flowChart.FlowChartGR;

public class StdOutputPinGR extends ModulePinGR {
    public StdOutputPinGR(final Point position, 
            final FlowChartGR flowChartGR, final String iconText, final int heigth, final int width) {
        super(position, flowChartGR, iconText, heigth, width);
        setPinOrientation(PinOrientation.RIGHT);
    }
}
