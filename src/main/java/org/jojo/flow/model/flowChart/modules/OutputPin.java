package org.jojo.flow.model.flowChart.modules;

import org.jojo.flow.model.api.IConnection;
import org.jojo.flow.model.api.IData;
import org.jojo.flow.model.api.IDefaultArrow;
import org.jojo.flow.model.api.IOutputPin;

public class OutputPin extends ModulePin implements IOutputPin {
    public OutputPin(final ModulePinImp imp, final ModulePinGR gr) {
        super(imp, gr);
    }
    
    @Override
    public boolean putData(final IData data) {
        boolean ok = true;
        for (final IConnection connection : getConnections()) {
            if (connection instanceof IDefaultArrow) {
                final IDefaultArrow arrow = (IDefaultArrow)connection;
                ok &= arrow.putData(data);
            }
        }
        notifyObservers(data);
        return ok;
    }
}
