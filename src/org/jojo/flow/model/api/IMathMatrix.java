package org.jojo.flow.model.api;

import org.jojo.flow.exc.IllegalUnitOperationException;

/**
 * This interface represents a mathematical matrix.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 * @param <T> - the generic number type
 * @see IMatrix
 */
public interface IMathMatrix<T extends Number> extends IBasicCheckable {
    
    /**
     * Gets the default implementation.
     * 
     * @param <K> - the generic number type
     * @param matrix - the 2d-array representing the matrix, which must contain at least one entry
     * @param unit - the unit signature
     * @return the default implementation
     */
    public static <K extends Number> IMathMatrix<K> getDefaultImplementation(final K[][] matrix, final UnitSignature unit) {
        @SuppressWarnings("unchecked")
        final var ret = (IMathMatrix<K>) IAPI.defaultImplementationOfThisApi(new Class<?>[] {Number[][].class, UnitSignature.class}, matrix, unit);
        return ret;
    }
    
    /**
     * Gets the matrix.
     * 
     * @return the matrix as a pair of data and unit
     */
    Pair<T[][], UnitSignature> getMatrix();
    
    /**
     * Adds the other matrix.
     * 
     * @param other - the other matrix
     * @return the sum of the matrices
     * @throws IllegalArgumentException if the dimensions do not fit
     * @throws IllegalUnitOperationException if the units do not fit 
     */
    IMathMatrix<T> add(final IMathMatrix<T> other) throws IllegalArgumentException, IllegalUnitOperationException;
    
    /**
     * Multiplies the scalar.
     * 
     * @param scalar - the given scalar
     * @return the product of scalar and this matrix
     */
    IMathMatrix<T> multiply(final Unit<T> scalar);
    
    /**
     * Multiplies the other matrix.
     * 
     * @param other - the other matrix
     * @return the product of this matrix with the other matrix ("this*other")
     * @throws IllegalArgumentException if the dimensions do not fit
     * @throws IllegalUnitOperationException if the units do not fit 
     */
    IMathMatrix<T> multiply(final IMathMatrix<T> other) throws IllegalArgumentException, IllegalUnitOperationException;
    
    /**
     * Calculates the determinant of this matrix.
     * 
     * @return the determinant of this matrix
     * @throws UnsupportedOperationException if the matrix is not quadratic
     */
    T determinant() throws UnsupportedOperationException;
}
