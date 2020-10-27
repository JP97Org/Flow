package org.jojo.flow.model.api;

import org.jojo.flow.model.flowChart.modules.DefaultInputPinGR;
import org.jojo.flow.model.util.DynamicObjectLoader;

/**
 * This interface represents the graphical representation for a default input pin.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public interface IDefaultInputPinGR extends IModulePinGR {
    
    /**
     * Gets the default implementation.
     * 
     * @return the default implementation
     */
    public static IDefaultInputPinGR getDefaultImplementation() {
        return (IDefaultInputPinGR) DynamicObjectLoader.loadGR(DefaultInputPinGR.class.getName());
    }
}