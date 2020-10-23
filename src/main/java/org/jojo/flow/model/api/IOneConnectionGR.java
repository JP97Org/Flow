package org.jojo.flow.model.api;

import java.awt.Color;
import java.awt.Point;
import java.util.List;

import org.jojo.flow.model.flowChart.connections.OneConnectionGR;
import org.jojo.flow.model.util.DynamicObjectLoader;

/**
 * This interface represents a graphical representation for one single connection from one output pin
 * to one input pin.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public interface IOneConnectionGR extends IGraphicalRepresentation, IDOMable {
    
    /**
     * Gets the default implementation.
     * 
     * @param hasDefaultPin - whether the connection is a default connection
     * @return the default implementation
     */
    public static IOneConnectionGR getDefaultImplementation(final boolean hasDefaultPin) {
        return (IOneConnectionGR) DynamicObjectLoader.loadGR(OneConnectionGR.class.getName(), hasDefaultPin);
    }

    /**
     * The height of this GR is defined as the maximum Point regarding the y coordinate.
     */
    int getHeight();
    
    /**
     * The width of this GR is defined as the maximum Point regarding the x coordinate.
     */
    int getWidth();

    /**
     * Sets the given to pin connecting it with the already set from pin via the given diversion point.
     * 
     * @param diversionPoint - the given diversion point (must not be {@code null})
     * @param toPin - the to pin (must not be {@code null})
     */
    void setToPin(Point diversionPoint, IModulePinGR toPin);

    /**
     * Gets the lines from the from pin to the to pin.
     * 
     * @return the lines
     */
    List<IConnectionLineGR> getLines();

    /**
     * Gets the diversion points, i.e. the corner points at which the connection diverts.
     * 
     * @return the diversion points
     */
    List<Point> getDiversionPoints();

    /**
     * Sets the path to the one defined by the given diversion points.
     * If this method does not succeed nothing changes and a Warning is reported to the 
     * {@link IFlowChartElement#GENERIC_ERROR_ELEMENT}.
     * 
     * @param diversionPoints - the given diversion points
     * @return whether the path was set
     */
    boolean setPath(List<Point> diversionPoints);

    /**
     * Gets the color.
     * 
     * @return the color (cannot be {@code null})
     */
    Color getColor();

    /**
     * Sets the color to the given color.
     * 
     * @param color - the given color (must not be {@code null})
     */
    void setColor(Color color);

    /**
     * Gets the from pin GR.
     * 
     * @return the from pin GR (must not be {@code null})
     */
    IModulePinGR getFromPin();

    /**
     * Gets the to pin GR.
     * 
     * @return the to pin GR (must not be {@code null})
     */
    IModulePinGR getToPin();

}