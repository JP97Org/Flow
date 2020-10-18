package org.jojo.flow.model.api;

import org.jojo.flow.model.flowChart.LabelGR;
import org.jojo.flow.model.util.DynamicObjectLoader;

public interface ILabelGR extends IGraphicalRepresentation {
    public static ILabelGR getDefaultImplementation() {
        return (ILabelGR) DynamicObjectLoader.loadGR(LabelGR.class.getName());
    }

    String getText();

    void setText(String text);

    IFlowChartElement getElement();

    void setElement(IFlowChartElement element);

    void setHeight(int height);

    void setWidth(int width);
}