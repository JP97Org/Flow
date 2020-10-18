package org.jojo.flow.model.flowChart.modules;

import org.jojo.flow.model.Warning;
import org.jojo.flow.model.api.IData;
import org.jojo.flow.model.api.IInputPin;
import org.jojo.flow.model.flowChart.connections.Connection;
import org.jojo.flow.model.flowChart.connections.DefaultArrow;

public class InputPin extends ModulePin implements IInputPin {  
    public InputPin(final ModulePinImp imp, final ModulePinGR gr) {
        super(imp, gr);
    }
    
    @Override
    public synchronized boolean addConnection(final Connection toAdd) throws ListSizeException {
        if (getConnections().isEmpty()) {
            return super.addConnection(toAdd); 
        } else {
            throw new ListSizeException(new Warning(getModule(), "input pin may only have one incoming connection", true));
        }
    }
    
    @Override
    public IData getData() {
        return getConnections().isEmpty() 
                ? getDefaultData() 
                        : (getConnections().get(0) instanceof DefaultArrow 
                                ? ((DefaultArrow) getConnections().get(0)).getData() : getDefaultData());
    }
}
