package org.jojo.flow.model.api;

import org.jojo.flow.model.data.Pair;

public interface IMatrix<T> extends IBasicCheckable {
    public static <K> IMatrix<K> getDefaultImplementation(final K[][] matrix, final UnitSignature unit) {
        @SuppressWarnings("unchecked")
        final var ret = (IMatrix<K>) IAPI.defaultImplementationOfThisApi(new Class<?>[] {Object[][].class, UnitSignature.class}, matrix, unit);
        return ret;
    }
    
    Pair<T[][], UnitSignature> getMatrix();
}
