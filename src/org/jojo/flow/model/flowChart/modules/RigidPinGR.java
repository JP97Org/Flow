package org.jojo.flow.model.flowChart.modules;

import java.awt.Point;

import org.jojo.flow.model.flowChart.FlowChartGR;

public class RigidPinGR extends ModulePinGR {
    public RigidPinGR(final Point position, final FlowChartGR flowChartGR,
                final String iconText, final int heigth, final int width) {
        super(position, flowChartGR, iconText, heigth, width);
    }
}
