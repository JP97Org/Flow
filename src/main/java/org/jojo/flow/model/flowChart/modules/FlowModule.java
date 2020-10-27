package org.jojo.flow.model.flowChart.modules;

import static org.jojo.flow.model.util.OK.ok;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.jojo.flow.exc.ParsingException;
import org.jojo.flow.exc.ValidationException;
import org.jojo.flow.exc.Warning;
import org.jojo.flow.model.api.DOMStringUnion;
import org.jojo.flow.model.api.IConnection;
import org.jojo.flow.model.api.IDOM;
import org.jojo.flow.model.api.IData;
import org.jojo.flow.model.api.IDataSignature;
import org.jojo.flow.model.api.IDefaultArrow;
import org.jojo.flow.model.api.IDefaultPin;
import org.jojo.flow.model.api.IExternalConfig;
import org.jojo.flow.model.api.IFlowModule;
import org.jojo.flow.model.api.IInputPin;
import org.jojo.flow.model.api.IObserver;
import org.jojo.flow.model.api.IOutputPin;
import org.jojo.flow.model.api.ISubject;
import org.jojo.flow.model.api.IInternalConfig;
import org.jojo.flow.model.api.IModulePin;
import org.jojo.flow.model.data.Fraction;
import org.jojo.flow.model.data.units.Frequency;
import org.jojo.flow.model.flowChart.FlowChartElement;
import org.jojo.flow.model.flowChart.connections.DefaultArrow;
import org.jojo.flow.model.storeLoad.ConfigDOM;
import org.jojo.flow.model.storeLoad.GraphicalRepresentationDOM;
import org.jojo.flow.model.storeLoad.ModuleDOM;
import org.jojo.flow.model.util.OK;

public abstract class FlowModule extends FlowChartElement implements IObserver, IFlowModule {
    private final IExternalConfig externalConfig;
    
    public FlowModule(final int id, final IExternalConfig externalConfig) {
        super(id);
        this.externalConfig = Objects.requireNonNull(externalConfig);
        this.externalConfig.setModule(this);
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
    
    @Override
    public ISubject getObserved() {
        return getExternalConfig();
    }

    @Override
    public List<ISubject> getOtherObservedSubjects() {
        return new ArrayList<>();
    }
    
    @Override
    public abstract List<IModulePin> getAllModulePins();
    
    /**
     * Sets all module pins with the given pins DOM.
     * 
     * @param pinsDom - the given pins DOM
     */
    protected abstract void setAllModulePins(IDOM pinsDom);
    
    /**
     * Determines whether the given pins DOM is valid.
     * 
     * @param pinsDom - the given pins DOM
     * @return whether the given pins DOM is valid
     */
    protected abstract boolean isPinsDOMValid(IDOM pinsDom);
    
    @Override
    public abstract Frequency<Fraction> getFrequency();
    
    @Override
    public abstract void run() throws Exception;
    
    @Override
    public IDefaultArrow validate() throws ValidationException {
        IDefaultArrow ret = checkInputDataTypes();
        if (ret == null) { // input data types ok
            ret = putDefaultDataOnOutgoingConnections();
        }
        return ret;
    }

    private DefaultArrow checkInputDataTypes() throws ValidationException {
        if (getDefaultInputs().stream().anyMatch(x -> !(x.getModulePinImp() instanceof DefaultPin))) {
            throw new ValidationException(new Warning(this, "an input module pin-imp is not default", true));
        }
        
        final List<DefaultPin> allInputPins = getDefaultInputs()
                .stream()
                .map(x -> (DefaultPin)x.getModulePinImp())
                .collect(Collectors.toList());
        for (final DefaultPin pin : allInputPins) {
            IData data = pin.getDefaultData();
            if (data == null) {
                throw new ValidationException(new Warning(this, "an input pin of this module has not specified default data", true));
            }
            
            final IDataSignature pinDataSignature = pin.getCheckDataSignature();
            if (pinDataSignature == null) {
                throw new ValidationException(new Warning(this, "an input pin of this module has not specified a data signature", true));
            } else if (!pinDataSignature.isCheckingRecursive()) {
                throw new ValidationException(new Warning(this, "this module has not overriden validate, although it had to", true));
            }
            
            final DefaultArrow arrow = pin.getConnections().isEmpty() ? null : (DefaultArrow) (pin.getConnections().get(0));
            data = arrow == null ? null : arrow.getData();
            if (data == null) {
                // We are a module selected as a root on this pin arrow cycle.
                // However, we still need to check other pins.
                continue;
            }
            
            if (!data.hasSameType(pinDataSignature)) {
                return arrow;
            }
        }
        return null;
    }
    
    private IDefaultArrow putDefaultDataOnOutgoingConnections() throws ValidationException {
        final List<IOutputPin> allOutputPins = getDefaultOutputs();
        if (allOutputPins.stream().anyMatch(x -> !(x.getModulePinImp() instanceof DefaultPin))) {
            throw new ValidationException(new Warning(this, "an output module pin-imp is not default", true));
        }
        
        for (final IOutputPin pin : allOutputPins) {
            for (final IConnection connection : pin.getConnections()) {
                final IDefaultArrow arrow = (IDefaultArrow)connection;
                final IData data = pin.getModulePinImp().getDefaultData();
                if (data == null) {
                    throw new ValidationException(new Warning(this, "an output pin of this module has not specified default data", true));
                }
                if (!arrow.putDataSignature(data.getDataSignature().getCopy()) || !arrow.putData(data)) {
                    return arrow;
                }
            }
        }
        return null;
    }

    @Override
    public abstract void setInternalConfig(final IDOM internalConfigDOM);
    
    @Override
    public abstract boolean isInternalConfigDOMValid(final IDOM internalConfigDOM);
    
    @Override
    public abstract IInternalConfig getInternalConfig();
    
    @Override
    public final boolean hasInternalConfig() {
        return getInternalConfig() != null;
    }
    
    @Override
    public final IExternalConfig getExternalConfig() {
        return this.externalConfig;
    }
    
    @Override
    public final List<IInputPin> getDefaultInputs() {
        return getAllInputs()
                .stream()
                .filter(p -> p.getModulePinImp() instanceof IDefaultPin)
                .collect(Collectors.toList());
    }
    
    @Override
    public final List<IOutputPin> getDefaultOutputs() {
        return getAllOutputs()
                .stream()
                .filter(p -> p.getModulePinImp() instanceof IDefaultPin)
                .collect(Collectors.toList());
    }
    
    @Override
    public final List<IInputPin> getAllInputs() {
        return this.getAllModulePins()
                .stream()
                .filter(x -> x instanceof IInputPin)
                .map(x -> (InputPin)x)
                .collect(Collectors.toList());
    }
    
    @Override
    public final List<IOutputPin> getAllOutputs() {
        return this.getAllModulePins()
                .stream()
                .filter(x -> x instanceof IOutputPin)
                .map(x -> (OutputPin)x)
                .collect(Collectors.toList());
    }
    
    @Override
    public final List<IFlowModule> getDefaultDependencyList() {
        final Set<IFlowModule> retSet = new HashSet<>();
        retSet.addAll(getDefaultInputs()
                .stream()
                .map(x -> x.getConnections())
                .flatMap(x -> x.stream())
                .map(x -> x.getFromPin())
                .map(x -> x.getModule())
                .collect(Collectors.toList()));
        return retSet.stream().collect(Collectors.toList());
    }
    
    @Override
    public final List<IFlowModule> getDefaultAdjacencyList() {
        final Set<IFlowModule> retSet = new HashSet<>();
        retSet.addAll(getDefaultOutputs()
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
    public final int compareTo(final IFlowModule other) {
        int ret = this.externalConfig.compareTo(other.getExternalConfig());
        if (ret == 0) {
            ret = Integer.valueOf(getId()).compareTo(other.getId());
        }
        return ret;
    }
    
    @Override
    public IDOM getDOM() {
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
    public void restoreFromDOM(final IDOM dom) {
        if (isDOMValid(dom)) {
            final Map<String, DOMStringUnion> domMap = dom.getDOMMap();
            final IDOM idDom = (IDOM)domMap.get(ModuleDOM.NAME_ID).getValue();
            setId(Integer.parseInt(idDom.elemGet()));
            if (domMap.containsKey(ConfigDOM.NAME_INT_CONFIG)) {
                final IDOM intDom = (IDOM)domMap.get(ConfigDOM.NAME_INT_CONFIG).getValue();
                setInternalConfig(intDom);
            }
            final IDOM extDom = (IDOM)domMap.get(ConfigDOM.NAME_EXT_CONFIG).getValue();
            this.externalConfig.restoreFromDOM(extDom);
            final IDOM grDom = (IDOM)domMap.get(GraphicalRepresentationDOM.NAME).getValue();
            getGraphicalRepresentation().restoreFromDOM(grDom);
            final IDOM pinsDom = (IDOM)domMap.get(ModuleDOM.NAME_PINS).getValue();
            setAllModulePins(pinsDom);
            notifyObservers();
        }
    }
    
    @Override
    public boolean isDOMValid(IDOM dom) {
        Objects.requireNonNull(dom);
        final Map<String, DOMStringUnion> domMap = dom.getDOMMap();
        try {
            ok(domMap.get(ModuleDOM.NAME_ID).isDOM(), OK.ERR_MSG_WRONG_CAST);
            final IDOM idDom = (IDOM)domMap.get(ModuleDOM.NAME_ID).getValue();
            ok(x -> Integer.parseInt(idDom.elemGet()), "");
            if (domMap.containsKey(ConfigDOM.NAME_INT_CONFIG)) {
                ok(domMap.get(ConfigDOM.NAME_INT_CONFIG).isDOM(), OK.ERR_MSG_WRONG_CAST);
                final IDOM intDom = (IDOM)domMap.get(ConfigDOM.NAME_INT_CONFIG).getValue();
                ok(isInternalConfigDOMValid(intDom), "Int.Config " + OK.ERR_MSG_DOM_NOT_VALID);
            }
            ok(domMap.get(ConfigDOM.NAME_EXT_CONFIG).isDOM(), OK.ERR_MSG_WRONG_CAST);
            final IDOM extDom = (IDOM)domMap.get(ConfigDOM.NAME_EXT_CONFIG).getValue();
            ok(this.externalConfig.isDOMValid(extDom), "EXT_CONFIG " + OK.ERR_MSG_DOM_NOT_VALID);
            ok(domMap.get(GraphicalRepresentationDOM.NAME).isDOM(), OK.ERR_MSG_WRONG_CAST);
            final IDOM grDom = (IDOM)domMap.get(GraphicalRepresentationDOM.NAME).getValue();
            ok(getGraphicalRepresentation().isDOMValid(grDom), "ModuleGR " + OK.ERR_MSG_DOM_NOT_VALID);
            ok(domMap.get(ModuleDOM.NAME_PINS).isDOM(), OK.ERR_MSG_WRONG_CAST);
            final IDOM pinsDom = (IDOM)domMap.get(ModuleDOM.NAME_PINS).getValue();
            ok(isPinsDOMValid(pinsDom), "Pins " + OK.ERR_MSG_DOM_NOT_VALID);
            return true;
        } catch (ParsingException e) {
            e.getWarning().setAffectedElement(this).reportWarning();
            return false;
        }
    }
    
    @Override
    public String toString() {
        final List<IModulePin> allPins = new ArrayList<>(this.getAllModulePins());
        allPins.sort(ModulePin.getComparator());
        return "ID= " + this.getId() + " | " + this.externalConfig.toString() 
                    + " | allPins= " + allPins
                    + " | allConnectionsOfAllPins= " 
                    + allPins.stream().map(p -> p.getConnections()).collect(Collectors.toList());
    }
}
