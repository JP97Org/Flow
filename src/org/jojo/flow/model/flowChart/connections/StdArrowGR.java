package org.jojo.flow.model.flowChart.connections;

import java.awt.Point;
import java.awt.Shape;
import java.util.Objects;

import org.jojo.flow.model.flowChart.FlowChart;
import org.jojo.flow.model.flowChart.modules.ModulePinGR;
import org.jojo.flow.model.flowChart.modules.StdInputPinGR;
import org.jojo.flow.model.flowChart.modules.StdOutputPinGR;

public class StdArrowGR extends ConnectionGR {
    private Shape defaultArrow; //TODO evtl. anderer Typ jenachdem ob das so geht
    private Shape selectedArrow; //TODO evtl. anderer Typ jenachdem ob das so geht
    
    public StdArrowGR(final StdOutputPinGR fromPin, final StdInputPinGR toPin, final FlowChart flowChart,
            final Shape defaultArrow) {
        super(fromPin, toPin, flowChart);
        this.defaultArrow = defaultArrow;
    }

    @Override
    public void addToPin(final Point diversionPoint, final ModulePinGR toPin) {
        Objects.requireNonNull(diversionPoint);
        Objects.requireNonNull(toPin);
        if (!(toPin instanceof StdInputPinGR)) {
            throw new IllegalArgumentException("to pin GR must be std input pin GR");
        }
        addConnection(new OneConnectionGR(getFromPin(), toPin, getFlowChartGR()));
    }

    public Shape getDefaultArrow() {
        return this.defaultArrow;
    }

    public void setDefaultArrow(final Shape defaultArrow) {
        this.defaultArrow = Objects.requireNonNull(defaultArrow);
        notifyObservers(defaultArrow);
    }

    public Shape getSelectedArrow() {
        return this.selectedArrow;
    }

    public void setSelectedArrow(final Shape selectedArrow) {
        this.selectedArrow = Objects.requireNonNull(selectedArrow);
        notifyObservers(selectedArrow);
    }

}
