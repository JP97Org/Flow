package org.jojo.flow.api;

import java.io.IOException;
import java.io.Serializable;

import org.jojo.flow.api.IDataSignature;

public interface IData extends Serializable {
    
    IDataSignature getDataSignature();
    
    boolean hasSameType(final IDataSignature dataType);
    
    @Override
    String toString();
    
    String toSerializedString() throws ClassNotFoundException, IOException;
}
