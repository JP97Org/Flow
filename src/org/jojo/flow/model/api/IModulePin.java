package org.jojo.flow.model.api;

import java.util.List;

import org.jojo.flow.model.data.Data;
import org.jojo.flow.model.flowChart.GraphicalRepresentation;
import org.jojo.flow.model.flowChart.connections.Connection;
import org.jojo.flow.model.flowChart.modules.FlowModule;
import org.jojo.flow.model.flowChart.modules.ListSizeException;
import org.jojo.flow.model.flowChart.modules.ModulePinImp;

public interface IModulePin extends IAPI {

    GraphicalRepresentation getGraphicalRepresentation();

    FlowModule getModule();

    List<Connection> getConnections();

    boolean addConnection(Connection toAdd) throws ListSizeException;

    boolean removeConnection(Connection toRemove);

    boolean removeConnection(int index);

    Data getDefaultData();

    ModulePinImp getModulePinImp();

}