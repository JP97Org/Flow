package org.jojo.flow.model.api;

import java.io.IOException;
import java.io.Serializable;

import org.jojo.flow.model.api.IDataSignature;

/**
 * Represents a Data which can be sent via an arrow in the flow chart or processed by a flow module.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public interface IData extends IAPI, Serializable {
    
    /**
     * Gets the data's signature of this data. Note that it is possible, that the returned IDataSignature
     * is not a copy but the real data's signature. So, if you want to alter it e.g. by deactivating
     * checking in any contained signature please ensure that you copy the returned data signature first
     * and alter the copied data signature.<br/><br/>
     * 
     * Moreover, it is highly not recommended to render a data's real data signature non-recursive-checking.
     * If it is done anyway, the receiver of the data may not be able to cast the data correctly.
     * 
     * @return the data's signature, i.e. its datatype
     */
    IDataSignature getDataSignature();
    
    /**
     * Determines whether this data has the same type as the one defined by the given IDataSignature.
     * Note that this data has the same type as the one defined by the given IDataSignature iff the data's
     * signature matches the given data type, i.e. {@code getDataSignature().matches(dataType)} 
     * evaluates to {@code true}.
     * 
     * @param dataType - the given IDataSignature
     * @return whether this data has the same type as the one defined by the given IDataSignature
     * @see IDataSignature#matches(IDataSignature)
     */
    boolean hasSameType(final IDataSignature dataType);
    
    @Override
    String toString();
    
    /**
     * Serializes this data. If XML should be created and the transformer fails in being created or during
     * transformation, an error Warning is reported.
     * 
     * @return the serialized data encoded as BASE64 or XML 
     * if {@link IXMLSerialTransform#getDefaultImplementation()} 
     * does not return {@code null}
     * @throws ClassNotFoundException if a class is not found
     * @throws IOException if an IO failure occurs
     */
    String toSerializedString() throws ClassNotFoundException, IOException;
}
