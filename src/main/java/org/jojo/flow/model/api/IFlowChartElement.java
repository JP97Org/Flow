package org.jojo.flow.model.api;

import java.util.List;

import org.jojo.flow.exc.Warning;
import org.jojo.flow.model.flowChart.FlowChart;
import org.jojo.flow.model.flowChart.FlowChartGR;

/**
 * This interface represents a super-interface for all elements which are directly on the flow chart.
 * This includes flow charts, flow modules and connections.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 * @see IFlowChart
 * @see IFlowModule
 * @see IConnection
 */
public interface IFlowChartElement extends IDOMable {
    
    /**
     * This {@link IFlowChartElement} represents an element for reporting instances of 
     * {@link org.jojo.flow.exc.Warning} and error {@link org.jojo.flow.exc.Warning} 
     * whenever it is not possible to determine an actual element to which the warning should be reported.
     * The element is implemented as an instance of {@link IFlowChart} but it should usually not be used as such.
     * The ID of the element is -1.
     */
    IFlowChartElement GENERIC_ERROR_ELEMENT = new FlowChart(-1, new FlowChartGR());
    
    /**
     * Gets the id of this element. This id should be unique at least inside the same model.
     * However, it is strongly recommended that the id is unique program-widely.
     * 
     * @return the id of this element
     * @see IModelFacade#nextFreeId()
     */
    int getId();
    
    /**
     * Gets the graphical representation of this element. It must not be {@code null}.
     * 
     * @return the {@link IGraphicalRepresentation} of this element
     */
    IGraphicalRepresentation getGraphicalRepresentation();

    // TODO
    /**
     * Serializes the simulation state of this element. <br/>
     * Note that as of version 1.0 this method is not yet used.
     * 
     * @return the serialized simulation state
     */
    String serializeSimulationState();

    // TODO
    /**
     * Restores the given simulation state of this element. <br/>
     * Note that as of version 1.0 this method is not yet used. 
     * 
     * @param simulationState - the given simulation state
     */
    void restoreSerializedSimulationState(String simulationState);

    /**
     * Reports the given warning to this element, i.e. adds the warning to this elements's warning list.
     * 
     * @param warning - the given warning
     */
    void reportWarning(Warning warning);

    /**
     * Resolves the given warning, i.e. it is not longer stored in this element's warning list.
     * This method does not change anything if the given warning is not in this element's warning list.
     * 
     * @param warning - the given warning
     */
    void warningResolved(Warning warning);

    /**
     * Gets a copy of this element's warning list at the moment when this method is entered.
     * Note that implementations of this method should be {@code synchronized} or use another
     * synchronization mechanism in order to ensure this method's contract.
     * 
     * @return this element's warning list
     */
    List<Warning> getWarnings();

    /**
     * Gets the last reported warning to this element at the moment this method is entered.
     * Note that implementations of this method should be {@code synchronized} or use another
     * synchronization mechanism in order to ensure this method's contract.
     * 
     * @return the last reported {@link org.jojo.flow.exc.Warning} or {@code null} if none was reported
     */
    Warning getLastWarning();
}