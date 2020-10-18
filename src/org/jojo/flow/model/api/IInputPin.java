package org.jojo.flow.model.api;

import org.jojo.flow.exc.ListSizeException;
import org.jojo.flow.model.flowChart.connections.Connection;

public interface IInputPin extends IModulePin {

    @Override
    boolean addConnection(Connection toAdd) throws ListSizeException;

    IData getData();
}