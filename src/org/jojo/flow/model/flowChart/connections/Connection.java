package org.jojo.flow.model.flowChart.connections;

import java.util.ArrayList;
import java.util.List;

import org.jojo.flow.model.flowChart.FlowChartElement;
import org.jojo.flow.model.flowChart.modules.InputPin;
import org.jojo.flow.model.flowChart.modules.OutputPin;

public abstract class Connection extends FlowChartElement {
    private final List<InputPin> toPins;
    private OutputPin fromPin;
    private final String name;
    
    public Connection(final OutputPin fromPin, final String name) throws ConnectionException {
        this.toPins = new ArrayList<>();
        this.fromPin = fromPin;
        this.name = name;
        
        final boolean connectionMatchesPins = connectionMatchesPins();
        if (!connectionMatchesPins) {
            throw new ConnectionException("fromPin to be set does not match connection type", this);
        }
    }
    
    public String getName() {
        return this.name;
    }
    
    public abstract String getInfo();
    
    public OutputPin getFromPin() {
        return this.fromPin;
    }
    
    public synchronized List<InputPin> getToPins() {
        return new ArrayList<>(this.toPins);
    }
    
    public synchronized boolean addToPin(final InputPin toPin) throws ConnectionException {
        this.toPins.add(toPin);
        final boolean connectionMatchesPins = connectionMatchesPins();
        final boolean ok = connectionMatchesPins && checkDataTypes();
        if (!ok) {
            this.toPins.remove(this.toPins.size() - 1);
        }
        if (!connectionMatchesPins) {
            throw new ConnectionException("toPin to be added does not match connection type", this);
        }
        return ok;
    }
    
    public synchronized boolean removeToPin(final InputPin toPin) {
        return this.toPins.remove(toPin);
    }
    
    public synchronized boolean removeToPin(final int index) {
        if (index >= this.toPins.size()) {
            return false;
        }
        this.toPins.remove(index);
        return true;
    }
    
    public synchronized boolean setFromPin(final OutputPin fromPin) throws ConnectionException {
        final OutputPin before = this.fromPin;
        this.fromPin = fromPin;
        final boolean connectionMatchesPins = connectionMatchesPins();
        final boolean ok = connectionMatchesPins && checkDataTypes();
        if (!ok) {
            this.fromPin = before;
        }
        if (!connectionMatchesPins) {
            throw new ConnectionException("fromPin to be set does not match connection type", this);
        }
        return ok;
    }
    
    protected abstract boolean connectionMatchesPins();
    
    protected abstract boolean checkDataTypes();
}
