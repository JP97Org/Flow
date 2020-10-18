package org.jojo.flow.model.api;

import org.jojo.flow.model.util.DynamicObjectLoader;

public interface IRigidConnection extends IConnection {
    public static IRigidConnection getDefaultImplementation(final IRigidPin asFrom, final IRigidPin asTo, final String name) {
        return (IRigidConnection) DynamicObjectLoader.loadConnection(
                IModelFacade.getDefaultImplementation().nextFreeId(), asFrom, asTo, name);
    }
}