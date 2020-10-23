package org.jojo.flow.model.api;

import java.util.List;

import org.jojo.flow.exc.ValidationException;
import org.jojo.flow.model.flowChart.FlowChartGR;
import org.jojo.flow.model.util.DynamicObjectLoader;

/**
 * This interface represents a flow chart. A flow chart contains flow chart elements.
 * The flow chart itself is considered a flow chart element. As of version 1.0 it cannot be directly
 * placed on another flow chart. However, encapsulating a flow chart inside a flow module can be possible.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 * @see IFlowChartElement
 */
public interface IFlowChart extends IFlowChartElement {
    
    /**
     * Gets the default implementation.
     * 
     * @return the default implementation
     */
    public static IFlowChart getDefaultImplementation() {
        return (IFlowChart) IAPI.defaultImplementationOfThisApi(
                new Class<?>[] {int.class, IFlowChartGR.class}, 
                IModelFacade.getDefaultImplementation().nextFreeId(), 
                DynamicObjectLoader.loadGR(FlowChartGR.class.getName()));
    }

    /**
     * Adds the given flow module to this flow chart. 
     * Note that the uniqueness of the ID is not checked. Consider using 
     * {@link IModelFacade#addModule(IFlowChart, IDynamicClassLoader, String, java.awt.Point)} 
     * to ensure ID uniqueness.
     * 
     * @param module - the given flow module (must not be {@code null})
     */
    void addModule(IFlowModule module);

    /**
     * Adds the given connection to this flow chart if all modules connected to the given connection
     * are on this flow chart and the connection can be connected with {@link IConnection#connect()}.
     * If this method does not succeed nothing changes.
     * 
     * @param connection - the given connection
     * @return whether the connection was added
     */
    boolean addConnection(IConnection connection);

    /**
     * Removes the given flow module.
     * 
     * @param module - the given flow module
     * @return whether the module was removed
     */
    boolean removeModule(IFlowModule module);

    /**
     * Removes the given connection.
     * 
     * @param connection - the given connection
     * @return whether the connection was removed
     */
    boolean removeConnection(IConnection connection);

    /**
     * Gets a copy of the flow modules list of this flow chart sorted by id.
     * 
     * @return a copy of the flow modules list of this flow chart sorted by id
     */
    List<IFlowModule> getModules();

    /**
     * Gets a copy of the connections list of this flow chart sorted by id.
     * 
     * @return a copy of the connections list of this flow chart sorted by id
     */
    List<IConnection> getConnections();

    /**
     * Gets a copy of the default arrow list of this flow chart sorted by id.
     * 
     * @return a copy of the default arrow list of this flow chart sorted by id
     */
    List<IDefaultArrow> getArrows();

    /**
     * Validates the flow chart, i.e. checks if the default data put on all default arrows
     * are matching the respective declared data types of the receiving flow modules.
     * 
     * @return {@code null} if this flow chart is valid, otherwise the first found invalid arrow
     * @throws ValidationException if validation cannot be done due to wrong flow module programming
     * @see IDataSignature
     * @see IFlowModule#validate()
     */
    IDefaultArrow validate() throws ValidationException;
}