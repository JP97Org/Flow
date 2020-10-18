package org.jojo.flow.model.api;

import org.jojo.flow.model.flowChart.modules.DefaultOutputPinGR;
import org.jojo.flow.model.util.DynamicObjectLoader;

public interface IDefaultOutputPinGR extends IModulePinGR {
    public static IDefaultOutputPinGR getDefaultImplementation() {
        return (IDefaultOutputPinGR) DynamicObjectLoader.loadGR(DefaultOutputPinGR.class.getName());
    }
}