package org.jojo.flow.model.flowChart.modules;

import static org.jojo.flow.model.storeLoad.OK.ok;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.jojo.flow.exc.ListSizeException;
import org.jojo.flow.exc.ParsingException;
import org.jojo.flow.model.ModelFacade;
import org.jojo.flow.model.Subject;
import org.jojo.flow.model.api.IConnection;
import org.jojo.flow.model.api.IDOMable;
import org.jojo.flow.model.api.IData;
import org.jojo.flow.model.api.IDataSignature;
import org.jojo.flow.model.api.IFlowChartElement;
import org.jojo.flow.model.api.IFlowModule;
import org.jojo.flow.model.api.IModulePin;
import org.jojo.flow.model.data.Data;
import org.jojo.flow.model.data.DataSignature;
import org.jojo.flow.model.flowChart.GraphicalRepresentation;
import org.jojo.flow.model.flowChart.connections.Connection;
import org.jojo.flow.model.storeLoad.DOM;
import org.jojo.flow.model.storeLoad.GraphicalRepresentationDOM;
import org.jojo.flow.model.storeLoad.ModulePinDOM;
import org.jojo.flow.model.storeLoad.OK;

public abstract class ModulePin extends Subject implements IDOMable, IModulePin {
    private final ModulePinImp imp;
    private final ModulePinGR gr;
    
    public ModulePin(final ModulePinImp imp, final ModulePinGR gr) {
        this.imp = Objects.requireNonNull(imp);
        this.gr = Objects.requireNonNull(gr);
    }
    
    @Override
    public GraphicalRepresentation getGraphicalRepresentation() {
        return this.gr;
    }
    
    @Override
    public IFlowModule getModule() {
        return this.imp.getModule();
    }
    
    private void setModule(final FlowModule module) {
        this.imp.setModule(module);
    }
    
    @Override
    public synchronized List<IConnection> getConnections() {
        return this.imp.getConnections();
    }
    
    @Override
    public synchronized boolean addConnection(final IConnection toAdd) throws ListSizeException {
        final boolean ret = this.imp.addConnection(toAdd);
        if (ret) {
            notifyObservers(toAdd);
        }
        return ret;
    }
    
    @Override
    public synchronized boolean removeConnection(final IConnection toRemove) {
        final boolean ret = this.imp.removeConnection(toRemove);
        if (ret) {
            notifyObservers(toRemove);
        }
        return ret;
    }
    
    @Override
    public synchronized boolean removeConnection(final int index) {
        final IConnection toRemove = index >= getConnections().size() ? null : getConnections().get(index);
        final boolean ret = this.imp.removeConnection(index);
        if (ret) {
            notifyObservers(toRemove);
        }
        return ret;
    }
    
    @Override
    public IData getDefaultData() {
        return this.imp.getDefaultData();
    }
    
    protected void setDefaultData(final IData defaultData) {
        this.imp.setDefaultData(defaultData);
        notifyObservers(defaultData);
    }
    
    @Override
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
            if (getDefaultData() != null) {
                dom.appendString("defaultData", getDefaultData().toSerializedString());
            }
        } catch (ClassNotFoundException | IOException e) {
            // should not happen
            e.printStackTrace();
        }
        dom.setGraphicalRepresentation(getGraphicalRepresentation());
        if (getModulePinImp() instanceof DefaultPin) {
            final DefaultPin imp = (DefaultPin)getModulePinImp();
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
            final IFlowChartElement fce = new ModelFacade().getElementById(modId);
            if (fce instanceof IFlowModule) {
                setModule((FlowModule)fce);
            }
            final DOM conIdsDom = (DOM)domMap.get(ModulePinDOM.NAME_CONNECTION_IDS);
            final Map<String, Object> conIdsMap = conIdsDom.getDOMMap();
            for (Object conIdObj : conIdsMap.values()) {
                if (conIdObj instanceof DOM) {
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
            }
            final DOM defaultDataDom = (DOM)domMap.get("defaultData");
            if (defaultDataDom != null) {
                final String dataStr = defaultDataDom.elemGet();
                try {
                    setDefaultData(Data.ofSerializedString(dataStr));
                } catch (ClassNotFoundException | IOException e) {
                    // should not happen
                    e.printStackTrace();
                }
            }
            final DOM grDom = (DOM)domMap.get(GraphicalRepresentationDOM.NAME);
            this.gr.restoreFromDOM(grDom);
            if (domMap.containsKey("checkDataSignature")) {
                final DOM cdsDom = (DOM)domMap.get("checkDataSignature");
                final String cdsString = cdsDom.elemGet();
                final IDataSignature cds = DataSignature.of(cdsString);
                ((DefaultPin)this.imp).forceSetCheckDataSignature(cds);
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
            ok(x -> Integer.parseInt(modIdDom.elemGet()), "");

            ok(domMap.get(ModulePinDOM.NAME_CONNECTION_IDS) instanceof DOM, OK.ERR_MSG_WRONG_CAST);
            final DOM conIdsDom = (DOM)domMap.get(ModulePinDOM.NAME_CONNECTION_IDS);
            final Map<String, Object> conIdsMap = conIdsDom.getDOMMap();
            for (Object conIdObj : conIdsMap.values()) {
                if (conIdObj instanceof DOM) {
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
            }
            if (domMap.containsKey("defaultData")) {
                ok(domMap.get("defaultData") instanceof DOM, OK.ERR_MSG_WRONG_CAST);
                final DOM defaultDataDom = (DOM)domMap.get("defaultData");
                final String dataStr = defaultDataDom.elemGet();
                ok(dataStr != null, OK.ERR_MSG_NULL);
                final IData before = getDefaultData();
                final String nullIsOk = ok(x -> {try {
                    setDefaultData(Data.ofSerializedString(dataStr));
                    return null;
                } catch (ClassNotFoundException | IOException e) {
                    return e.toString();
                }}, "");
                setDefaultData(before);
                ok(nullIsOk == null, "this exc would occur: " + nullIsOk);
            }
            
            ok(domMap.get(GraphicalRepresentationDOM.NAME) instanceof DOM, OK.ERR_MSG_WRONG_CAST);
            final DOM grDom = (DOM)domMap.get(GraphicalRepresentationDOM.NAME);
            ok(this.gr.isDOMValid(grDom), "ModulePinGR " + OK.ERR_MSG_DOM_NOT_VALID);
            if (domMap.containsKey("checkDataSignature")) {
                ok(domMap.get("checkDataSignature") instanceof DOM, OK.ERR_MSG_WRONG_CAST);
                final DOM cdsDom = (DOM)domMap.get("checkDataSignature");
                final String cdsString = cdsDom.elemGet();
                ok(cdsString != null, OK.ERR_MSG_NULL);
                final IDataSignature cds = DataSignature.of(cdsString);
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
    public int fixedHashCode() {
        final int prime = 31;
        int factor = 0;
        final String className = getClass().getName();
        for (final char c : className.toCharArray()) {
            factor += c;
        }
        factor += this.gr.getLinePoint().x + this.gr.getLinePoint().y;
        factor += (long)Math.signum(this.gr.getLinePoint().x);
        factor += (long)Math.signum(this.gr.getLinePoint().y);
        factor = factor == 0 ? 1 : factor;
        return prime * factor;
    }
    
    @Override
    public boolean equals(final Object other) {
        if (other != null && other.getClass().equals(this.getClass())) {
            return this.gr.equals(((ModulePin)other).gr);
        }
        return false;
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() 
                + " with GR= " + getGraphicalRepresentation() 
                + " | IMP= " + getModulePinImp();
    }

    public static Comparator<? super IModulePin> getComparator() { // TODO doc that comparator does not always imply 0 => equals
        return new Comparator<IModulePin>() {
            @Override
            public int compare(IModulePin o1, IModulePin o2) {
                return Integer.valueOf(o1.fixedHashCode()).compareTo(o2.fixedHashCode());
            }
        };
    }
}
