package org.jojo.flow.model.api;

import org.jojo.flow.model.flowChart.modules.FlowModule;
import org.jojo.flow.model.util.DynamicObjectLoader;

/**
 * This interface represents a rigid pin, i.e. a pin to which a IRigidConnection can be connected.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public interface IRigidPin extends IModulePinImp {
    
    /**
     * Gets the default implementation.
     * 
     * @param module - the module to which this rigid pin is attached (must not be {@code null})
     * @return the default implementation
     */
    public static IRigidPin getDefaultImplementation(final FlowModule module) {
        return DynamicObjectLoader.loadRigidPin(module);
    }

    /**
     * Gets the as input (asTo) pin of this rigid pin.
     * 
     * @return the as input (asTo) pin of this rigid pin.
     */
    IInputPin getInputPin();

    /**
     * Gets the as output (asFrom) pin of this rigid pin.
     * 
     * @return the as output (asFrom) pin of this rigid pin
     */
    IOutputPin getOutputPin();
}