package org.jojo.flow.model.api;

import java.util.List;

public interface IModulePinImp extends IAPI {
    
    public IFlowModule getModule();
    
    List<IConnection> getConnections();
    
    boolean addConnection(final IConnection toAdd);
    
    boolean removeConnection(final IConnection toRemove);
    
    boolean removeConnection(final int index);
    
    IData getDefaultData();
}
