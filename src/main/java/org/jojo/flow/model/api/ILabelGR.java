package org.jojo.flow.model.api;

import org.jojo.flow.model.flowChart.LabelGR;
import org.jojo.flow.model.util.DynamicObjectLoader;

/**
 * This interface represents the graphical representation for labels which can be 
 * attached to flow chart elements.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public interface ILabelGR extends IGraphicalRepresentation {
    
    /**
     * Gets the default implementation.
     * 
     * @return the default implementation
     */
    public static ILabelGR getDefaultImplementation() {
        return (ILabelGR) DynamicObjectLoader.loadGR(LabelGR.class.getName());
    }

    /**
     * Gets the text of the label.
     * 
     * @return the text of the label (cannot be {@code null})
     */
    String getText();

    /**
     * Sets the text of this label to the given text.
     * 
     * @param text - the given text
     */
    void setText(String text);

    /**
     * Gets the flow chart element to which this label is attached or {@code null} if none.
     * 
     * @return the flow chart element to which this label is attached or {@code null} if none
     */
    IFlowChartElement getElement();

    /**
     * Sets the flow chart element to which this label is attached to.
     * 
     * @param element - the element (may be {@code null}, meaning removing an attachment if existent)
     */
    void setElement(IFlowChartElement element);

    /**
     * Sets the height of this label.
     * 
     * @param height - the height of the label
     */
    void setHeight(int height);

    /**
     * Sets the width of this label.
     * 
     * @param width - the width of the label
     */
    void setWidth(int width);
}