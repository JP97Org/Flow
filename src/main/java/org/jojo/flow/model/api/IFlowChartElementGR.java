package org.jojo.flow.model.api;

/**
 * This interface represents a super-interface for all graphical representations of flow chart elements.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public interface IFlowChartElementGR extends IGraphicalRepresentation {

    /**
     * Gets the label attached to this element or {@code null} if no label has been attached.
     * 
     * @return the label attached to this element or {@code null} if no label has been attached
     */
    ILabelGR getLabel();

    /**
     * Attaches the given label to this element.
     * 
     * @param label - the label to be set 
     */
    void setLabel(ILabelGR label);

    /**
     * Removes the attached label if existent.
     */
    void removeLabel();
}