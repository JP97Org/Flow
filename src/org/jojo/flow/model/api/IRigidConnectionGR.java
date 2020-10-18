package org.jojo.flow.model.api;

import org.jojo.flow.model.flowChart.connections.RigidConnectionGR;
import org.jojo.flow.model.util.DynamicObjectLoader;

public interface IRigidConnectionGR extends IConnectionGR {
    public static IRigidConnectionGR getDefaultImplementation() {
        return (IRigidConnectionGR) DynamicObjectLoader.loadGR(RigidConnectionGR.class.getName());
    }
}