package org.jojo.flow.model.flowChart.modules;

import java.util.Objects;

import org.jojo.flow.exc.DataTypeIncompatException;
import org.jojo.flow.exc.FlowException;
import org.jojo.flow.model.api.IDataSignature;
import org.jojo.flow.model.api.IDefaultPin;
import org.jojo.flow.model.data.Data;
import org.jojo.flow.model.flowChart.connections.DefaultArrow;

public class DefaultPin extends ModulePinImp implements IDefaultPin {
    private IDataSignature checkDataSignature;
    
    public DefaultPin(final FlowModule module, final Data defaultData) {
        super(module, defaultData);
        Objects.requireNonNull(defaultData);
        this.checkDataSignature = defaultData.getDataSignature().getCopy();
    }
    
    @Override
    public IDataSignature getCheckDataSignature() {
        return this.checkDataSignature;
    }
    
    /**
     * Sets a new checking data signature. It must match the old checking data signature.
     * However, it may be more or less checking.
     * 
     * @param checkingDataSignature - the data signature to be set
     * @throws FlowException if the data signature to be set and the already set one do not match
     */
    @Override
    public void setCheckDataSignature(final IDataSignature checkingDataSignature) throws FlowException {
        if (this.checkDataSignature.matches(checkingDataSignature)) {
            forceSetCheckDataSignature(checkingDataSignature);
        } else {
            throw new FlowException(new DataTypeIncompatException("data signature to be set and the already set one do not match"), getModule());
        }
    }
    
    protected void forceSetCheckDataSignature(final IDataSignature checkingDataSignature) {
        this.checkDataSignature = checkingDataSignature;
        getConnections().forEach(c -> ((DefaultArrow)c).forcePutDataSignature(checkingDataSignature));
    }

    @Override
    public String toString() {
        return "DefaultPin with checkDataSignature= " + getCheckDataSignature() + " | defaultData= " + getDefaultData();
    }
}