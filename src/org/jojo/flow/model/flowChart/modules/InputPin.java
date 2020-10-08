package org.jojo.flow.model.flowChart.modules;

import org.jojo.flow.model.Warning;
import org.jojo.flow.model.data.Data;
import org.jojo.flow.model.flowChart.connections.Connection;
import org.jojo.flow.model.flowChart.connections.StdArrow;
import org.jojo.flow.model.storeLoad.DOM;
import org.jojo.flow.model.storeLoad.ModulePinDOM;

public class InputPin extends ModulePin {  
    public InputPin(final ModulePinImp imp, final ModulePinGR gr) {
        super(imp, gr);
    }
    
    @Override
    public synchronized void addConnection(final Connection toAdd) throws ListSizeException {
        if (getConnections().isEmpty()) {
            super.addConnection(toAdd); 
        } else {
            throw new ListSizeException(new Warning(getModule(), "input pin may only have one incoming connection", true));
        }
    }
    
    public Data getData() {
        return getConnections().isEmpty() 
                ? getDefaultData() 
                        : (getConnections().get(0) instanceof StdArrow 
                                ? ((StdArrow) getConnections().get(0)).getData() : getDefaultData());
    }

    @Override
    public DOM getDOM() {
        final ModulePinDOM dom = new ModulePinDOM();
        dom.setClassName(getClass().getName());
        dom.setClassNameImp(getModulePinImp().getClass().getName());
        dom.setModuleID(getModule().getId());
        dom.setConnectionIDs(getConnections().stream().mapToInt(c -> c.getId()).toArray());
        dom.appendString("defaultData", getDefaultData().toString());
        dom.setGraphicalRepresentation(getGraphicalRepresentation());
        if (getModulePinImp() instanceof StdPin) {
            final StdPin imp = (StdPin)getModulePinImp();
            dom.appendString("checkDataSignature", imp.getCheckDataSignature().toString());
        }
        return dom;
    }

    @Override
    public void restoreFromDOM(DOM dom) {
        // TODO Auto-generated method stub
        
    }
}
