package org.jojo.flow.model.api;

import java.util.List;

import org.jojo.flow.exc.ValidationException;
import org.jojo.flow.model.data.Fraction;
import org.jojo.flow.model.data.units.Frequency;
import org.jojo.flow.model.storeLoad.DOM;

public interface IFlowModule extends IFlowChartElement, Comparable<IFlowModule> {

    List<IModulePin> getAllModulePins();

    Frequency<Fraction> getFrequency();

    void run() throws Exception;

    IDefaultArrow validate() throws ValidationException;

    void setInternalConfig(DOM internalConfigDOM);

    boolean isInternalConfigDOMValid(DOM internalConfigDOM);

    IInternalConfig getInternalConfig();

    boolean hasInternalConfig();

    IExternalConfig getExternalConfig();

    List<IInputPin> getDefaultInputs();

    List<IOutputPin> getDefaultOutputs();

    List<IInputPin> getAllInputs();

    List<IOutputPin> getAllOutputs();

    List<IFlowModule> getDefaultDependencyList();

    List<IFlowModule> getDefaultAdjacencyList();
}