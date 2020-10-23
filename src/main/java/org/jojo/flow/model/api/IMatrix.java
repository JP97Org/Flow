package org.jojo.flow.model.api;

/**
 * This interface represents a matrix.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 * @param <T> - the generic type
 * @see IMathMatrix
 * @see ITensor
 */
public interface IMatrix<T> extends IBasicCheckable {
    
    /**
     * Gets the default implementation.
     * 
     * @param <K> - the generic type
     * @param matrix - the 2d-array representing the matrix, which must contain at least one entry
     * @param unit - the unit signature
     * @return the default implementation
     */
    public static <K> IMatrix<K> getDefaultImplementation(final K[][] matrix, final UnitSignature unit) {
        @SuppressWarnings("unchecked")
        final var ret = (IMatrix<K>) IAPI.defaultImplementationOfThisApi(new Class<?>[] {Object[][].class, UnitSignature.class}, matrix, unit);
        return ret;
    }
    
    /**
     * Gets the matrix.
     * 
     * @return the matrix as a pair of data and unit
     */
    Pair<T[][], UnitSignature> getMatrix();
}
