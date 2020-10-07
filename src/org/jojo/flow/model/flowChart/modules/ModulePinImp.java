package org.jojo.flow.model.flowChart.modules;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.jojo.flow.model.data.Data;
import org.jojo.flow.model.flowChart.connections.Connection;

public abstract class ModulePinImp {
    private final Module module;
    private final List<Connection> connections;
    private Data defaultData;
    
    public ModulePinImp(final Module module, final Data defaultData) {
        this.module = Objects.requireNonNull(module);
        this.connections = new ArrayList<>();
        this.defaultData = defaultData;
    }
    
    public Module getModule() {
        return this.module;
    }
    
    public synchronized List<Connection> getConnections() {
        return new ArrayList<>(this.connections);
    }
    
    public synchronized void addConnection(final Connection toAdd)  {
        this.connections.add(toAdd); //TODO check whether pin is in connection
    }
    
    public synchronized boolean removeConnection(final Connection toRemove) {
        return this.connections.remove(toRemove);
    }
    
    public synchronized boolean removeConnection(final int index) {
        if (index >= this.connections.size()) {
            return false;
        }
        
        this.connections.remove(index);
        return true;
    }
    
    protected Data getDefaultData() {
        return this.defaultData;
    }
    
    protected void setDefaultData(final Data defaultData) {
        this.defaultData = defaultData;
    }
    
}
