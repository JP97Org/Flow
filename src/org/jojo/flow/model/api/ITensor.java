package org.jojo.flow.model.api;

/**
 * This interface represents a tensor.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 * @param <T> - the generic type
 * @see IMatrix
 */
public interface ITensor<T> extends IBasicCheckable {
    
    /**
     * Gets the default implementation.
     * 
     * @param <K> - the generic type
     * @param matrix - the 3d-array representing the tensor, which must contain at least one entry
     * @param unit - the unit signature
     * @return the default implementation
     */
    public static <K> ITensor<K> getDefaultImplementation(final K[][][] tensor, final UnitSignature unit) {
        @SuppressWarnings("unchecked")
        final var ret = (ITensor<K>) IAPI.defaultImplementationOfThisApi(new Class<?>[] {Object[][][].class, UnitSignature.class}, tensor, unit);
        return ret;
    }
    
    /**
     * Gets the tensor.
     * 
     * @return the tensor as a pair of data and unit
     */
    Pair<T[][][], UnitSignature> getTensor();
}
