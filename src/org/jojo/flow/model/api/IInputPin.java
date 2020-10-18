package org.jojo.flow.model.api;

import org.jojo.flow.exc.ListSizeException;
import org.jojo.flow.model.flowChart.modules.DefaultPin;
import org.jojo.flow.model.flowChart.modules.FlowModule;
import org.jojo.flow.model.flowChart.modules.InputPin;
import org.jojo.flow.model.util.DynamicObjectLoader;

public interface IInputPin extends IModulePin {
    public static IInputPin getDefaultImplementation(final FlowModule module) {
        return (IInputPin) DynamicObjectLoader.loadPin(InputPin.class.getName(), DefaultPin.class.getName(), module);
    }

    @Override
    boolean addConnection(IConnection toAdd) throws ListSizeException;

    IData getData();
}