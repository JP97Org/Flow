package org.jojo.flow.model.api;

public interface ILabelGR extends IGraphicalRepresentation {

    String getText();

    void setText(String text);

    IFlowChartElement getElement();

    void setElement(IFlowChartElement element);

    void setHeight(int height);

    void setWidth(int width);
}