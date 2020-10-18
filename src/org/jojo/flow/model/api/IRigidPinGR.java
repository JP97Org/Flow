package org.jojo.flow.model.api;

import org.jojo.flow.model.flowChart.modules.RigidPinGR;
import org.jojo.flow.model.util.DynamicObjectLoader;

public interface IRigidPinGR extends IModulePinGR {
    public static IRigidPinGR getDefaultImplementation() {
        return (IRigidPinGR) DynamicObjectLoader.loadGR(RigidPinGR.class.getName());
    }
}