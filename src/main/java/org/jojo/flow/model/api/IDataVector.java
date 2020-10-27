package org.jojo.flow.model.api;

import java.util.List;

import org.jojo.flow.exc.DataTypeIncompatException;

/**
 * This interface represents a data vector, i.e. a variable-sized list of {@link IData} of the same type.
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

    /**
     * Adds the given data to the end of this vector.
     * 
     * @param toAdd - the given data
     * @throws DataTypeIncompatException if the data types of the other vector elements and the added element
     * do not match
     */
    void add(IData toAdd) throws DataTypeIncompatException;

    /**
     * Removes the element at the given index.
     * 
     * @param index - the given index
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    void remove(int index) throws IndexOutOfBoundsException;
}
