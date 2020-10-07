package org.jojo.flow.model.flowChart.modules;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.jojo.flow.model.Warning;
import org.jojo.flow.model.data.Data;
import org.jojo.flow.model.data.DataSignature;
import org.jojo.flow.model.data.Fraction;
import org.jojo.flow.model.data.Unit;
import org.jojo.flow.model.flowChart.FlowChartElement;
import org.jojo.flow.model.flowChart.ValidationException;
import org.jojo.flow.model.flowChart.connections.Connection;
import org.jojo.flow.model.flowChart.connections.StdArrow;

public abstract class Module extends FlowChartElement implements Comparable<Module> {
    private final ExternalConfig externalConfig;
    
    public Module(final ExternalConfig externalConfig) {
        this.externalConfig = externalConfig;
    }
    
    public abstract List<ModulePin> getAllModulePins();
    public abstract Unit<Fraction> getFrequency();
    public abstract void run() throws Exception;
    
    public StdArrow validate() throws ValidationException {
        final StdArrow ret = checkInputDataTypes();
        if (ret == null) { // everything ok
            putDefaultDataOnOutgoingConnections();
        }
        return ret;
    }

    private StdArrow checkInputDataTypes() throws ValidationException {
        if (getStdInputs().stream().anyMatch(x -> !(x.getModulePinImp() instanceof StdPin))) {
            throw new ValidationException(new Warning(this, "an input module pin-imp is not std", true));
        }
        
        final List<StdPin> allInputPins = getStdInputs()
                .stream()
                .map(x -> (StdPin)x.getModulePinImp())
                .collect(Collectors.toList());
        for (final StdPin pin : allInputPins) {
            Data data = pin.getDefaultData();
            if (data == null) {
                throw new ValidationException(new Warning(this, "an input pin of this module has not specified default data", true));
            }
            
            final DataSignature pinDataSignature = pin.getCheckDataSignature();
            if (pinDataSignature == null) {
                throw new ValidationException(new Warning(this, "an input pin of this module has not specified a data signature", true));
            } else if (!pinDataSignature.isCheckingRecursive()) {
                throw new ValidationException(new Warning(this, "this module has not overriden validate, although it had to", true));
            }
            
            final StdArrow arrow = pin.getConnections().isEmpty() ? null : (StdArrow) (pin.getConnections().get(0));
            data = arrow == null ? null : arrow.getData();
            if (data == null) { // TODO schauen, dass data vor validation auf allen pfeilen null ist 
                // we are a module in a cycle (selected as root). However, we still need to check other pins
                continue;
            }
            
            if (!data.hasSameType(pinDataSignature)) {
                return arrow;
            }
        }
        return null;
    }
    
    private void putDefaultDataOnOutgoingConnections() throws ValidationException {
        final List<OutputPin> allOutputPins = getStdOutputs();
        if (allOutputPins.stream().anyMatch(x -> !(x.getModulePinImp() instanceof StdPin))) {
            throw new ValidationException(new Warning(this, "an output module pin-imp is not std", true));
        }
        
        for (final OutputPin pin : allOutputPins) {
            for (final Connection connection : pin.getConnections()) {
                final StdArrow arrow = (StdArrow)connection;
                final Data data = pin.getModulePinImp().getDefaultData();
                if (data == null) {
                    throw new ValidationException(new Warning(this, "an output pin of this module has not specified default data", true));
                }
                arrow.putData(data);
            }
        }
    }

    public abstract void setInternalConfig(final InternalConfig internalConfig);
    public abstract InternalConfig getInternalConfig();
    
    public ExternalConfig getExternalConfig() {
        return this.externalConfig;
    }
    
    public abstract List<InputPin> getStdInputs();
    public abstract List<OutputPin> getStdOutputs();
    public abstract List<InputPin> getAllInputs();
    public abstract List<OutputPin> getAllOutputs();
    
    public List<Module> getStdDependencyList() {
        final Set<Module> retSet = new HashSet<>();
        retSet.addAll(getStdInputs()
                .stream()
                .map(x -> x.getConnections())
                .flatMap(x -> x.stream())
                .map(x -> x.getFromPin())
                .map(x -> x.getModule())
                .collect(Collectors.toList()));
        return retSet.stream().collect(Collectors.toList());
    }
    
    public List<Module> getStdAdjacencyList() {
        final Set<Module> retSet = new HashSet<>();
        retSet.addAll(getStdOutputs()
                .stream()
                .map(x -> x.getConnections())
                .flatMap(x -> x.stream())
                .map(x -> x.getToPins())
                .flatMap(x -> x.stream())
                .map(x -> x.getModule())
                .collect(Collectors.toList()));
        return retSet.stream().collect(Collectors.toList());
    }
    
    @Override
    public int hashCode() {
        return this.externalConfig.hashCode();
    }
    
    @Override
    public boolean equals(final Object other) {
        if (other != null && other instanceof Module) {
            return this.externalConfig.equals(((Module)other).externalConfig);
        }
        return false;
    }
}
