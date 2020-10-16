package org.jojo.flow.model.data;

import java.util.Arrays;
import java.util.Objects;

import org.jojo.flow.model.api.BasicType;
import org.jojo.flow.model.api.IMatrix;
import org.jojo.flow.model.api.UnitSignature;

public final class Matrix<T> extends BasicCheckable implements IMatrix<T> {
    /**
     * 
     */
    private static final long serialVersionUID = -7329803310105934770L;
    private final T[][] matrix;
    private final UnitSignature unit;
    private final DataSignature dataSignature;
    
    public Matrix(final T[][] matrix, final UnitSignature unit) throws IllegalArgumentException {
        this.matrix = matrix;
        final boolean ok = this.matrix.length > 0 
                && this.matrix[0].length > 0 
                && Arrays.stream(matrix).allMatch(x -> x.length == matrix[0].length);
        if (!ok) {
            throw new IllegalArgumentException("matrix must be rectangular and must contain at least one value");
        }
        this.unit = Objects.requireNonNull(unit);
        this.dataSignature = new BasicSignature(this);
    }
    
    @Override
    public Pair<T[][], UnitSignature> getMatrix() {
        return new Pair<T[][], UnitSignature>(this.matrix, this.unit);
    }

    @Override
    public int[] getSizes() {
        return new int[] {this.matrix.length, this.matrix[0].length};
    }

    @Override
    public UnitSignature getUnitSignature() {
        return this.unit;
    }

    @Override
    public BasicType getBasicType() {
        return BasicType.of(this.matrix[0][0]);
    }

    @Override
    protected int getDataId() {
        return DataSignature.MATRIX;
    }

    @Override
    public DataSignature getDataSignature() {
        return this.dataSignature;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.unit, Arrays.deepHashCode(this.matrix));
    }
    
    @Override
    public boolean equals(final Object other) {
        if (super.equals(other)) {
            final Matrix<?> otherM = (Matrix<?>)other;
            return unit.equals(otherM.unit) && Arrays.deepEquals(this.matrix , otherM.matrix);
        }
        return false;
    }

    @Override
    public String toString() {
        return Arrays.deepToString(this.matrix) + " " + this.unit;
    }
}
