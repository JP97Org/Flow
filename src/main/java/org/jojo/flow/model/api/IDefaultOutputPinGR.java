package org.jojo.flow.model.api;

import org.jojo.flow.model.flowChart.modules.DefaultOutputPinGR;
import org.jojo.flow.model.util.DynamicObjectLoader;

/**
 * This interface represents the graphical representation for an IDefaultOutputPin.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public interface IDefaultOutputPinGR extends IModulePinGR {
    
    /**
     * Gets the default implementation.
     * 
     * @return the default implementation
     */
    public static IDefaultOutputPinGR getDefaultImplementation() {
        return (IDefaultOutputPinGR) DynamicObjectLoader.loadGR(DefaultOutputPinGR.class.getName());
    }
}