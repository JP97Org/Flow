package org.jojo.flow.model.flowChart.modules;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.jojo.flow.model.api.IModulePinImp;
import org.jojo.flow.model.data.Data;
import org.jojo.flow.model.flowChart.FlowChartElement;
import org.jojo.flow.model.flowChart.connections.Connection;

public abstract class ModulePinImp implements IModulePinImp {
    private FlowModule module;
    private final List<Connection> connections;
    private Data defaultData;
    
    public ModulePinImp(final FlowModule module, final Data defaultData) {
        this.module = Objects.requireNonNull(module);
        this.connections = new ArrayList<>();
        this.defaultData = defaultData;
    }
    
    public FlowModule getModule() {
        return this.module;
    }
    
    protected void setModule(FlowModule module) {
        this.module = module;
    }
    
    public synchronized List<Connection> getConnections() {
        final List<Connection> ret = new ArrayList<>(this.connections);
        ret.sort(FlowChartElement.getIdComparator());
        return ret;
    }
    
    public synchronized boolean addConnection(final Connection toAdd)  {
        final boolean ret = toAdd.isPinImpInConnection(this);
        if (ret) {
            this.connections.add(toAdd);
        }
        return ret;
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
    
    @Override
    public int hashCode() {
        return Objects.hash(this.module);
    }
    
    @Override
    public boolean equals(final Object other) {
        if (other instanceof ModulePinImp) {
            final ModulePinImp otherM = (ModulePinImp)other;
            if (this.module == null && otherM.module == null) {
                return true;
            } else if (this.module == null || otherM.module == null) {
                return false;
            }
            return this.module.equals(otherM.module);
        }
        return false;
    }
    
    @Override
    public abstract String toString();
}
