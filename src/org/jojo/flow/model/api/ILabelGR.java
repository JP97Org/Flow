package org.jojo.flow.model.api;

import org.jojo.flow.model.flowChart.FlowChartElement;

public interface ILabelGR extends IGraphicalRepresentation {

    String getText();

    void setText(String text);

    FlowChartElement getElement();

    void setElement(FlowChartElement element);

    void setHeight(int height);

    void setWidth(int width);
}