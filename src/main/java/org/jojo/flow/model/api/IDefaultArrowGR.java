package org.jojo.flow.model.api;

import java.awt.Shape;

import org.jojo.flow.model.flowChart.connections.DefaultArrowGR;
import org.jojo.flow.model.util.DynamicObjectLoader;

/**
 * This interface represents the graphical representation for an {@link IDefaultArrow}.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public interface IDefaultArrowGR extends IConnectionGR {
    
    /**
     * Gets the default implementation.
     * 
     * @return the default implementation
     */
    public static IDefaultArrowGR getDefaultImplementation() {
        return (IDefaultArrowGR) DynamicObjectLoader.loadGR(DefaultArrowGR.class.getName());
    }

    //TODO impl. erfuellt das momentan noch nicht
    /**
     * Gets the default arrow shape. It must not be {@code null}. TODO
     * 
     * @return the default arrow shape
     */
    Shape getDefaultArrow();

    /**
     * Sets the default arrow shape to the given shape.
     * 
     * @param defaultArrow - the given shape (must not be {@code null})
     */
    void setDefaultArrow(Shape defaultArrow);

    /**
     * Gets the selected arrow shape. It may be {@code null} if no arrow shape has been selected.
     * 
     * @return the selected arrow shape or {@code null} if none has been selected
     * @see #setSelectedArrow(Shape)
     */
    Shape getSelectedArrow();

    /**
     * Sets the selected arrow shape to the given shape.
     * 
     * @param selectedArrow - the given shape (must not be {@code null})
     */
    void setSelectedArrow(Shape selectedArrow);
}