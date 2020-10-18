package org.jojo.flow.model.api;

public interface IRigidPin extends IModulePinImp {

    IInputPin getInputPin();

    IOutputPin getOutputPin();
}