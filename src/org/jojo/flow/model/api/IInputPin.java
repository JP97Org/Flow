package org.jojo.flow.model.api;

import org.jojo.flow.exc.ListSizeException;

public interface IInputPin extends IModulePin {

    @Override
    boolean addConnection(IConnection toAdd) throws ListSizeException;

    IData getData();
}