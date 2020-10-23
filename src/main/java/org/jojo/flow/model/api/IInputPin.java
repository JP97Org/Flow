package org.jojo.flow.model.api;

import org.jojo.flow.exc.ListSizeException;
import org.jojo.flow.model.flowChart.modules.DefaultPin;
import org.jojo.flow.model.flowChart.modules.FlowModule;
import org.jojo.flow.model.flowChart.modules.InputPin;
import org.jojo.flow.model.util.DynamicObjectLoader;

/**
 * This interface represents a module input pin.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public interface IInputPin extends IModulePin {
    
    /**
     * Gets the default implementation with an IDefaultPin as implementation.
     * 
     * @param module - the flow module to which this pin is attached (must not be {@code null})
     * @return the default implementation with an IDefaultPin as implementation
     * @see IRigidPin#getDefaultImplementation(FlowModule)
     * @see IOutputPin#getDefaultImplementation(FlowModule)
     */
    public static IInputPin getDefaultImplementation(final FlowModule module) {
        return (IInputPin) DynamicObjectLoader.loadPin(InputPin.class.getName(), DefaultPin.class.getName(), module);
    }

    @Override
    boolean addConnection(IConnection toAdd) throws ListSizeException;

    /**
     * Gets the data on the incoming connection or {@code getDefaultData()} if no incoming data exists.
     * 
     * @return the data on the incoming connection or {@code getDefaultData()} if no incoming data exists
     * @see #getDefaultData()
     */
    IData getData();
}