package org.jojo.flow.model.api;

import org.jojo.flow.model.flowChart.modules.DefaultPin;
import org.jojo.flow.model.flowChart.modules.FlowModule;
import org.jojo.flow.model.flowChart.modules.OutputPin;
import org.jojo.flow.model.util.DynamicObjectLoader;

public interface IOutputPin extends IModulePin {
    public static IOutputPin getDefaultImplementation(final FlowModule module) {
        return (IOutputPin) DynamicObjectLoader.loadPin(OutputPin.class.getName(), DefaultPin.class.getName(), module);
    }

    boolean putData(IData data);
}