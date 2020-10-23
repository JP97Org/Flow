package org.jojo.flow.model.api;

import org.jojo.flow.model.flowChart.connections.RigidConnectionGR;
import org.jojo.flow.model.util.DynamicObjectLoader;

/**
 * This interface represents the graphical representation for an IRigidConnection.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public interface IRigidConnectionGR extends IConnectionGR {
    
    /**
     * Gets the default implementation.
     * 
     * @return the default implementation
     */
    public static IRigidConnectionGR getDefaultImplementation() {
        return (IRigidConnectionGR) DynamicObjectLoader.loadGR(RigidConnectionGR.class.getName());
    }
}