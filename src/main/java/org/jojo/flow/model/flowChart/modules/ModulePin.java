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
import org.jojo.flow.model.api.DOMStringUnion;
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
import org.jojo.flow.model.api.IDOM;
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
    public IDOM getDOM() {
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
    public void restoreFromDOM(final IDOM dom) {
        if (isDOMValid(dom)) {
            getConnections().forEach(c -> removeConnection(c));
            final Map<String, DOMStringUnion> domMap = dom.getDOMMap();
            final IDOM modIdDom = (IDOM)domMap.get(ModulePinDOM.NAME_MODULE_ID).getValue();
            final int modId = Integer.parseInt(modIdDom.elemGet());
            final IFlowChartElement fce = new ModelFacade().getElementById(modId);
            if (fce instanceof IFlowModule) {
                setModule((FlowModule)fce);
            }
            final IDOM conIdsDom = (IDOM)domMap.get(ModulePinDOM.NAME_CONNECTION_IDS).getValue();
            final Map<String, DOMStringUnion> conIdsMap = conIdsDom.getDOMMap();
            for (var conIdObj : conIdsMap.values()) {
                if (conIdObj.isDOM()) {
                    final IDOM conIdDom = (IDOM)conIdObj.getValue();
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
            final IDOM defaultDataDom = domMap.get("defaultData") == null ? null : (IDOM)domMap.get("defaultData").getValue();
            if (defaultDataDom != null) {
                final String dataStr = defaultDataDom.elemGet();
                try {
                    setDefaultData(Data.ofSerializedString(dataStr));
                } catch (ClassNotFoundException | IOException e) {
                    // should not happen
                    e.printStackTrace();
                }
            }
            final IDOM grDom = (IDOM)domMap.get(GraphicalRepresentationDOM.NAME).getValue();
            this.gr.restoreFromDOM(grDom);
            if (domMap.containsKey("checkDataSignature")) {
                final IDOM cdsDom = (IDOM)domMap.get("checkDataSignature").getValue();
                final String cdsString = cdsDom.elemGet();
                final IDataSignature cds = DataSignature.of(cdsString);
                ((DefaultPin)this.imp).forceSetCheckDataSignature(cds);
            }
            notifyObservers();
        }
    }
    
    @Override
    public boolean isDOMValid(final IDOM dom) {
        Objects.requireNonNull(dom);
        final Map<String, DOMStringUnion> domMap = dom.getDOMMap();
        try {
            ok(domMap.get(ModulePinDOM.NAME_CLASSNAME).isDOM(), OK.ERR_MSG_WRONG_CAST);
            final IDOM cnDom = (IDOM)domMap.get(ModulePinDOM.NAME_CLASSNAME).getValue();
            final String cn = cnDom.elemGet();
            ok(getClass().getName().equals(cn), OK.ERR_MSG_WRONG_CAST);
            ok(domMap.get(ModulePinDOM.NAME_CLASSNAME_IMP).isDOM(), OK.ERR_MSG_WRONG_CAST);
            final IDOM cnDomImp = (IDOM)domMap.get(ModulePinDOM.NAME_CLASSNAME_IMP).getValue();
            final String cnImp = cnDomImp.elemGet();
            ok(this.imp.getClass().getName().equals(cnImp), OK.ERR_MSG_WRONG_CAST);
            
            ok(domMap.get(ModulePinDOM.NAME_MODULE_ID).isDOM(), OK.ERR_MSG_WRONG_CAST);
            final IDOM modIdDom = (IDOM)domMap.get(ModulePinDOM.NAME_MODULE_ID).getValue();
            ok(x -> Integer.parseInt(modIdDom.elemGet()), "");

            ok(domMap.get(ModulePinDOM.NAME_CONNECTION_IDS).isDOM(), OK.ERR_MSG_WRONG_CAST);
            final IDOM conIdsDom = (IDOM)domMap.get(ModulePinDOM.NAME_CONNECTION_IDS).getValue();
            final Map<String, DOMStringUnion> conIdsMap = conIdsDom.getDOMMap();
            for (var conIdObj : conIdsMap.values()) {
                if (conIdObj.isDOM()) {
                    final IDOM conIdDom = (IDOM)conIdObj.getValue();
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
                ok(domMap.get("defaultData").isDOM(), OK.ERR_MSG_WRONG_CAST);
                final IDOM defaultDataDom = (IDOM)domMap.get("defaultData").getValue();
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
            
            ok(domMap.get(GraphicalRepresentationDOM.NAME).isDOM(), OK.ERR_MSG_WRONG_CAST);
            final IDOM grDom = (IDOM)domMap.get(GraphicalRepresentationDOM.NAME).getValue();
            ok(this.gr.isDOMValid(grDom), "ModulePinGR " + OK.ERR_MSG_DOM_NOT_VALID);
            if (domMap.containsKey("checkDataSignature")) {
                ok(domMap.get("checkDataSignature").isDOM(), OK.ERR_MSG_WRONG_CAST);
                final IDOM cdsDom = (IDOM)domMap.get("checkDataSignature").getValue();
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

    /**
     * Gets a comparator for flow module pins based on {@link #fixedHashCode()}. Note that
     * the returned comparator is not necessarily consistent with {@code equals}, i.e. comparison
     * result 0 does not necessarily imply that the two compared module pins are equal. However, if two
     * module pins are equal, the comparison result will always be 0.
     * 
     * @return a comparator for flow module pins
     */
    public static Comparator<? super IModulePin> getComparator() {
        return new Comparator<IModulePin>() {
            @Override
            public int compare(IModulePin o1, IModulePin o2) {
                return Integer.valueOf(o1.fixedHashCode()).compareTo(o2.fixedHashCode());
            }
        };
    }
}
