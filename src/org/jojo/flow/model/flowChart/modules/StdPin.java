package org.jojo.flow.model.flowChart.modules;

import java.util.Objects;

import org.jojo.flow.model.FlowException;
import org.jojo.flow.model.data.Data;
import org.jojo.flow.model.data.DataSignature;
import org.jojo.flow.model.data.DataTypeIncompatException;

public class StdPin extends ModulePinImp {
    private DataSignature checkDataSignature;
    
    public StdPin(final FlowModule module, final Data defaultData) {
        super(module, defaultData);
        Objects.requireNonNull(defaultData);
        this.checkDataSignature = defaultData.getDataSignature().getCopy();
    }
    
    public DataSignature getCheckDataSignature() {
        return this.checkDataSignature;
    }
    
    /**
     * Sets a new checking data signature. It must match the old checking data signature.
     * However, it may be  more checking.
     * @param newCheckDataSignature - the data signature to be set
     * @throws FlowException if the data signature to be set and the already set one do not match
     */
    protected void setCheckDataSignature(final DataSignature newCheckDataSignature) throws FlowException {
        if (this.checkDataSignature.equals(newCheckDataSignature)) {
            this.checkDataSignature = newCheckDataSignature;
        } else {
            throw new FlowException(new DataTypeIncompatException("data signature to be set and the already set one do not match"), getModule());
        }
    }
    
    protected void forceSetCheckDataSignature(final DataSignature newCheckDataSignature) {
        this.checkDataSignature = newCheckDataSignature;
    }
}
