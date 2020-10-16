package org.jojo.flow.model.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.jojo.flow.api.BasicType;

public final class MathMatrix<T extends Number> extends BasicCheckable {
    /**
     * 
     */
    private static final long serialVersionUID = -1293085528815395762L;
    private final Number[][] matrix;
    private final UnitSignature unit;
    private final DataSignature dataSignature;
    
    public MathMatrix(final T[][] matrix, final UnitSignature unit) throws IllegalArgumentException {
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
    
    @SuppressWarnings("unchecked")
    private MathMatrix(final Number[][] matrix, final UnitSignature unit, final boolean ignored) {
        this((T[][])matrix, unit);
    }
    
    public Pair<T[][], UnitSignature> getMatrix() {
        @SuppressWarnings("unchecked")
        final T[][] tmatr = (T[][]) this.matrix;
        return new Pair<T[][], UnitSignature>(tmatr, this.unit);
    }
    
    public MathMatrix<T> add(final MathMatrix<T> other) throws IllegalArgumentException, IllegalUnitOperationException {
        if (!getBasicType().equals(other.getBasicType())) {
            throw new IllegalArgumentException("other matrix does not have the same basic type");
        } else if (this.matrix.length != other.matrix.length || this.matrix[0].length != other.matrix[0].length) { 
            throw new IllegalArgumentException("incorrect dimensions");
        }
        this.unit.add(other.unit); // check if units are ok
        final List<List<Number>> retList = new ArrayList<>();
        
        for (int o = 0; o < this.matrix.length; o++) {
            final List<Number> toAdd = new ArrayList<>();
            for (int i = 0; i < this.matrix[o].length; i++) {
                final Number value = this.matrix[o][i];
                final Number otherValue = other.matrix[o][i];
                final Unit<Number> unitThisVal = new Unit<>(BasicType.of(value), value, UnitSignature.NO_UNIT);
                final Unit<Number> unitOtherVal = new Unit<>(BasicType.of(otherValue), otherValue, UnitSignature.NO_UNIT);
                final Number result = unitThisVal.add(unitOtherVal).value;
                toAdd.add(result);
            }
            retList.add(toAdd);
        }

        return new MathMatrix<T>(toArray(retList), this.unit.add(other.unit));
    }
    
    private T[][] toArray(List<List<Number>> retList) {
        final Number[][] ret = new Number[retList.size()][retList.get(0).size()];
        for (int o = 0; o < ret.length ; o++) {
            for (int i = 0; i < ret[o].length; i++) {
                ret[o][i] = retList.get(o).get(i);
            }
        }
        @SuppressWarnings("unchecked")
        final T[][] retT = (T[][]) ret;
        return retT;
    }
    
    public MathMatrix<T> multiply(final Unit<T> scalar) {
        @SuppressWarnings("unchecked")
        final MathMatrix<T> ret = new MathMatrix<T>(Arrays
                .stream(this.matrix)
                .map(x -> Arrays
                        .stream(x)
                        .map(y -> {
                            try {
                                return new Unit<T>(scalar.type, (T) y, UnitSignature.NO_UNIT).multiply(scalar).value;
                            } catch (IllegalUnitOperationException e) {
                                // should not happen
                                e.printStackTrace();
                                return null;
                            }
                        }).toArray(Number[]::new))
                .toArray(Number[][]::new), this.unit.multiply(scalar.unit), true);
        return ret;
    }
    
    public MathMatrix<T> multiply(final MathMatrix<T> other) throws IllegalArgumentException, IllegalUnitOperationException {
        return new MathMatrix<T>(multiply(this.matrix, other.matrix), this.unit.multiply(other.unit));
    }
    
    private T[][] multiply(final Number[][] matrixA, final Number[][] matrixB) {
        if (matrixA[0].length != matrixB.length) {
            throw new IllegalArgumentException("incorrect dimensions");
        }

        final int height = matrixA.length;
        final int width = matrixB[0].length;
        final List<List<Number>> res = new ArrayList<>();

        final Unit.Type type = BasicType.of(matrixA[0][0]);
        final UnitSignature no = UnitSignature.NO_UNIT;
        
        for (int i = 0; i < height; i++) {
            final List<Number> toAdd = new ArrayList<>();
            for (int j = 0; j < width; j++) {
                Unit<Number> resultEntry = new Unit<>(type, type.transformToCorrectType(0), no);
                for (int k = 0; k < width; k++) {
                    final Unit<Number> factorOne = new Unit<>(type, matrixA[i][k], no);
                    final Unit<Number> factorTwo = new Unit<>(type, matrixB[k][j], no);
                    try {
                        resultEntry = resultEntry.add(factorOne.multiply(factorTwo));
                    } catch (IllegalUnitOperationException e) {
                        // should not happen
                        e.printStackTrace();
                        return null;
                    }
                }
                final Number toAddVal = resultEntry.value;
                toAdd.add(toAddVal);
            }
            res.add(toAdd);
        }
        
        return toArray(res);
    }
    
    public T determinant() throws UnsupportedOperationException {
        if (this.matrix.length != this.matrix[0].length) {
            throw new UnsupportedOperationException("matrix must be quadratic");
        }
        
        return determinant(this.matrix);
    }
    
    private T determinant(final Number[][] submatrix) {
        // base-case
        if (submatrix.length == 1) {
            @SuppressWarnings("unchecked")
            final T ret = (T) submatrix[0][0];
            return ret;
        }
        
        // development with first row
        final Unit.Type type = BasicType.of(submatrix[0][0]);
        final UnitSignature no = UnitSignature.NO_UNIT;
        Unit<Number> ret = new Unit<Number>(type, type.transformToCorrectType(0), no);
        for (int i = 0; i < submatrix.length; i++) {
            final Unit<Number> factorOne = new Unit<Number>(type, 
                    type.transformToCorrectType(Math.pow(-1, i)), no);
            final Unit<Number> factorTwo = new Unit<Number>(type, submatrix[0][1], no);
            final Unit<Number> factorThree = new Unit<Number>(type, 
                    determinant(smallerMatrix(submatrix, 0, i)), no);
            final Unit<Number> toAdd;
            try {
                toAdd = factorOne.multiply(factorTwo).multiply(factorThree);
                ret = ret.add(toAdd); 
            } catch (IllegalUnitOperationException e) {
                // should not happen
                e.printStackTrace();
                return null;
            }
        }
        @SuppressWarnings("unchecked")
        final T retVal = (T) ret.value;
        return retVal;
    }

    private Number[][] smallerMatrix(final Number[][] submatrix, int row, int col) {
        final List<List<Number>> retList = new ArrayList<>();
        
        for (int o = 0; o < submatrix.length; o++) {
            if (o != row) {
                final List<Number> toAdd = new ArrayList<>();
                for (int i = 0; i < submatrix[o].length; i++) {
                    if (i != col) {
                        toAdd.add(submatrix[o][i]);
                    }
                }
                retList.add(toAdd);
            }
        }

        return toArray(retList);
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
        return BasicType.of(BasicType.of(this.matrix[0][0]));
    }

    @Override
    protected int getDataId() {
        return DataSignature.MATH_MATRIX;
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
            final MathMatrix<?> otherM = (MathMatrix<?>)other;
            return unit.equals(otherM.unit) && Arrays.deepEquals(this.matrix , otherM.matrix);
        }
        return false;
    }

    @Override
    public String toString() {
        return Arrays.deepToString(this.matrix) + " " + this.unit;
    }
}
