package org.jojo.flow.model.api;

import org.jojo.flow.model.flowChart.modules.InputPin;
import org.jojo.flow.model.flowChart.modules.OutputPin;

public interface IRigidPin extends IModulePinImp {

    InputPin getInputPin();

    OutputPin getOutputPin();
}