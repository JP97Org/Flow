package org.jojo.flow.model.api;

import org.jojo.flow.exc.IllegalUnitOperationException;
import org.jojo.flow.model.data.Pair;

public interface IMathMatrix<T extends Number> extends IBasicCheckable {
    public static <K extends Number> IMathMatrix<K> getDefaultImplementation(final K[][] matrix, final UnitSignature unit) {
        @SuppressWarnings("unchecked")
        final var ret = (IMathMatrix<K>) IAPI.defaultImplementationOfThisApi(new Class<?>[] {Number[][].class, UnitSignature.class}, matrix, unit);
        return ret;
    }
    
    Pair<T[][], UnitSignature> getMatrix();
    
    IMathMatrix<T> add(final IMathMatrix<T> other) throws IllegalArgumentException, IllegalUnitOperationException;
    
    IMathMatrix<T> multiply(final Unit<T> scalar);
    
    IMathMatrix<T> multiply(final IMathMatrix<T> other) throws IllegalArgumentException, IllegalUnitOperationException;
    
    T determinant() throws UnsupportedOperationException;
}
