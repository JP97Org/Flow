package org.jojo.flow.model.flowChart.connections;

import org.jojo.flow.model.flowChart.FlowChartElementGR;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.jojo.flow.model.flowChart.FlowChart;
import org.jojo.flow.model.flowChart.modules.ModulePinGR;

public abstract class ConnectionGR extends FlowChartElementGR {
    private final ModulePinGR fromPin;
    private final List<OneConnectionGR> connections;
    
    public ConnectionGR(final ModulePinGR fromPin, final ModulePinGR toPin, final FlowChart flowChart) { 
        super(fromPin.getPosition(), flowChart);
        this.fromPin = Objects.requireNonNull(fromPin);
        this.connections = new ArrayList<>();
        addToPin(new Point(fromPin.getPosition().x, toPin.getPosition().y), toPin);
    }
    
    public abstract void addToPin(final Point diversionPoint, final ModulePinGR toPin);
    
    protected void addConnection(final OneConnectionGR connection) {
        Objects.requireNonNull(connection);
        if (!connection.getFromPin().equals(this.fromPin)) {
            throw new IllegalArgumentException("from pin is not the same as the one of this connection");
        }
        this.connections.add(connection);
    }
    
    public boolean removeToPin(final ModulePinGR toPin) {
        final List<OneConnectionGR> toRemove = this.connections
                .stream()
                .filter(c -> c.getToPin().equals(toPin))
                .collect(Collectors.toList());
        boolean ret = !toRemove.isEmpty();
        for (final OneConnectionGR toRemoveElem : toRemove) {
            ret &= this.connections.remove(toRemoveElem);
        }
        return ret;
    }
    
    public ModulePinGR getFromPin() {
        return this.fromPin;
    }
    
    public List<ModulePinGR> getToPins() {
        return this.connections.stream().map(c -> c.getToPin()).collect(Collectors.toList());
    }
    
    public List<OneConnectionGR> getSingleConnections() {
        return new ArrayList<>(this.connections);
    }
    
    public void setColor(final Color color) {
        Objects.requireNonNull(color);
        this.connections.forEach(c -> c.setColor(color));
    }
    
    @Override
    public int getHeigth() {
        final int maxTo = getMaxTo(false);
        return Math.abs(this.fromPin.getLinePoint().y - maxTo);
    }
    
    private int getMaxTo(final boolean isX) {
        return this.connections
                .stream()
                .map(c -> c.getToPin().getLinePoint())
                .mapToInt(p -> isX ? (Math.abs(this.fromPin.getLinePoint().x - p.x)) 
                        : (Math.abs(this.fromPin.getLinePoint().y - p.y)))
                .max().orElse(isX ? this.fromPin.getLinePoint().x : this.fromPin.getLinePoint().y);
    }

    @Override
    public int getWidth() {
        final int maxTo = getMaxTo(true);
        return Math.abs(this.fromPin.getLinePoint().x - maxTo);
    }
}
