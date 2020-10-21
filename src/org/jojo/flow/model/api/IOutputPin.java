package org.jojo.flow.model.api;

import org.jojo.flow.model.flowChart.modules.DefaultPin;
import org.jojo.flow.model.flowChart.modules.FlowModule;
import org.jojo.flow.model.flowChart.modules.OutputPin;
import org.jojo.flow.model.util.DynamicObjectLoader;

/**
 * This interface represents a module output pin.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public interface IOutputPin extends IModulePin {
    
    /**
     * Gets the default implementation with an IDefaultPin as implementation.
     * 
     * @param module - the flow module to which this pin is attached (must not be {@code null})
     * @return the default implementation with an IDefaultPin as implementation
     * @see IRigidPin#getDefaultImplementation(FlowModule)
     * @see IInputPin#getDefaultImplementation(FlowModule)
     */
    public static IOutputPin getDefaultImplementation(final FlowModule module) {
        return (IOutputPin) DynamicObjectLoader.loadPin(OutputPin.class.getName(), DefaultPin.class.getName(), module);
    }

    /**
     * Puts the given data on each outgoing IDefaultArrow. If some arrows refuse the data to be put,
     * it is put on the other arrows anyway.
     * 
     * @param data - the given data
     * @return whether the data was successfully put on all outgoing arrows
     */
    boolean putData(IData data);
}