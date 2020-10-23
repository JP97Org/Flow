package org.jojo.flow.model.api;

import java.awt.Color;
import java.awt.Point;
import java.util.List;

/**
 * This interface represents a super-interface for all graphical representations of connections.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public interface IConnectionGR extends IFlowChartElementGR {

    /**
     * Adds the given to pin by connecting the from pin with two IConnectionLineGR instances
     * diverted at the given point of diversion.
     * 
     * @param diversionPoint - the point of diversion
     * @param toPin - the GR of the to pin
     */
    void addToPin(Point diversionPoint, IModulePinGR toPin);

    /**
     * Removes the given to pin GR if it is part of this connection GR.
     * 
     * @param toPin - the to pin GR
     * @return whether the to pin GR was removed
     */
    boolean removeToPin(IModulePinGR toPin);

    /**
     * Gets the from pin GR. It must not be {@code null}.
     * 
     * @return the from pin GR
     */
    IModulePinGR getFromPin();

    /**
     * Gets a copy of the to pins list.
     * 
     * @return a copy of the to pins list
     */
    List<IModulePinGR> getToPins();

    /**
     * Gets the single connections of this connection.
     * 
     * @return a copy of the IOneConnectionGR list
     */
    List<IOneConnectionGR> getSingleConnections();

    /**
     * Sets the given color. It must not be {@code null}.
     * 
     * @param color - the color to be set
     */
    void setColor(Color color);
}