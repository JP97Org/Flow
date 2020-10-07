package org.jojo.flow.model.flowChart;

import java.util.ArrayList;
import java.util.List;

import org.jojo.flow.model.Subject;
import org.jojo.flow.model.Warning;
import org.jojo.flow.model.flowChart.modules.InternalConfig;

public abstract class FlowChartElement extends Subject {
    public static final FlowChartElement GENERIC_ERROR_ELEMENT = new FlowChart();
    
    private final List<Warning> warnings;
    
    public FlowChartElement() {
        this.warnings = new ArrayList<>();
    }
    
    //public abstract DOM getDOM(); //TODO DOM
    public abstract GraphicalRepresentation getGraphicalRepresentation();
    public abstract InternalConfig serializeInternalConfig();
    public abstract void restoreSerializedInternalConfig(InternalConfig internalConfig);
    public abstract String serializeSimulationState();
    public abstract void restoreSerializedSimulationState(String simulationState);
    
    public void reportWarning(final Warning warning) {
        this.warnings.add(warning);
    }
    
    public void warningResolved(final Warning warning) {
        this.warnings.remove(warning);
    }
    
    public List<Warning> getWarnings() {
        return new ArrayList<>(this.warnings);
    }
}
