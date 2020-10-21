package org.jojo.flow.model.api;

import java.awt.Point;

import org.jojo.flow.model.flowChart.connections.ConnectionLineGR;
import org.jojo.flow.model.util.DynamicObjectLoader;

/**
 * This interface represents a graphical representation for a connection line, i.e. a straight line
 * from a Point A to a different Point B. It must be ensured by implementations of this interface
 * that A and B are different points and that either the x coordinate or the y coordinate of the
 * two points are equal.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public interface IConnectionLineGR extends IGraphicalRepresentation {
    /**
     * Gets the default implementation.
     * 
     * @return the default implementation
     */
    public static IConnectionLineGR getDefaultImplementation() {
        return (IConnectionLineGR) DynamicObjectLoader.loadGR(ConnectionLineGR.class.getName());
    }

    /**
     * Gets the position of Point A. It must not be {@code null}.
     * 
     * @return the position of Point A
     */
    Point getPositionA();

    /**
     * Gets the position of Point B. It must not be {@code null}.
     * 
     * @return the position of Point B
     */
    Point getPositionB();

    /**
     * Sets the position of Point A. It must not be {@code null}.
     * 
     * @param positionA - the position of Point A
     */
    void setPositionA(Point positionA);

    /**
     * Sets the position of Point B. It must not be {@code null}.
     * 
     * @param positionB - the position of Point B
     */
    void setPositionB(Point positionB);
}