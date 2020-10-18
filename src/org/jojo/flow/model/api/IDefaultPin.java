package org.jojo.flow.model.api;

import org.jojo.flow.model.FlowException;

public interface IDefaultPin extends IModulePinImp {

    IDataSignature getCheckDataSignature();

    /**
     * Sets a new checking data signature. It must match the old checking data signature.
     * However, it may be more or less checking.
     * 
     * @param checkingDataSignature - the data signature to be set
     * @throws FlowException if the data signature to be set and the already set one do not match
     */
    void setCheckDataSignature(IDataSignature checkingDataSignature) throws FlowException;
}