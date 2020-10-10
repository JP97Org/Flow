package org.jojo.flow.model.flowChart.modules;

import static org.jojo.flow.model.storeLoad.OK.ok;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.jojo.flow.IObserver;
import org.jojo.flow.ISubject;
import org.jojo.flow.model.Warning;
import org.jojo.flow.model.data.Data;
import org.jojo.flow.model.data.DataSignature;
import org.jojo.flow.model.data.Fraction;
import org.jojo.flow.model.data.units.Frequency;
import org.jojo.flow.model.flowChart.FlowChartElement;
import org.jojo.flow.model.flowChart.ValidationException;
import org.jojo.flow.model.flowChart.connections.Connection;
import org.jojo.flow.model.flowChart.connections.StdArrow;
import org.jojo.flow.model.storeLoad.ConfigDOM;
import org.jojo.flow.model.storeLoad.DOM;
import org.jojo.flow.model.storeLoad.GraphicalRepresentationDOM;
import org.jojo.flow.model.storeLoad.ModuleDOM;
import org.jojo.flow.model.storeLoad.OK;
import org.jojo.flow.model.storeLoad.ParsingException;

public abstract class FlowModule extends FlowChartElement implements Comparable<FlowModule>, IObserver {
    private final ExternalConfig externalConfig;
    
    public FlowModule(final int id, final ExternalConfig externalConfig) {
        super(id);
        this.externalConfig = externalConfig;
        this.externalConfig.registerObserver(this);
    }
    
    @Override
    public final void update(ISubject subject, Object argument) {
        update();
    }
    
    @Override
    public final void update() {
        notifyObservers(getExternalConfig());
    }
    
    public abstract List<ModulePin> getAllModulePins();
    protected abstract void setAllModulePins(DOM pinsDom);
    protected abstract boolean isPinsDOMValid(DOM pinsDom);
    
    public abstract Frequency<Fraction> getFrequency();
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

    public abstract void setInternalConfig(final DOM internalConfigDOM);
    public abstract boolean isInternalConfigDOMValid(final DOM internalConfigDOM);
    public abstract InternalConfig getInternalConfig();
    
    public final boolean hasInternalConfig() {
        return getInternalConfig() != null;
    }
    
    public final ExternalConfig getExternalConfig() {
        return this.externalConfig;
    }
    
    public final List<InputPin> getStdInputs() {
        return getAllInputs()
                .stream()
                .filter(p -> p.getModulePinImp() instanceof StdPin)
                .collect(Collectors.toList());
    }
    
    public final List<OutputPin> getStdOutputs() {
        return getAllOutputs()
                .stream()
                .filter(p -> p.getModulePinImp() instanceof StdPin)
                .collect(Collectors.toList());
    }
    
    public final List<InputPin> getAllInputs() {
        return this.getAllModulePins()
                .stream()
                .filter(x -> x instanceof InputPin)
                .map(x -> (InputPin)x)
                .collect(Collectors.toList());
    }
    
    public final List<OutputPin> getAllOutputs() {
        return this.getAllModulePins()
                .stream()
                .filter(x -> x instanceof OutputPin)
                .map(x -> (OutputPin)x)
                .collect(Collectors.toList());
    }
    
    public final List<FlowModule> getStdDependencyList() {
        final Set<FlowModule> retSet = new HashSet<>();
        retSet.addAll(getStdInputs()
                .stream()
                .map(x -> x.getConnections())
                .flatMap(x -> x.stream())
                .map(x -> x.getFromPin())
                .map(x -> x.getModule())
                .collect(Collectors.toList()));
        return retSet.stream().collect(Collectors.toList());
    }
    
    public final List<FlowModule> getStdAdjacencyList() {
        final Set<FlowModule> retSet = new HashSet<>();
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
        if (other != null && other instanceof FlowModule) {
            return super.equals(other) && this.externalConfig.equals(((FlowModule)other).externalConfig);
        }
        return false;
    }
    
    @Override
    public final int compareTo(final FlowModule other) {
        int ret = this.externalConfig.compareTo(other.externalConfig);
        if (ret == 0) {
            ret = Integer.valueOf(getId()).compareTo(other.getId());
        }
        return ret;
    }
    
    @Override
    public DOM getDOM() {
        final ModuleDOM dom = new ModuleDOM();
        dom.setClassName(getClass().getName());
        dom.setName(getExternalConfig().getName());
        dom.setID(getId());
        if (hasInternalConfig()) {
            dom.setInternalConfig(getInternalConfig());
        }
        dom.setExternalConfig(getExternalConfig());
        dom.setGraphicalRepresentation(getGraphicalRepresentation());
        dom.setPins(getAllModulePins());
        return dom;
    }
    
    @Override
    public void restoreFromDOM(final DOM dom) {
        if (isDOMValid(dom)) {
            final Map<String, Object> domMap = dom.getDOMMap();
            final DOM idDom = (DOM)domMap.get(ModuleDOM.NAME_ID);
            setId(Integer.parseInt(idDom.elemGet()));
            if (domMap.containsKey(ConfigDOM.NAME_INT_CONFIG)) {
                final DOM intDom = (DOM)domMap.get(ConfigDOM.NAME_INT_CONFIG);
                setInternalConfig(intDom);
            }
            final DOM extDom = (DOM)domMap.get(ConfigDOM.NAME_EXT_CONFIG);
            this.externalConfig.restoreFromDOM(extDom);
            final DOM grDom = (DOM)domMap.get(GraphicalRepresentationDOM.NAME);
            getGraphicalRepresentation().restoreFromDOM(grDom);
            final DOM pinsDom = (DOM)domMap.get(ModuleDOM.NAME_PINS);
            setAllModulePins(pinsDom);
            notifyObservers();
        }
    }
    
    @Override
    public boolean isDOMValid(DOM dom) {
        Objects.requireNonNull(dom);
        final Map<String, Object> domMap = dom.getDOMMap();
        try {
            ok(domMap.get(ModuleDOM.NAME_ID) instanceof DOM, OK.ERR_MSG_WRONG_CAST);
            final DOM idDom = (DOM)domMap.get(ModuleDOM.NAME_ID);
            ok(x -> Integer.parseInt(idDom.elemGet()), "");
            if (domMap.containsKey(ConfigDOM.NAME_INT_CONFIG)) {
                ok(domMap.get(ConfigDOM.NAME_INT_CONFIG) instanceof DOM, OK.ERR_MSG_WRONG_CAST);
                final DOM intDom = (DOM)domMap.get(ConfigDOM.NAME_INT_CONFIG);
                ok(isInternalConfigDOMValid(intDom), "Int.Config " + OK.ERR_MSG_DOM_NOT_VALID);
            }
            ok((DOM)domMap.get(ConfigDOM.NAME_EXT_CONFIG) instanceof DOM, OK.ERR_MSG_WRONG_CAST);
            final DOM extDom = (DOM)domMap.get(ConfigDOM.NAME_EXT_CONFIG);
            this.externalConfig.isDOMValid(extDom);
            ok((DOM)domMap.get(GraphicalRepresentationDOM.NAME) instanceof DOM, OK.ERR_MSG_WRONG_CAST);
            final DOM grDom = (DOM)domMap.get(GraphicalRepresentationDOM.NAME);
            getGraphicalRepresentation().isDOMValid(grDom);
            ok((DOM)domMap.get(ModuleDOM.NAME_PINS) instanceof DOM, OK.ERR_MSG_WRONG_CAST);
            final DOM pinsDom = (DOM)domMap.get(ModuleDOM.NAME_PINS);
            ok(isPinsDOMValid(pinsDom), "Pins " + OK.ERR_MSG_DOM_NOT_VALID);
            return true;
        } catch (ParsingException e) {
            e.getWarning().setAffectedElement(this).reportWarning();
            return false;
        }
    }
}
