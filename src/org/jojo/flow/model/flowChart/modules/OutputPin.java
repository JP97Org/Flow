package org.jojo.flow.model.flowChart.modules;

import org.jojo.flow.model.api.IData;
import org.jojo.flow.model.api.IOutputPin;
import org.jojo.flow.model.flowChart.connections.Connection;
import org.jojo.flow.model.flowChart.connections.DefaultArrow;

public class OutputPin extends ModulePin implements IOutputPin {
    public OutputPin(final ModulePinImp imp, final ModulePinGR gr) {
        super(imp, gr);
    }
    
    @Override
    public boolean putData(final IData data) {
        boolean ok = true;
        for (final Connection connection : getConnections()) {
            if (connection instanceof DefaultArrow) {
                final DefaultArrow arrow = (DefaultArrow)connection;
                ok &= arrow.putData(data);
            }
        }
        notifyObservers(data);
        return ok;
    }
}
