package org.jojo.flow.model.api;

import java.util.List;

import org.jojo.flow.exc.Warning;
import org.jojo.flow.model.flowChart.FlowChart;
import org.jojo.flow.model.flowChart.FlowChartGR;

public interface IFlowChartElement extends IDOMable {
    
    IFlowChartElement GENERIC_ERROR_ELEMENT = new FlowChart(-1, new FlowChartGR()); //TODO replace by default impl. aufruf von IFlowChart

    IGraphicalRepresentation getGraphicalRepresentation();

    IInternalConfig serializeInternalConfig();

    void restoreSerializedInternalConfig(IInternalConfig internalConfig);

    String serializeSimulationState();

    void restoreSerializedSimulationState(String simulationState);

    void reportWarning(Warning warning);

    void warningResolved(Warning warning);

    int getId();

    List<Warning> getWarnings();

    Warning getLastWarning();
}