package org.jojo.flow.model.api;

import org.jojo.flow.exc.Warning;
import org.jojo.flow.model.util.FactoryUtil;

/**
 * The main interface of the Flow Model API. All interfaces of the API should extend this interface.
 * It declares no methods but provides a static API initializer and a default implementation getter
 * which can be used from sub-interfaces to get respective default implementations.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public interface IAPI {
    
    /**
     * Initializes the API, i.e. sets the mappings for the API interfaces to default implementations map
     * defined in the {@link org.jojo.flow.model.util.FactoryUtil} class.
     * 
     * @see org.jojo.flow.model.util.FactoryUtil#initialize()
     */
    static void initialize() { 
        FactoryUtil.initialize();
    }
    
    /**
     * Gets the default implementation of the calling sub-interface of IAPI.
     * This method should only directly be called from a sub-interface of IAPI.
     * 
     * @param parameterTypes - the parameter types for the constructor to call
     * @param initArgs - the initial arguments for the constructor to call
     * @return a default implementation of the calling IAPI sub-interface created with the given initial arguments
     */
    static IAPI defaultImplementationOfThisApi(final Class<?>[] parameterTypes, final Object... initArgs) {
        final String iClassName;
        try { 
            throw new RuntimeException();
        } catch (RuntimeException e) {
            iClassName = e.getStackTrace()[1].getClassName();
        }
        try {
            return FactoryUtil.getImplementationOfApi(iClassName, parameterTypes, initArgs);
        } catch (IllegalArgumentException e) {
            new Warning(null, e.toString(), true).reportWarning();
            return null;
        }
    }
}
