package org.jojo.flow.model.api;

import java.awt.Point;
import java.util.List;

import org.jojo.flow.exc.ValidationException;

/**
 * This interface represents the facade for the whole flow model.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public interface IModelFacade extends IAPI {
    
    /**
     * Gets the default implementation.
     * 
     * @return the default implementation
     */
    public static IModelFacade getDefaultImplementation() {
        return (IModelFacade) IAPI.defaultImplementationOfThisApi(new Class<?>[] {});
    }
    
    /**
     * Gets the default implementation but with an own dynamic ID name space. 
     * Note: As of version 1.0, do not use the IModelFacade instance returned by this method 
     * unless you know what you do because DOM creation of the default model implementation relies on
     * program-wide unique IDs. Rather use {@link #getDefaultImplementation()} instead.
     * 
     * @param isUsingOwnDynamicIdNameSpace - whether the returned model facade should be dynamic (not recommended)
     * @return the default implementation
     */
    public static IModelFacade getDefaultImplementation(final boolean isUsingOwnDynamicIdNameSpace) {
        return (IModelFacade) IAPI.defaultImplementationOfThisApi(new Class<?>[] {boolean.class}, isUsingOwnDynamicIdNameSpace);
    }

    /**
     * Gets the flow chart with the given ID.
     * 
     * @param id - the given ID
     * @return the flow chart with the given ID or {@code null} if ID is unknown
     */
    IFlowChart getFlowChartById(int id);

    /**
     * Gets the element with the given ID. The element must be an element of the main flow chart.
     * 
     * @param id - the given ID
     * @return the element with the given ID or {@code null} if ID is unknown
     * @see #getMainFlowChart()
     * @see #getElementById(IFlowChart, int)
     */
    IFlowChartElement getElementById(int id);

    /**
     * Gets the element with the given ID. The element must be an element of the given flow chart.
     * 
     * @param flowChart - the given flow chart
     * @param id - the given ID
     * @return the element with the given ID or {@code null} if ID is unknown
     */
    IFlowChartElement getElementById(IFlowChart flowChart, int id);

    /**
     * Gets the main flow chart. Usually this flow chart has the ID 0.
     * However, you are not supposed to alter the returned flow chart, 
     * use other methods provided by this facade for this purpose.
     * 
     * @return the main flow chart or {@code null} if the main flow chart has yet not been set
     * @see #getOtherFlowCharts()
     */
    IFlowChart getMainFlowChart();

    /**
     * Gets the list of other flow charts. However, you are not supposed to alter the returned
     * list or flow charts, use other methods provided by this facade for this purpose.
     * 
     * @return list of other flow charts
     * @see #getMainFlowChart()
     */
    List<IFlowChart> getOtherFlowCharts();

    /**
     * Adds a flow chart to the other flow charts list.
     * @param flowChart - the flow chart to be added
     * @return whether the flow chart was added
     * @see #getOtherFlowCharts()
     */
    boolean addFlowChart(IFlowChart flowChart);

    /**
     * Sets the main flow chart.
     * 
     * @param flowChart - the main flow chart (must not be {@code null})
     */
    void setMainFlowChart(IFlowChart flowChart);

    /**
     * Calculates the next free ID. It is only ensured that this ID is unique in the scope defined
     * by this model facade's construction type, usually static construction 
     * ({@code isUsingOwnDynamicIdNameSpace == false}) is used, which means the ID is unique program-widely.
     * 
     * @return the next free ID
     * @see #getDefaultImplementation(boolean)
     */
    int nextFreeId();

    /**
     * Gets the store/load facade.
     * 
     * @return the store/load facade
     */
    IStoreLoadFacade getStoreLoad();

    /**
     * Gets the simulation facade for the main flow chart.
     * 
     * @return the simulation facade for the main flow chart
     */
    ISimulation getSimulation();

    /**
     * Gets the simulation facade for the given flow chart.
     * 
     * @param flowChart - the given flow chart
     * @return the simulation facade for the given flow chart
     */
    ISimulation getSimulation(IFlowChart flowChart);

    /**
     * Gets the simulation facade with the given config for the given flow chart.
     * 
     * @param flowChart - the given flow chart
     * @param config - the given ISimulationConfiguration
     * @return the simulation facade with the given config for the given flow chart
     */
    ISimulation getSimulation(IFlowChart flowChart, ISimulationConfiguration config);

    /**
     * Adds the module defined by the class loader and the class name to the main flow chart
     * at the specified position. If this method does not succeed nothing changes.
     * 
     * @param loader - the class loader
     * @param className - the class name 
     * @param position - the specified position
     * @return whether the module was successfully added
     * @see #addModule(IFlowChart, IDynamicClassLoader, String, Point)
     */
    boolean addModule(IDynamicClassLoader loader, String className, Point position);

    /**
     * Adds the module defined by the class loader and the class name to the given flow chart
     * at the specified position. If this method does not succeed nothing changes.
     * 
     * @param fc - the given flow chart
     * @param loader - the class loader
     * @param className - the class name 
     * @param position - the specified position
     * @return whether the module was successfully added
     */
    boolean addModule(IFlowChart fc, IDynamicClassLoader loader, String className, Point position);

    /**
     * Removes the module with the given ID if existent from the main flow chart.
     * 
     * @param id - the given ID
     * @return whether the module was removed
     * @see #removeModule(IFlowChart, int)
     */
    boolean removeModule(int id);

    /**
     * Removes the module with the given ID if existent from the given flow chart.
     * 
     * @param fc - the given flow chart
     * @param id - the given ID
     * @return whether the module was removed
     */
    boolean removeModule(IFlowChart fc, int id);

    /**
     * Tries to connect the given pins on the main flow chart. If it does not succeed nothing is changed.
     * 
     * @param from - the from pin
     * @param to - the to pin
     * @return whether the connection was established
     * @see #connect(IFlowChart, IOutputPin, IInputPin)
     */
    boolean connect(IOutputPin from, IInputPin to);

    /**
     * Tries to connect the given pins on the given flow chart. If it does not succeed nothing is changed.
     *
     * @param fc - the given flow chart
     * @param from - the from pin
     * @param to - the to pin
     * @return whether the connection was established
     */
    boolean connect(IFlowChart fc, IOutputPin from, IInputPin to);

    /**
     * Removes the connection with the given ID if existent from the main flow chart.
     * 
     * @param id - the given ID
     * @return whether the connection was removed
     */
    boolean removeConnection(int id);

    /**
     * Removes the connection with the given ID if existent from the given flow chart.
     * 
     * @param fc - the given flow chart
     * @param id - the given ID
     * @return whether the connection was removed
     */
    boolean removeConnection(IFlowChart fc, int id);

    /**
     * Validates the main flow chart.
     * 
     * @return {@code null} if flow chart is valid, otherwise the first faulty arrow
     * @throws ValidationException if validation is not possible
     * @see IFlowChart#validate()
     */
    IDefaultArrow validateFlowChart() throws ValidationException;

    /**
     * Validates the given flow chart.
     * 
     * @param fc - the given flow chart
     * @return {@code null} if flow chart is valid, otherwise the first faulty arrow
     * @throws ValidationException if validation is not possible
     * @see IFlowChart#validate()
     */
    IDefaultArrow validateFlowChart(IFlowChart fc) throws ValidationException;
}