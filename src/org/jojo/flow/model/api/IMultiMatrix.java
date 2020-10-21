package org.jojo.flow.model.api;

/**
 * This interface represents a multi matrix data, i.e. a n-dimensional array of basic types.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 * @param <T> - the generic type
 */
public interface IMultiMatrix<T> extends IBasicCheckable, Iterable<T> {
    
    /**
     * Gets the default implementation with the specified sizes.
     * 
     * @param <K> - the generic type
     * @param sizes - the sizes of the different dimensions (must be an array with length > 0
     * and all elements must be > 0)
     * @param dataSample - a data sample in order to determine the generic type
     * @param unit - the unit signature
     * @return the default implementation with the specified sizes
     * @see #getDefaultImplementation(int[], K[], UnitSignature)
     */
    public static <K> IMultiMatrix<K> getDefaultImplementation(final int[] sizes, K dataSample, final UnitSignature unit) {
        @SuppressWarnings("unchecked")
        final var ret = (IMultiMatrix<K>) IAPI.defaultImplementationOfThisApi(new Class<?>[] {int[].class, UnitSignature.class}, sizes, unit);
        return ret;
    }
    
    /**
     * Gets the default implementation with the specified sizes and the data as content.
     * 
     * @param <K> - the generic type
     * @param sizes - the sizes of the different dimensions
     * @param data - the data array in one dimensional representation
     * @param unit - the unit signature
     * @return the default implementation with the specified sizes and the data as content
     * @see #getDefaultImplementation(int[], Object, UnitSignature)
     * @see #toOneDimensionalRepresentation()
     */
    public static <K> IMultiMatrix<K> getDefaultImplementation(final int[] sizes, final K[] data, final UnitSignature unit) {
        @SuppressWarnings("unchecked")
        final var ret = (IMultiMatrix<K>) IAPI.defaultImplementationOfThisApi(new Class<?>[] {int[].class, Object[].class, UnitSignature.class}, sizes, data, unit);
        return ret;
    }
    
    /**
     * Gets the element at the specified indices.
     * 
     * @param indices - the specified indices
     * @return the element at the specified indices
     * @throws IllegalArgumentException if indices are somehow out of bounds
     */
    T get(final int[] indices) throws IllegalArgumentException;

    /**
     * Gets the row at the given other indices.
     * 
     * @param otherIndices - the other indices
     * @return the row at the given other indices
     * @throws IllegalArgumentException if indices are somehow out of bounds
     */
    T[] getRow(final int[] otherIndices) throws IllegalArgumentException;
    
    /**
     * Sets the value at the specified indices.
     * 
     * @param value - the value
     * @param indices - the specified indices
     * @throws IllegalArgumentException if indices are somehow out of bounds
     */
    void set(final T value, final int[] indices) throws IllegalArgumentException;

    /**
     * Gets a n-dimensional array representation of this multi matrix. A call to 
     * {@code Arrays.deepToString(m.toArray())} returns the same result as a deepToString call
     * with an actual n-dimensional T array (matching this multi matrix) as parameter.
     * 
     * @return a n-dimensional array representation of this multi matrix
     * @see java.util.Arrays#deepToString(Object[])
     */
    public Object[] toArray();
    
    /**
     * Gets a one-dimensional representation of this multi matrix.
     * 
     * @return a one-dimensional representation of this multi matrix
     */
    public T[] toOneDimensionalRepresentation();
}
