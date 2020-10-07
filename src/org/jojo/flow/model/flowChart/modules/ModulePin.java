package org.jojo.flow.model.flowChart.modules;

import java.util.List;

import org.jojo.flow.model.data.Data;
import org.jojo.flow.model.flowChart.connections.Connection;

public abstract class ModulePin {
    private final ModulePinImp imp;
    
    public ModulePin(final ModulePinImp imp) {
        this.imp = imp;
    }
    
    public FlowModule getModule() {
        return this.imp.getModule();
    }
    
    public synchronized List<Connection> getConnections() {
        return this.imp.getConnections();
    }
    
    public synchronized void addConnection(final Connection toAdd) throws ListSizeException {
        this.imp.addConnection(toAdd);
    }
    
    public synchronized boolean removeConnection(final Connection toRemove) {
        return this.imp.removeConnection(toRemove);
    }
    
    public synchronized boolean removeConnection(final int index) {
        return this.imp.removeConnection(index);
    }
    
    public Data getDefaultData() {
        return this.imp.getDefaultData();
    }
    
    protected void setDefaultData(final Data defaultData) {
        this.imp.setDefaultData(defaultData);
    }
    
    public ModulePinImp getModulePinImp() {
        return this.imp;
    }
}
