package org.jojo.flow.model.flowChart;

import java.awt.Point;

public abstract class FlowChartElementGR extends GraphicalRepresentation {
    private LabelGR label;
    
    public FlowChartElementGR(final Point position, final FlowChart flowChart) {
        super(position, (FlowChartGR)flowChart.getGraphicalRepresentation());
    }

    public LabelGR getLabel() {
        return label;
    }

    public void setLabel(final LabelGR label) {
        this.label = label;
    }

    public void removeLabel() {
        setLabel(null);
    }
}
