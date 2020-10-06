package org.jojo.flow.model.flowChart.modules;

import org.jojo.flow.model.data.Data;
import org.jojo.flow.model.flowChart.connections.Connection;
import org.jojo.flow.model.flowChart.connections.StdArrow;

public class OutputPin extends ModulePin {
    public OutputPin(final ModulePinImp imp) {
        super(imp);
    }
    
    public boolean putData(final Data data) {
        boolean ok = true;
        for (final Connection connection : getConnections()) {
            if (connection instanceof StdArrow) {
                final StdArrow arrow = (StdArrow)connection;
                ok &= arrow.putData(data);
            }
        }
        return ok;
    }
}
