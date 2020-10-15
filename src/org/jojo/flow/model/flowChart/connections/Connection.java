package org.jojo.flow.model.flowChart.connections;

import static org.jojo.flow.model.storeLoad.OK.ok;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.jojo.flow.model.FlowException;
import org.jojo.flow.model.Warning;
import org.jojo.flow.model.data.DataSignature;
import org.jojo.flow.model.flowChart.FlowChartElement;
import org.jojo.flow.model.flowChart.modules.InputPin;
import org.jojo.flow.model.flowChart.modules.ListSizeException;
import org.jojo.flow.model.flowChart.modules.ModulePin;
import org.jojo.flow.model.flowChart.modules.ModulePinImp;
import org.jojo.flow.model.flowChart.modules.OutputPin;
import org.jojo.flow.model.flowChart.modules.DefaultPin;
import org.jojo.flow.model.storeLoad.ConnectionDOM;
import org.jojo.flow.model.storeLoad.DOM;
import org.jojo.flow.model.storeLoad.DynamicObjectLoader;
import org.jojo.flow.model.storeLoad.FlowChartDOM;
import org.jojo.flow.model.storeLoad.GraphicalRepresentationDOM;
import org.jojo.flow.model.storeLoad.ModulePinDOM;
import org.jojo.flow.model.storeLoad.OK;
import org.jojo.flow.model.storeLoad.ParsingException;

public abstract class Connection extends FlowChartElement {
    private final List<InputPin> toPins;
    private OutputPin fromPin;
    private String name;
    
    public Connection(final int id, final OutputPin fromPin, final String name) throws ConnectionException {
        super(id);
        this.toPins = new ArrayList<>();
        this.fromPin = fromPin;
        this.name = name;
        
        final boolean connectionMatchesPins = connectionMatchesPins();
        if (!connectionMatchesPins) {
            throw new ConnectionException("fromPin to be set does not match connection type", this);
        }
    }
    
    public boolean connect() {
        if (!this.toPins.isEmpty()) {
            try {
                this.fromPin.addConnection(this);
            } catch (ListSizeException e) {
                // should not happen
                e.printStackTrace();
            }
            for (final var p : this.toPins) {
                try {
                    p.addConnection(this);
                } catch (ListSizeException e) {
                    disconnect();
                    e.getWarning().reportWarning();
                    new Warning(e.getWarning()).setAffectedElement(this).reportWarning();
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    public void disconnect() {
        this.fromPin.removeConnection(this);
        this.toPins.forEach(p -> p.removeConnection(this));
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public abstract String getInfo();
    
    public OutputPin getFromPin() {
        return this.fromPin;
    }
    
    public synchronized List<InputPin> getToPins() {
        return new ArrayList<>(this.toPins);
    }
    
    public boolean isPinImpInConnection(final ModulePinImp modulePinImp) {
        final List<ModulePin> allPins = getToPins()
                .stream()
                .map(x -> (ModulePin)x)
                .collect(Collectors.toList());
        allPins.add(getFromPin());
        return allPins.stream().anyMatch(p -> p.getModulePinImp().equals(modulePinImp));
    }
    
    public synchronized boolean addToPin(final InputPin toPin) throws ConnectionException {
        this.toPins.add(toPin);
        final boolean connectionMatchesPins = connectionMatchesPins();
        final boolean ok = connectionMatchesPins && checkDataTypes();
        if (!ok) {
            this.toPins.remove(this.toPins.size() - 1);
        }
        if (!connectionMatchesPins) {
            throw new ConnectionException("toPin to be added does not match connection type", this);
        }
        if (ok) {
            notifyObservers(toPin);
        }
        return ok;
    }
    
    public synchronized boolean removeToPin(final InputPin toPin) {
        final boolean ret = this.toPins.remove(toPin);
        if (ret) {
            notifyObservers(toPin);
        }
        return ret;
    }
    
    public synchronized boolean removeToPin(final int index) {
        if (index >= this.toPins.size()) {
            return false;
        }
        final InputPin toPin = this.toPins.get(index);
        this.toPins.remove(index);
        notifyObservers(toPin);
        return true;
    }
    
    public synchronized boolean setFromPin(final OutputPin fromPin) throws ConnectionException {
        final OutputPin before = this.fromPin;
        this.fromPin = fromPin;
        final boolean connectionMatchesPins = connectionMatchesPins();
        final boolean ok = connectionMatchesPins && checkDataTypes();
        if (!ok) {
            this.fromPin = before;
        }
        if (!connectionMatchesPins) {
            throw new ConnectionException("fromPin to be set does not match connection type", this);
        }
        if (ok) {
            notifyObservers(fromPin);
        }
        return ok;
    }
    
    protected abstract boolean connectionMatchesPins();
    
    protected abstract boolean checkDataTypes();
    
    @Override
    public DOM getDOM() {
        final ConnectionDOM dom = new ConnectionDOM();
        dom.setName(getName());
        dom.setID(getId());
        dom.setClassName(getClass().getName());
        dom.setGraphicalRepresentation(getGraphicalRepresentation());
        dom.setFromPin(getFromPin());
        dom.setToPins(getToPins());
        return dom;
    }

    @Override
    public void restoreFromDOM(final DOM dom) {
        if (isDOMValid(dom)) {
            this.getToPins().forEach(p -> removeToPin(p));
            final Map<String, Object> domMap = dom.getDOMMap();
            final String nameStr = (String)domMap.get(ConnectionDOM.NAME_NAME);
            setName(nameStr);
            final DOM idDom = (DOM)domMap.get(ConnectionDOM.NAME_ID);
            final String idStr = idDom.elemGet();
            final int id = Integer.parseInt(idStr);
            setId(id);
            DOM fromDom = (DOM)domMap.get(ConnectionDOM.NAME_FROM_PIN);
            fromDom = (DOM)fromDom.getDOMMap().get(ModulePinDOM.NAME);
            final DOM cnFromDom = (DOM) (fromDom.getDOMMap().get(ModulePinDOM.NAME_CLASSNAME));
            final String pinToLoadFrom = cnFromDom.elemGet();
            final DOM cnDomImpFrom = (DOM) (fromDom.getDOMMap().get(ModulePinDOM.NAME_CLASSNAME_IMP));
            final String pinToLoadImpFrom = cnDomImpFrom.elemGet();
            final ModulePin pinFrom = DynamicObjectLoader.loadPin(pinToLoadFrom, pinToLoadImpFrom);
            pinFrom.restoreFromDOM(fromDom);
            try {
                setFromPin((OutputPin)pinFrom);
            } catch (ConnectionException e1) {
                // should not happen
                e1.printStackTrace();
            }
            final DOM toDoms = (DOM)domMap.get(ConnectionDOM.NAME_TO_PINS);
            final Map<String, Object> toMap = toDoms.getDOMMap();
            for (final var toObj : toMap.values()) {
                if (toObj instanceof DOM) {
                    final DOM toDom = (DOM)toObj;
                    final DOM cnDom = (DOM) (toDom.getDOMMap().get(ModulePinDOM.NAME_CLASSNAME));
                    final String pinToLoad = cnDom.elemGet();
                    final DOM cnDomImp = (DOM) (toDom.getDOMMap().get(ModulePinDOM.NAME_CLASSNAME_IMP));
                    final String pinToLoadImp = cnDomImp.elemGet();
                    final ModulePin pin = DynamicObjectLoader.loadPin(pinToLoad, pinToLoadImp);
                    pin.restoreFromDOM(toDom);
                    try {
                        addToPin((InputPin)pin);
                    } catch (ConnectionException e) {
                        // should not happen
                        e.printStackTrace();
                    }
                }
            }
            final DOM grDom = (DOM)domMap.get(GraphicalRepresentationDOM.NAME);
            getGraphicalRepresentation().restoreFromDOM(grDom);
            notifyObservers();
        }
    }
    
    @Override
    public boolean isDOMValid(final DOM dom) {
        Objects.requireNonNull(dom);
        final Map<String, Object> domMap = dom.getDOMMap();
        try {
            ok(domMap.get(ConnectionDOM.NAME_NAME) instanceof String, OK.ERR_MSG_WRONG_CAST);
            final String nameStr = (String)domMap.get(ConnectionDOM.NAME_NAME);
            ok(nameStr != null, OK.ERR_MSG_NULL);
            ok(domMap.get(FlowChartDOM.NAME_ID) instanceof DOM, OK.ERR_MSG_WRONG_CAST);
            final DOM idDom = (DOM)domMap.get(FlowChartDOM.NAME_ID);
            final String idStr = idDom.elemGet();
            ok(idStr != null, OK.ERR_MSG_NULL);
            ok(domMap.get(ConnectionDOM.NAME_FROM_PIN) instanceof DOM, OK.ERR_MSG_WRONG_CAST);
            DOM fromDom = (DOM)domMap.get(ConnectionDOM.NAME_FROM_PIN);
            ok(fromDom.getDOMMap().get(ModulePinDOM.NAME) instanceof DOM, OK.ERR_MSG_WRONG_CAST);
            fromDom = (DOM)fromDom.getDOMMap().get(ModulePinDOM.NAME);
            ok(fromDom.getDOMMap().get(ModulePinDOM.NAME_CLASSNAME) instanceof DOM, OK.ERR_MSG_WRONG_CAST);
            final DOM cnFromDom = (DOM) (fromDom.getDOMMap().get(ModulePinDOM.NAME_CLASSNAME));
            final String pinToLoadFrom = cnFromDom.elemGet();
            ok(pinToLoadFrom != null, OK.ERR_MSG_NULL);
            ok(fromDom.getDOMMap().get(ModulePinDOM.NAME_CLASSNAME_IMP) instanceof DOM, OK.ERR_MSG_WRONG_CAST);
            final DOM cnDomImpFrom = (DOM) (fromDom.getDOMMap().get(ModulePinDOM.NAME_CLASSNAME_IMP));
            final String pinToLoadImpFrom = cnDomImpFrom.elemGet();
            ok(pinToLoadImpFrom != null, OK.ERR_MSG_NULL);
            final ModulePin pinFrom = ok(x -> DynamicObjectLoader.loadPin(pinToLoadFrom, pinToLoadImpFrom), "");
            final var before = getFromPin();
            final var fromDomFinal = fromDom;
            ok(ok(x -> {try {
                final DataSignature checkSignBefore = pinFrom.getModulePinImp() instanceof DefaultPin 
                        ? ((DefaultPin)pinFrom.getModulePinImp()).getCheckDataSignature() : null;
                if (checkSignBefore != null) {
                    final var copy = checkSignBefore.getCopy();
                    copy.deactivateChecking();
                    try {
                        ((DefaultPin)pinFrom.getModulePinImp()).setCheckDataSignature(copy);
                    } catch (FlowException e) {
                        // should not happen
                        e.printStackTrace();
                        return false;
                    }
                }
                final boolean debugOk = pinFrom.isDOMValid(fromDomFinal);
                if (!debugOk) { //TODO remove this debugOK
                    System.err.println("debug");
                }
                final boolean ok = pinFrom.isDOMValid(fromDomFinal) && setFromPin((OutputPin)pinFrom);
                setFromPin(before);
                return ok;
            } catch (ConnectionException e1) {
                try {
                    setFromPin(before);
                } catch (ConnectionException e) {
                    //should not happen
                    e.printStackTrace();
                }
                return false;
            }}, "").booleanValue(), "from pin setting failed");
            ok(domMap.get(ConnectionDOM.NAME_TO_PINS) instanceof DOM, OK.ERR_MSG_WRONG_CAST);
            final DOM toDoms = (DOM)domMap.get(ConnectionDOM.NAME_TO_PINS);
            final Map<String, Object> toMap = toDoms.getDOMMap();
            for (final var toObj : toMap.values()) {
                if (toObj instanceof DOM) {
                    final DOM toDom = (DOM)toObj;
                    ok(toDom.getDOMMap().get(ModulePinDOM.NAME_CLASSNAME) instanceof DOM, OK.ERR_MSG_WRONG_CAST);
                    final DOM cnDom = (DOM) (toDom.getDOMMap().get(ModulePinDOM.NAME_CLASSNAME));
                    final String pinToLoad = cnDom.elemGet();
                    ok(pinToLoad != null, OK.ERR_MSG_NULL);
                    ok(toDom.getDOMMap().get(ModulePinDOM.NAME_CLASSNAME_IMP) instanceof DOM, OK.ERR_MSG_WRONG_CAST);
                    final DOM cnDomImp = (DOM) (toDom.getDOMMap().get(ModulePinDOM.NAME_CLASSNAME_IMP));
                    final String pinToLoadImp = cnDomImp.elemGet();
                    ok(pinToLoadImp != null, OK.ERR_MSG_NULL);
                    final ModulePin pin = ok(x -> DynamicObjectLoader.loadPin(pinToLoad, pinToLoadImp), "");
                    ok(ok(x -> {try {
                        final DataSignature checkSignBefore = pin.getModulePinImp() instanceof DefaultPin 
                                ? ((DefaultPin)pin.getModulePinImp()).getCheckDataSignature() : null;
                        if (checkSignBefore != null) {
                            final var copy = checkSignBefore.getCopy();
                            copy.deactivateChecking();
                            try {
                                ((DefaultPin)pin.getModulePinImp()).setCheckDataSignature(copy);
                            } catch (FlowException e) {
                                // should not happen
                                e.printStackTrace();
                                return false;
                            }
                        }
                        boolean ok = pin.isDOMValid(toDom) && addToPin((InputPin)pin);
                        removeToPin((InputPin)pin);
                        return ok;
                    } catch (ConnectionException e1) {
                        removeToPin((InputPin)pin);
                        return false;
                    }}, "").booleanValue(), "to pin adding failed");
                }
            }
            ok(domMap.get(GraphicalRepresentationDOM.NAME) instanceof DOM, OK.ERR_MSG_WRONG_CAST);
            final DOM grDom = (DOM)domMap.get(GraphicalRepresentationDOM.NAME);
            ok(getGraphicalRepresentation().isDOMValid(grDom), "ConnectionGR " + OK.ERR_MSG_DOM_NOT_VALID);
            return true;
        } catch (ParsingException e) {
            e.getWarning().setAffectedElement(this).reportWarning();
            return false;
        }
    }
}
