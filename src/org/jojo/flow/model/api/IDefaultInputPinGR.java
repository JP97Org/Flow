package org.jojo.flow.model.api;

import org.jojo.flow.model.flowChart.modules.DefaultInputPinGR;
import org.jojo.flow.model.util.DynamicObjectLoader;

public interface IDefaultInputPinGR extends IModulePinGR {
    public static IDefaultInputPinGR getDefaultImplementation() {
        return (IDefaultInputPinGR) DynamicObjectLoader.loadGR(DefaultInputPinGR.class.getName());
    }
}