package org.jojo.flow.model.api;

import org.jojo.flow.exc.FlowException;

/**
 * This interface represents a default pin, i.e. a pin to which a IDefaultArrow can be connected.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public interface IDefaultPin extends IModulePinImp {

    /**
     * Gets the data signature used for checking validity of connections which should be connected to this pin.
     * It must not be {@code null}.
     * 
     * @return the data signature used for checking validity
     */
    IDataSignature getCheckDataSignature();

    /**
     * Sets a new checking data signature. It must match the old checking data signature.
     * However, it may be more or less checking.
     * 
     * @param checkingDataSignature - the data signature to be set (must not be {@code null})
     * @throws FlowException if the data signature to be set and the already set one do not match
     */
    void setCheckDataSignature(IDataSignature checkingDataSignature) throws FlowException;
}