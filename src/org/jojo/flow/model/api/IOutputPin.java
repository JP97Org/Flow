package org.jojo.flow.model.api;

public interface IOutputPin extends IModulePin {

    boolean putData(IData data);
}