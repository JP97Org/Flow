package org.jojo.flow.model.util;

import java.util.Map;

import org.jojo.flow.model.api.IAPI;
import org.jojo.flow.model.api.IFactory;


/**
 * This utility class can be used by default implementation getters
 * in order to find mappings from {@link org.jojo.flow.model.api.IAPI} interfaces to respective
 * default implementations. This utility class wraps the {@link org.jojo.flow.model.util.DefaultFactory}.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public final class FactoryUtil {
    private FactoryUtil() {
        
    }
    
    /**
     * A Map which maps the API interfaces to respective default implementation classes.
     */
    private static final IFactory defaultFactory = new DefaultFactory();
    
    /**
     * Sets the default mappings for the API interfaces to default implementations map.
     */
    public static void initialize() {
        defaultFactory.initialize();
    }
    
    /**
     * 
     * @return the api to default implementations mappings
     */
    public static Map<Class<?>, Class<?>> getApiToDefaultImplementationMap() {
        return defaultFactory.getApiToImplementationMap();
    }
    
    /**
     * Puts the given mapping. If it does not succeed an error {@link org.jojo.flow.exc.Warning} is 
     * reported.
     * 
     * @param key - the {@link org.jojo.flow.model.api.IAPI} interface
     * @param value - the respective default implementation to be put
     * @return whether the putting was successful, i.e. the key is assignable from the value
     */
    public static boolean putDefaultImplementationMapping(final Class<?> key, final Class<?> value) {
        return defaultFactory.putImplementationMapping(key, value);
    }
    
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
    public static IAPI getImplementationOfApi(String iClassName, final Class<?>[] parameterTypes, final Object... initArgs) throws IllegalArgumentException {
        return defaultFactory.getImplementationOfApi(iClassName, parameterTypes, initArgs);
    }

    /**
     * 
     * @return the wrapped default factory
     */
    public static IFactory getFactory() {
        return defaultFactory;
    }
}
