package org.jojo.flow.model.api;

import org.jojo.flow.model.flowChart.modules.FlowModule;
import org.jojo.flow.model.util.DynamicObjectLoader;

public interface IRigidPin extends IModulePinImp {
    public static IRigidPin getDefaultImplementation(final FlowModule module) {
        return DynamicObjectLoader.loadRigidPin(module);
    }

    IInputPin getInputPin();

    IOutputPin getOutputPin();
}