package org.jojo.flow.model.api;

import org.jojo.flow.model.flowChart.connections.Connection;
import org.jojo.flow.model.flowChart.modules.ListSizeException;

public interface IInputPin extends IModulePin {

    @Override
    boolean addConnection(Connection toAdd) throws ListSizeException;

    IData getData();
}