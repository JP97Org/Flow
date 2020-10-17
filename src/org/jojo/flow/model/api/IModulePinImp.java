package org.jojo.flow.model.api;

import java.util.List;

import org.jojo.flow.model.flowChart.connections.Connection;
import org.jojo.flow.model.flowChart.modules.FlowModule;

public interface IModulePinImp extends IAPI {
    
    public FlowModule getModule();
    
    List<Connection> getConnections();
    
    boolean addConnection(final Connection toAdd);
    
    boolean removeConnection(final Connection toRemove);
    
    boolean removeConnection(final int index);
}
