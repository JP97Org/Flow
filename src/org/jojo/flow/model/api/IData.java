package org.jojo.flow.model.api;

import java.io.IOException;
import java.io.Serializable;

import org.jojo.flow.model.api.IDataSignature;

public interface IData extends IAPI, Serializable {
    
    IDataSignature getDataSignature();
    
    boolean hasSameType(final IDataSignature dataType);
    
    @Override
    String toString();
    
    String toSerializedString() throws ClassNotFoundException, IOException;
}
