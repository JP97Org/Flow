package org.jojo.flow.model.api;

import java.util.List;

/**
 * This interface represents a data bundle, i.e. a constant-sized array of IData with possibly 
 * different types.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public interface IDataBundle extends IRecursiveCheckable {
    
    /**
     * Gets the default implementation.
     * 
     * @param data - the data array as a list
     * @return the default implementation
     */
    public static IDataBundle getDefaultImplementation(final List<IData> data) {
        return (IDataBundle) IAPI.defaultImplementationOfThisApi(new Class<?>[] {List.class}, data);
    }
}
