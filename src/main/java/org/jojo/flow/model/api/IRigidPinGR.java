package org.jojo.flow.model.api;

import org.jojo.flow.model.flowChart.modules.RigidPinGR;
import org.jojo.flow.model.util.DynamicObjectLoader;

/**
 * This interface represents a graphical representation of an IRigidPin.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public interface IRigidPinGR extends IModulePinGR {
    
    /**
     * Gets the default implementation.
     * 
     * @return the default implementation
     */
    public static IRigidPinGR getDefaultImplementation() {
        return (IRigidPinGR) DynamicObjectLoader.loadGR(RigidPinGR.class.getName());
    }
}