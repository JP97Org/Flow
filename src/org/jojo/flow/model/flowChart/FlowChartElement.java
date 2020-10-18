package org.jojo.flow.model.flowChart;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.jojo.flow.exc.Warning;
import org.jojo.flow.model.Subject;
import org.jojo.flow.model.api.IDOMable;
import org.jojo.flow.model.api.IFlowChartElement;
import org.jojo.flow.model.api.IGraphicalRepresentation;
import org.jojo.flow.model.api.IInternalConfig;

public abstract class FlowChartElement extends Subject implements IDOMable, IFlowChartElement {
    private int id;
    private final List<Warning> warnings;
    
    public FlowChartElement(final int id) {
        this.id = id;
        this.warnings = new ArrayList<>();
    }
    
    @Override
    public abstract IGraphicalRepresentation getGraphicalRepresentation();
    @Override
    public abstract IInternalConfig serializeInternalConfig();
    @Override
    public abstract void restoreSerializedInternalConfig(IInternalConfig internalConfig);
    @Override
    public abstract String serializeSimulationState();
    @Override
    public abstract void restoreSerializedSimulationState(String simulationState);
    
    @Override
    public void reportWarning(final Warning warning) {
        this.warnings.add(warning);
        notifyObservers(warning);
    }
    
    @Override
    public void warningResolved(final Warning warning) {
        this.warnings.remove(warning);
        notifyObservers(warning);
    }
    
    @Override
    public final int getId() {
        return id;
    }
    
    protected void setId(final int id) {
        this.id = id;
    }
    
    @Override
    public List<Warning> getWarnings() {
        return new ArrayList<>(this.warnings);
    }

    @Override
    public Warning getLastWarning() {
        if (this.warnings.isEmpty()) {
            return null;
        }
        return this.warnings.get(this.warnings.size() - 1);
    }
    
    public static Comparator<? super IFlowChartElement> getIdComparator() {
        return new Comparator<IFlowChartElement>() {
            @Override
            public int compare(IFlowChartElement o1, IFlowChartElement o2) {
                return Integer.valueOf(o1.getId()).compareTo(o2.getId());
            }
        };
    }
    
    @Override
    public int hashCode() {
        return this.id;
    }
    
    @Override
    public boolean equals(final Object other) {
        if (other instanceof FlowChartElement) {
            return hashCode() == other.hashCode();
        }
        return false;
    }
    
    @Override
    public abstract String toString();
}
