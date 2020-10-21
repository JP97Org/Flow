package org.jojo.flow.model.api;

import java.awt.Point;

import org.jojo.flow.model.flowChart.FlowChartGR;
import org.jojo.flow.model.util.DynamicObjectLoader;

/**
 * This interface represents the graphical representation for a flow chart.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public interface IFlowChartGR extends IFlowChartElementGR {
    
    /**
     * Gets the default implementation.
     * 
     * @return the default implementation
     */
    public static IFlowChartGR getDefaultImplementation() {
        return (IFlowChartGR) DynamicObjectLoader.loadGR(FlowChartGR.class.getName());
    }

    /**
     * Gets the flow chart to which this GR is attached.
     * 
     * @return the flow chart to which this GR is attached or {@code null} if none is yet set
     */
    IFlowChart getFlowChart();
    
    /**
     * Sets the given flow chart as the flow chart to which this GR is attached
     * 
     * @param fc - the given flow chart (must not be {@code null})
     */
    void setFlowChart(IFlowChart fc);

    /**
     * Adds a module GR.
     * 
     * @param moduleGR - the module GR
     */
    void addModule(IModuleGR moduleGR);

    /**
     * Adds a connection GR.
     * 
     * @param connectionGR - the connection GR
     */
    void addConnection(IConnectionGR connectionGR);

    /**
     * Removes the given module GR if existent.
     * 
     * @param moduleGR - the given module GR
     * @return whether it was removed
     */
    boolean removeModule(IModuleGR moduleGR);

    /**
     * Removes the given connection GR if existent.
     * 
     * @param connectionGR - the given connection GR
     * @return whether it was removed
     */
    boolean removeConnection(IConnectionGR connectionGR);

    /**
     * Gets the absolute origin point, i.e. the point on which the upper-left corner of the flow chart
     * is positioned in relation to the container which contains it. All other positions are specified
     * relative to this point. This means that a top-level flow chart always is positioned at (0,0), or
     * in other words {@code f.getPosition().equals(new Point(0,0))} for any top-level flow chart f.
     * 
     * @return the absolute origin point (must not be {@code null})
     * @see IGraphicalRepresentation#getPosition()
     */
    Point getAbsOriginPoint();

    /**
     * Sets the given point as the new absolute origin point.
     * 
     * @param absOriginPoint - the given point (must not be {@code null})
     * @see #getAbsOriginPoint()
     */
    void setAbsOriginPoint(Point absOriginPoint);

    /**
     * Determines whether a raster is enabled, i.e. whether a grid-like raster should be drawn by
     * the respective flow chart view.
     * 
     * @return whether a raster is enabled
     */
    boolean isRasterEnabled();

    /**
     * Sets whether a raster should be enabled.
     * 
     * @param isRasterEnabled - whether a raster should be enabled
     * @see #isRasterEnabled()
     */
    void setRasterEnabled(boolean isRasterEnabled);
}