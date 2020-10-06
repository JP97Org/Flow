package org.jojo.flow.model.flowChart.connections;

import org.jojo.flow.model.data.Data;
import org.jojo.flow.model.data.DataSignature;
import org.jojo.flow.model.flowChart.GraphicalRepresentation;
import org.jojo.flow.model.flowChart.modules.InputPin;
import org.jojo.flow.model.flowChart.modules.InternalConfig;
import org.jojo.flow.model.flowChart.modules.OutputPin;
import org.jojo.flow.model.flowChart.modules.StdPin;

public class StdArrow extends Connection {
    private DataSignature dataType;
    private Data data;
    
    public StdArrow(final OutputPin fromPin, final InputPin toPin, final String name) throws ConnectionException {
        super(fromPin, name);
        this.dataType = ((StdPin)fromPin.getModulePinImp()).getCheckDataSignature();
        this.data = null;
        addToPin(toPin);
    }
    
    public Data getData() {
        return this.data;
    }
    
    public boolean putData(final Data data) {
        if (data.hasSameType(this.dataType)) {
            this.data = data;
            return true;
        }
        return false;
    }
    
    public DataSignature getDataSignature() {
        return this.dataType;
    }
    
    /**
     * Sets a data signature to the arrow which matches the data signature at the moment,
     * but must be completely checking.
     * @param signature - the given data signature which must be recursively checking
     * @return whether putting the new data signature was successful
     */
    public boolean putDataSignature(final DataSignature signature) {
        if (this.dataType.equals(signature) && signature.isCheckingRecursive()) {
            this.dataType = signature;
            return true;
        }
        return false;
    }

    @Override
    public String getInfo() {
        return this.data == null ? "" + this.dataType : "" + this.data;
    }

    @Override
    protected boolean connectionMatchesPins() {
        return getFromPin().getModulePinImp() instanceof StdPin 
                && getToPins().stream().allMatch(x -> x.getModulePinImp() instanceof StdPin);
    }

    @Override
    protected boolean checkDataTypes() {
        if (connectionMatchesPins()) {
            final StdPin fromImp = (StdPin) getFromPin().getModulePinImp();
            return getToPins()
                    .stream()
                    .allMatch(x -> fromImp.getCheckDataSignature()
                            .equals(((StdPin)x.getModulePinImp()).getCheckDataSignature()));
        }
        return false;
    }

    @Override
    public GraphicalRepresentation getGraphicalRepresentation() {
        // TODO implement
        return null;
    }

    @Override
    public InternalConfig serializeInternalConfig() {
        return null; // no internal config exists
    }

    @Override
    public void restoreSerializedInternalConfig(InternalConfig internalConfig) {
        // no internal config exists
    }

    @Override
    public String serializeSimulationState() {
        // TODO implement
        return null;
    }

    @Override
    public void restoreSerializedSimulationState(String simulationState) {
        // TODO implement
        
    }

}
