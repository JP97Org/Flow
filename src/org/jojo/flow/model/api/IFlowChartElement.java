package org.jojo.flow.model.api;

import java.util.List;

import org.jojo.flow.model.Warning;
import org.jojo.flow.model.flowChart.FlowChart;
import org.jojo.flow.model.flowChart.FlowChartGR;
import org.jojo.flow.model.flowChart.GraphicalRepresentation;

public interface IFlowChartElement extends IAPI {
    
    IFlowChartElement GENERIC_ERROR_ELEMENT = new FlowChart(-1, new FlowChartGR());

    GraphicalRepresentation getGraphicalRepresentation();

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