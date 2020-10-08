package org.jojo.flow.model.flowChart.connections;

import java.awt.Point;
import java.util.Objects;

import org.jojo.flow.model.flowChart.FlowChart;
import org.jojo.flow.model.flowChart.modules.ModulePinGR;
import org.jojo.flow.model.flowChart.modules.RigidPinGR;

public class RigidConnectionGR extends ConnectionGR {
    public RigidConnectionGR(final RigidPinGR asFromPin, final RigidPinGR asToPin
            , final FlowChart flowChart) {
        super(asFromPin, asToPin, flowChart);
    }

    @Override
    public void addToPin(final Point diversionPoint, final ModulePinGR toPin) {
        Objects.requireNonNull(diversionPoint);
        Objects.requireNonNull(toPin);
        if (!(toPin instanceof RigidPinGR)) {
            throw new IllegalArgumentException("to pin GR must be rigid pin GR");
        }
        addConnection(new OneConnectionGR(getFromPin(), toPin, getFlowChartGR()));
    }
}
