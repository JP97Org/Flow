package org.jojo.flow.model.api;

public interface IFlowChartElementGR extends IGraphicalRepresentation {

    ILabelGR getLabel();

    void setLabel(ILabelGR label);

    void removeLabel();
}