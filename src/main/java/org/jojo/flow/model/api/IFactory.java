package org.jojo.flow.model.api;

import java.util.Map;

import org.jojo.flow.model.util.FactoryUtil;

public interface IFactory extends IAPI {
    
    /**
     * 
     * @return the default factory wrapped by the {@link org.jojo.flow.model.util.FactoryUtil}
     */
    public static IFactory getDefaultImplementation() {
        return FactoryUtil.getFactory();
    }
    
    /**
     * Sets the mappings for the API interfaces to implementations map.
     */
    void initialize();
    
    /**
     * 
     * @return a copy of the api to implementations map
     */
    Map<Class<?>, Class<?>> getApiToImplementationMap();
    
    /**
     * Puts the given mapping. If it does not succeed an error {@link org.jojo.flow.exc.Warning} is 
     * reported.
     * 
     * @param key - the {@link org.jojo.flow.model.api.IAPI} interface
     * @param value - the respective default implementation to be put
     * @return whether the putting was successful, i.e. the key is assignable from the value
     */
    boolean putImplementationMapping(final Class<?> key, final Class<?> value);
    
    /**
     * Gets the implementation of the sub-interface of IAPI defined 
     * by the given interface class name.
     * 
     * @param iClassName - the given interface class name
     * @param parameterTypes - the parameter types for the constructor to call
     * @param initArgs - the initial arguments for the constructor to call
     * @return an implementation of the IAPI defined by the given interface class name created with the given initial arguments
     * or {@code null} if no implementation was mapped to the specified IAPI
     * @throws IllegalArgumentException if an argument is invalid
     */
    IAPI getImplementationOfApi(String iClassName, final Class<?>[] parameterTypes, final Object... initArgs) throws IllegalArgumentException;
}
