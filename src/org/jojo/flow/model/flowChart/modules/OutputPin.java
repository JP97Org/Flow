package org.jojo.flow.model.flowChart.modules;

import org.jojo.flow.model.data.Data;
import org.jojo.flow.model.flowChart.connections.Connection;
import org.jojo.flow.model.flowChart.connections.StdArrow;
import org.jojo.flow.model.storeLoad.DOM;
import org.jojo.flow.model.storeLoad.ModulePinDOM;

public class OutputPin extends ModulePin {
    public OutputPin(final ModulePinImp imp, final ModulePinGR gr) {
        super(imp, gr);
    }
    
    public boolean putData(final Data data) {
        boolean ok = true;
        for (final Connection connection : getConnections()) {
            if (connection instanceof StdArrow) {
                final StdArrow arrow = (StdArrow)connection;
                ok &= arrow.putData(data);
            }
        }
        notifyObservers(data);
        return ok;
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
    
    @Override
    public boolean isDOMValid(DOM dom) {
        // TODO Auto-generated method stub
        return true;
    }
}
