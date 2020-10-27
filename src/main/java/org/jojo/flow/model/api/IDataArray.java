package org.jojo.flow.model.api;

/**
 * This interface represents a data array, i.e. a constant-sized array of {@link IData} of the same type.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public interface IDataArray extends IRecursiveCheckable {
    
    /**
     * Gets the default implementation.
     * 
     * @param data - the data array
     * @param dataSignature - the data signature
     * @return the default implementation
     */
    public static IDataArray getDefaultImplementation(final IData[] data, final IDataSignature dataSignature) {
        return (IDataArray) IAPI.defaultImplementationOfThisApi(new Class<?>[] {IData[].class, IDataSignature.class}, data, dataSignature);
    }
}
