package org.jojo.flow.model.api;

import java.util.List;

/**
 * This interface represents a data vector, i.e. a variable-sized list of IData of the same type.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public interface IDataVector extends IRecursiveCheckable {
    
    /**
     * Gets the default implementation.
     * 
     * @param data - the data list
     * @param dataSignature - the data signature
     * @return the default implementation
     */
    public static IDataVector getDefaultImplementation(final List<IData> data, final IDataSignature dataSignature) {
        return (IDataVector) IAPI.defaultImplementationOfThisApi(new Class<?>[] {List.class, IDataSignature.class}, data, dataSignature);
    }
}
