package org.jojo.flow.model.api;

import java.util.List;

import org.jojo.flow.exc.ValidationException;
import org.jojo.flow.model.data.Fraction;
import org.jojo.flow.model.data.units.Frequency;
import org.jojo.flow.model.flowChart.connections.DefaultArrow;
import org.jojo.flow.model.flowChart.modules.ExternalConfig;
import org.jojo.flow.model.flowChart.modules.FlowModule;
import org.jojo.flow.model.flowChart.modules.InputPin;
import org.jojo.flow.model.flowChart.modules.ModulePin;
import org.jojo.flow.model.flowChart.modules.OutputPin;
import org.jojo.flow.model.storeLoad.DOM;

public interface IFlowModule extends IFlowChartElement, Comparable<IFlowModule> {

    List<ModulePin> getAllModulePins();

    Frequency<Fraction> getFrequency();

    void run() throws Exception;

    DefaultArrow validate() throws ValidationException;

    void setInternalConfig(DOM internalConfigDOM);

    boolean isInternalConfigDOMValid(DOM internalConfigDOM);

    IInternalConfig getInternalConfig();

    boolean hasInternalConfig();

    ExternalConfig getExternalConfig();

    List<InputPin> getDefaultInputs();

    List<OutputPin> getDefaultOutputs();

    List<InputPin> getAllInputs();

    List<OutputPin> getAllOutputs();

    List<FlowModule> getDefaultDependencyList();

    List<FlowModule> getDefaultAdjacencyList();

}