package org.jojo.flow.model.flowChart;

import java.awt.Point;

public abstract class FlowChartElementGR extends GraphicalRepresentation {
    private LabelGR label;
    
    public FlowChartElementGR(final Point position) {
        super(position);
    }

    public final LabelGR getLabel() {
        return this.label;
    }

    public final void setLabel(final LabelGR label) {
        this.label = label;
        notifyObservers(label);
    }

    public final void removeLabel() {
        setLabel(null);
    }
}
