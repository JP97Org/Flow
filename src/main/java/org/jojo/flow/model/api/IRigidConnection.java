package org.jojo.flow.model.api;

import org.jojo.flow.model.util.DynamicObjectLoader;

/**
 * This interface represents a rigid connection, i.e. a connection used for connecting two rigid pins
 * rigidly as if they are connected with a rod. As of version 1.0, this connection does not have any
 * purpose. However, a purpose may be added in the future.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public interface IRigidConnection extends IConnection {
    
    /**
     * Gets the default implementation.
     * 
     * @param asFrom - the rigid pin internally used as the from pin (must not be {@code null})
     * @param asTo - the rigid pin internally used as the to pin (must not be {@code null})
     * @param name - the name of the connection (must not be {@code null})
     * @return the default implementation
     */
    public static IRigidConnection getDefaultImplementation(final IRigidPin asFrom, final IRigidPin asTo, final String name) {
        return (IRigidConnection) DynamicObjectLoader.loadConnection(
                IModelFacade.getDefaultImplementation().nextFreeId(), asFrom, asTo, name);
    }
}