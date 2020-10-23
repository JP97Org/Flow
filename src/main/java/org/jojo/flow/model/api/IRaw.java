package org.jojo.flow.model.api;

import java.util.List;

/**
 * This interface represents raw data.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public interface IRaw extends IBasicCheckable {
    
    /**
     * Gets the default implementation.
     * 
     * @param bytes - the byte array as a a list (must not be {@code null})
     * @return the default implementation
     */
    public static IRaw getDefaultImplementation(final List<Byte> bytes) {
        final var ret = (IRaw) IAPI.defaultImplementationOfThisApi(new Class<?>[] {List.class}, bytes);
        return ret;
    }
    
    /**
     * Gets the raw data.
     * 
     * @return the raw data
     */
    public byte[] getData();
}
