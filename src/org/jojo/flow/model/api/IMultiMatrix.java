package org.jojo.flow.model.api;

public interface IMultiMatrix<T> extends IBasicCheckable, Iterable<T> {
    public static <K> IMultiMatrix<K> getDefaultImplementation(final int[] sizes, K dataSample, final UnitSignature unit) {
        @SuppressWarnings("unchecked")
        final var ret = (IMultiMatrix<K>) IAPI.defaultImplementationOfThisApi(new Class<?>[] {int[].class, UnitSignature.class}, sizes, unit);
        return ret;
    }
    
    public static <K> IMultiMatrix<K> getDefaultImplementation(final int[] sizes, final K[] data, final UnitSignature unit) {
        @SuppressWarnings("unchecked")
        final var ret = (IMultiMatrix<K>) IAPI.defaultImplementationOfThisApi(new Class<?>[] {int[].class, Object[].class, UnitSignature.class}, sizes, data, unit);
        return ret;
    }
    
    T get(final int[] indices) throws IllegalArgumentException;

    T[] getRow(final int[] otherIndices) throws IllegalArgumentException;
    
    void set(final T value, final int[] indices) throws IllegalArgumentException;

    public Object[] toArray();
}
