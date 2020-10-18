package org.jojo.flow.model.api;

import java.util.List;

import org.jojo.flow.exc.ListSizeException;

public interface IModulePin extends IDOMable {

    IGraphicalRepresentation getGraphicalRepresentation();

    IFlowModule getModule();

    List<IConnection> getConnections();

    boolean addConnection(IConnection toAdd) throws ListSizeException;

    boolean removeConnection(IConnection toRemove);

    boolean removeConnection(int index);

    IData getDefaultData();

    IModulePinImp getModulePinImp();

    int fixedHashCode();
}