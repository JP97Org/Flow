package org.jojo.flow.model.flowChart.modules;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.jojo.flow.model.api.IConnection;
import org.jojo.flow.model.api.IData;
import org.jojo.flow.model.api.IFlowModule;
import org.jojo.flow.model.api.IModulePinImp;
import org.jojo.flow.model.flowChart.FlowChartElement;

public abstract class ModulePinImp implements IModulePinImp {
    private IFlowModule module;
    private final List<IConnection> connections;
    private IData defaultData;
    
    public ModulePinImp(final IFlowModule module, final IData defaultData) {
        this.module = Objects.requireNonNull(module);
        this.connections = new ArrayList<>();
        this.defaultData = defaultData;
    }
    
    public IFlowModule getModule() {
        return this.module;
    }
    
    protected void setModule(IFlowModule module) {
        this.module = module;
    }
    
    public synchronized List<IConnection> getConnections() {
        final List<IConnection> ret = new ArrayList<>(this.connections);
        ret.sort(FlowChartElement.getIdComparator());
        return ret;
    }
    
    public synchronized boolean addConnection(final IConnection toAdd)  {
        final boolean ret = toAdd.isPinImpInConnection(this);
        if (ret) {
            this.connections.add(toAdd);
        }
        return ret;
    }
    
    public synchronized boolean removeConnection(final IConnection toRemove) {
        return this.connections.remove(toRemove);
    }
    
    public synchronized boolean removeConnection(final int index) {
        if (index >= this.connections.size()) {
            return false;
        }
        
        this.connections.remove(index);
        return true;
    }
    
    public IData getDefaultData() {
        return this.defaultData;
    }
    
    protected void setDefaultData(final IData defaultData) {
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
