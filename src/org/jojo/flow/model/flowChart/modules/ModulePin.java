package org.jojo.flow.model.flowChart.modules;

import java.util.List;

import org.jojo.flow.model.Subject;
import org.jojo.flow.model.data.Data;
import org.jojo.flow.model.flowChart.GraphicalRepresentation;
import org.jojo.flow.model.flowChart.connections.Connection;

public abstract class ModulePin extends Subject {
    private final ModulePinImp imp;
    private final ModulePinGR gr;
    
    public ModulePin(final ModulePinImp imp, final ModulePinGR gr) {
        this.imp = imp;
        this.gr = gr;
    }
    
    public GraphicalRepresentation getGraphicalRepresentation() {
        return this.gr;
    }
    
    public FlowModule getModule() {
        return this.imp.getModule();
    }
    
    public synchronized List<Connection> getConnections() {
        return this.imp.getConnections();
    }
    
    public synchronized void addConnection(final Connection toAdd) throws ListSizeException {
        this.imp.addConnection(toAdd);
        notifyObservers(toAdd);
    }
    
    public synchronized boolean removeConnection(final Connection toRemove) {
        final boolean ret = this.imp.removeConnection(toRemove);
        if (ret) {
            notifyObservers(toRemove);
        }
        return ret;
    }
    
    public synchronized boolean removeConnection(final int index) {
        final Connection toRemove = index >= getConnections().size() ? null : getConnections().get(index);
        final boolean ret = this.imp.removeConnection(index);
        if (ret) {
            notifyObservers(toRemove);
        }
        return ret;
    }
    
    public Data getDefaultData() {
        return this.imp.getDefaultData();
    }
    
    protected void setDefaultData(final Data defaultData) {
        this.imp.setDefaultData(defaultData);
        notifyObservers(defaultData);
    }
    
    public ModulePinImp getModulePinImp() {
        return this.imp;
    }
}
