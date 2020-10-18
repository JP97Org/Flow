package org.jojo.flow.model.api;

import org.jojo.flow.model.flowChart.LabelGR;

public interface IFlowChartElementGR extends IGraphicalRepresentation {

    LabelGR getLabel();

    void setLabel(LabelGR label);

    void removeLabel();
}