package org.jojo.flow.model.api;

/**
 * This interface represents string data.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public interface IStringData extends IBasicCheckable {
    
    /**
     * Gets the default implementation.
     * 
     * @param string - the string value (must not be {@code null})
     * @return the default implementation
     */
    public static IStringData getDefaultImplementation(final String string) {
        final var ret = (IStringData) IAPI.defaultImplementationOfThisApi(new Class<?>[] {String.class}, string);
        return ret;
    }
    
    @Override
    String toString();
}
