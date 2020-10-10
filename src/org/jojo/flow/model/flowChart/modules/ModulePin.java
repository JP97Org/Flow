package org.jojo.flow.model.flowChart.modules;

import static org.jojo.flow.model.storeLoad.OK.ok;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.jojo.flow.model.ModelFacade;
import org.jojo.flow.model.Subject;
import org.jojo.flow.model.data.Data;
import org.jojo.flow.model.data.DataSignature;
import org.jojo.flow.model.flowChart.GraphicalRepresentation;
import org.jojo.flow.model.flowChart.connections.Connection;
import org.jojo.flow.model.storeLoad.DOM;
import org.jojo.flow.model.storeLoad.DOMable;
import org.jojo.flow.model.storeLoad.GraphicalRepresentationDOM;
import org.jojo.flow.model.storeLoad.ModulePinDOM;
import org.jojo.flow.model.storeLoad.OK;
import org.jojo.flow.model.storeLoad.ParsingException;

public abstract class ModulePin extends Subject implements DOMable {
    private final ModulePinImp imp;
    private final ModulePinGR gr;
    
    public ModulePin(final ModulePinImp imp, final ModulePinGR gr) {
        this.imp = Objects.requireNonNull(imp);
        this.gr = Objects.requireNonNull(gr);
    }
    
    public GraphicalRepresentation getGraphicalRepresentation() {
        return this.gr;
    }
    
    public FlowModule getModule() {
        return this.imp.getModule();
    }
    
    private void setModule(final FlowModule module) {
        this.imp.setModule(module);
    }
    
    public synchronized List<Connection> getConnections() {
        return this.imp.getConnections();
    }
    
    public synchronized boolean addConnection(final Connection toAdd) throws ListSizeException {
        final boolean ret = this.imp.addConnection(toAdd);
        if (ret) {
            notifyObservers(toAdd);
        }
        return ret;
    }
    
    public synchronized boolean removeConnection(final Connection toRemove) {
        final boolean ret = this.imp.removeConnection(toRemove);
        if (ret) {
            notifyObservers(toRemove);
        }
        return ret;
    }
    
    public synchronized boolean removeConnection(final int index) {
        final Connection toRemove = index >= getConnections().size() ? null : getConnections().get(index);
        final boolean ret = this.imp.removeConnection(index);
        if (ret) {
            notifyObservers(toRemove);
        }
        return ret;
    }
    
    public Data getDefaultData() {
        return this.imp.getDefaultData();
    }
    
    protected void setDefaultData(final Data defaultData) {
        this.imp.setDefaultData(defaultData);
        notifyObservers(defaultData);
    }
    
    public ModulePinImp getModulePinImp() {
        return this.imp;
    }
    
    @Override
    public DOM getDOM() {
        final ModulePinDOM dom = new ModulePinDOM();
        dom.setClassName(getClass().getName());
        dom.setClassNameImp(getModulePinImp().getClass().getName());
        dom.setModuleID(getModule().getId());
        dom.setConnectionIDs(getConnections().stream().mapToInt(c -> c.getId()).toArray());
        try {
            dom.appendString("defaultData", getDefaultData().toSerializedString());
        } catch (ClassNotFoundException | IOException e) {
            // should not happen
            e.printStackTrace();
        }
        dom.setGraphicalRepresentation(getGraphicalRepresentation());
        if (getModulePinImp() instanceof StdPin) {
            final StdPin imp = (StdPin)getModulePinImp();
            dom.appendString("checkDataSignature", imp.getCheckDataSignature().toString());
        }
        return dom;
    }

    @Override
    public void restoreFromDOM(final DOM dom) {
        if (isDOMValid(dom)) {
            getConnections().forEach(c -> removeConnection(c));
            final Map<String, Object> domMap = dom.getDOMMap();
            final DOM modIdDom = (DOM)domMap.get(ModulePinDOM.NAME_MODULE_ID);
            final int modId = Integer.parseInt(modIdDom.elemGet());
            setModule((FlowModule)new ModelFacade().getElementById(modId));
            final DOM conIdsDom = (DOM)domMap.get(ModulePinDOM.NAME_CONNECTION_IDS);
            final Map<String, Object> conIdsMap = conIdsDom.getDOMMap();
            for (Object conIdObj : conIdsMap.values()) {
                final DOM conIdDom = (DOM)conIdObj;
                final int conId = Integer.parseInt(conIdDom.elemGet());
                final Connection con = (Connection)new ModelFacade().getElementById(conId);
                try {
                    addConnection(con);
                } catch (ListSizeException e) {
                    // should not happen
                    e.printStackTrace();
                }
            }
            final DOM defaultDataDom = (DOM)domMap.get("defaultData");
            final String dataStr = defaultDataDom.elemGet();
            try {
                setDefaultData(Data.ofSerializedString(dataStr));
            } catch (ClassNotFoundException | IOException e) {
                // should not happen
                e.printStackTrace();
            }
            final DOM grDom = (DOM)domMap.get(GraphicalRepresentationDOM.NAME);
            this.gr.restoreFromDOM(grDom);
            if (domMap.containsKey("checkDataSignature")) {
                final DOM cdsDom = (DOM)domMap.get("checkDataSignature");
                final String cdsString = cdsDom.elemGet();
                final DataSignature cds = DataSignature.of(cdsString);
                ((StdPin)this.imp).forceSetCheckDataSignature(cds);
            }
            notifyObservers();
        }
    }
    
    @Override
    public boolean isDOMValid(final DOM dom) {
        Objects.requireNonNull(dom);
        final Map<String, Object> domMap = dom.getDOMMap();
        try {
            ok(domMap.get(ModulePinDOM.NAME_CLASSNAME) instanceof DOM, OK.ERR_MSG_WRONG_CAST);
            final DOM cnDom = (DOM)domMap.get(ModulePinDOM.NAME_CLASSNAME);
            final String cn = cnDom.elemGet();
            ok(getClass().getName().equals(cn), OK.ERR_MSG_WRONG_CAST);
            ok(domMap.get(ModulePinDOM.NAME_CLASSNAME_IMP) instanceof DOM, OK.ERR_MSG_WRONG_CAST);
            final DOM cnDomImp = (DOM)domMap.get(ModulePinDOM.NAME_CLASSNAME_IMP);
            final String cnImp = cnDomImp.elemGet();
            ok(this.imp.getClass().getName().equals(cnImp), OK.ERR_MSG_WRONG_CAST);
            
            ok(domMap.get(ModulePinDOM.NAME_MODULE_ID) instanceof DOM, OK.ERR_MSG_WRONG_CAST);
            final DOM modIdDom = (DOM)domMap.get(ModulePinDOM.NAME_MODULE_ID);
            final int modId = ok(x -> Integer.parseInt(modIdDom.elemGet()), "");
            ok(ok(x -> (FlowModule)new ModelFacade().getElementById(modId), "") != null, OK.ERR_MSG_NULL);
            ok(domMap.get(ModulePinDOM.NAME_CONNECTION_IDS) instanceof DOM, OK.ERR_MSG_WRONG_CAST);
            final DOM conIdsDom = (DOM)domMap.get(ModulePinDOM.NAME_CONNECTION_IDS);
            final Map<String, Object> conIdsMap = conIdsDom.getDOMMap();
            for (Object conIdObj : conIdsMap.values()) {
                ok(conIdObj instanceof DOM, OK.ERR_MSG_WRONG_CAST);
                final DOM conIdDom = (DOM)conIdObj;
                final int conId = ok(x -> Integer.parseInt(conIdDom.elemGet()), "");
                final Connection con = ok(x -> (Connection)new ModelFacade().getElementById(conId), "");
                ok(ok(x -> {try {
                    addConnection(con);
                    removeConnection(con);
                    return true;
                } catch (ListSizeException e) {
                    return false;
                }}, "").booleanValue(), "connection could not be added to due ListSizeException");
            }
            ok(domMap.get("defaultData") instanceof DOM, OK.ERR_MSG_WRONG_CAST);
            final DOM defaultDataDom = (DOM)domMap.get("defaultData");
            final String dataStr = defaultDataDom.elemGet();
            ok(dataStr != null, OK.ERR_MSG_NULL);
            final Data before = getDefaultData();
            final String nullIsOk = ok(x -> {try {
                setDefaultData(Data.ofSerializedString(dataStr));
                return null;
            } catch (ClassNotFoundException | IOException e) {
                return e.toString();
            }}, "");
            setDefaultData(before);
            ok(nullIsOk == null, "this exc would occur: " + nullIsOk);
            ok(domMap.get(GraphicalRepresentationDOM.NAME) instanceof DOM, OK.ERR_MSG_WRONG_CAST);
            final DOM grDom = (DOM)domMap.get(GraphicalRepresentationDOM.NAME);
            ok(this.gr.isDOMValid(grDom), "ModulePinGR " + OK.ERR_MSG_DOM_NOT_VALID);
            if (domMap.containsKey("checkDataSignature")) {
                ok(domMap.get("checkDataSignature") instanceof DOM, OK.ERR_MSG_WRONG_CAST);
                final DOM cdsDom = (DOM)domMap.get("checkDataSignature");
                final String cdsString = cdsDom.elemGet();
                ok(cdsString != null, OK.ERR_MSG_NULL);
                final DataSignature cds = DataSignature.of(cdsString);
                ok(cds != null, OK.ERR_MSG_NULL);
            }
            return true;
        } catch (ParsingException e) {
            e.getWarning().setAffectedElement(getModule()).reportWarning();
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.gr);
    }
    
    @Override
    public boolean equals(final Object other) {
        if (other instanceof ModulePin) {
            return this.gr.equals(((ModulePin)other).gr);
        }
        return false;
    }
}
