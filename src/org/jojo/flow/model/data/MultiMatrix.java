package org.jojo.flow.model.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.jojo.flow.model.api.BasicType;
import org.jojo.flow.model.api.IMultiMatrix;
import org.jojo.flow.model.api.UnitSignature;

public final class MultiMatrix<T> extends BasicCheckable implements IMultiMatrix<T> {
    /**
     * 
     */
    private static final long serialVersionUID = -8288349719294892611L;
    private final int[] sizes;
    private final T[] multiMatrix;
    private final UnitSignature unit;
    private final DataSignature dataSignature;
    
    public MultiMatrix(final int[] sizes, final UnitSignature unit) throws IllegalArgumentException {
        this.sizes = Objects.requireNonNull(sizes);
        final boolean ok = this.sizes.length > 0 
                && Arrays.stream(this.sizes).allMatch(x -> x >= 0);
        if (!ok) {
            throw new IllegalArgumentException("sizes must contain only non-negative values and at least one value");
        }
        final List<T> dataList = new ArrayList<T>();
        for (int i = 0; i < getProductOfSmallerSizes(-1); i++) {
            dataList.add(null);
        }
        @SuppressWarnings("unchecked")
        final T[] multiMatrix = (T[]) dataList.toArray();
        this.multiMatrix = multiMatrix;
        this.unit = Objects.requireNonNull(unit);
        this.dataSignature = new BasicSignature(this);
    }
    
    public MultiMatrix(final int[] sizes, final T[] data, final UnitSignature unit) throws IllegalArgumentException {
        this(sizes, unit);
        if (data.length != this.multiMatrix.length) {
            throw new IllegalArgumentException("data does not match declared sizes");
        }
        for (int i = 0; i < data.length; i++) {
            this.multiMatrix[i] = data[i];
        }
    }
    
    public T get(final int[] indices) throws IllegalArgumentException {
        return this.multiMatrix[getIndexOf(indices)];
    }
    
    private int getIndexOf(final int[] indices) throws IllegalArgumentException {
        final int len = indices.length;
        if (len != this.sizes.length) {
            throw new IllegalArgumentException("indices does not match declared sizes");
        }
        boolean ok = true;
        for (int i = 0; ok && i < len ; i++) {
            ok &= indices[i] < this.sizes[i];
        }
        if(!ok) {
            throw new IllegalArgumentException("at least one index is out of bounds");
        }
        
        int index = 0;
        for (int i = len - 1; i >= 0; i--) {
            final int productOfSmallerSizes = getProductOfSmallerSizes(i);
            index += indices[i] * productOfSmallerSizes;
        }
        return index;
    }
    
    private int getProductOfSmallerSizes(final int sizesIndex) {
        final int[] smallerSizes = new int[this.sizes.length - (sizesIndex + 1)];
        for (int i = 0; i < smallerSizes.length; i++) {
            smallerSizes[i] = this.sizes[this.sizes.length - (i + 1)];
        }
        return Arrays.stream(smallerSizes).reduce(1, (a,b) -> a * b);
    }

    public T[] getRow(final int[] otherIndices) throws IllegalArgumentException {
        final List<T> ret = new ArrayList<>();
        final List<Integer> indices = new ArrayList<>();
        for (final int index : otherIndices) {
            indices.add(index);
        }
        for (int i = 0; i < this.sizes[this.sizes.length - 1]; i++) {
            indices.add(i);
            ret.add(get(indices.stream().mapToInt(x -> x).toArray()));
            indices.remove(indices.size() - 1);
        }
        @SuppressWarnings("unchecked")
        final T[] retArr = (T[]) ret.toArray();
        return retArr;
    }
    
    public void set(final T value, final int[] indices) throws IllegalArgumentException {
        this.multiMatrix[getIndexOf(indices)] = value;
    }

    @Override
    public int[] getSizes() {
        return Arrays.stream(this.sizes).toArray();
    }

    @Override
    public UnitSignature getUnitSignature() {
        return this.unit;
    }

    @Override
    public BasicType getBasicType() {
        return BasicType.of(this.multiMatrix[0]);
    }

    @Override
    protected int getDataId() {
        return DataSignature.MULTI_MATRIX;
    }

    @Override
    public DataSignature getDataSignature() {
        return this.dataSignature;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(this.sizes), this.unit, Arrays.deepHashCode(this.multiMatrix));
    }
    
    @Override
    public boolean equals(final Object other) {
        if (super.equals(other)) {
            final MultiMatrix<?> otherM = (MultiMatrix<?>)other;
            return Arrays.equals(this.sizes, otherM.sizes) 
                    && unit.equals(otherM.unit) 
                    && Arrays.deepEquals(this.multiMatrix , otherM.multiMatrix);
        }
        return false;
    }
    
    @Override
    public String toString() {
        return Arrays.deepToString(toArray()) + " " + this.unit;
    }

    public Object[] toArray() {
        return toArray(this.multiMatrix, this.sizes);
    }

    private Object[] toArray(final Object[] localMatrix, final int[] localSizes) {
        if (localSizes.length == 1) {
            assert localSizes[0] == localMatrix.length;
            return localMatrix;
        } else {
            final int sizeNow = localSizes[0];
            final List<Object[]> ret = new ArrayList<>();
            for (int k = 0; k < sizeNow; k++) {
                final int[] newLocalSizes = new int[localSizes.length - 1];
                for (int i = 1; i-1 < newLocalSizes.length; i++) {
                    newLocalSizes[i-1] = localSizes[i];
                }
                
                final int productSizes = Arrays.stream(newLocalSizes).reduce(1, (a,b) -> a*b);
                final int beginIndex = k * productSizes;
                final int endIndex = (k + 1) * productSizes - 1;
                final List<Object> newLocalMatrix = new ArrayList<>();
                for (int i = beginIndex; i <= endIndex ; i++) {
                    newLocalMatrix.add(localMatrix[i]);
                }

                final Object[] nlm = newLocalMatrix.toArray();
                ret.add(toArray(nlm, newLocalSizes));
            }
            return ret.toArray();
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < multiMatrix.length;
            }

            @Override
            public T next() {
                if (hasNext()) {
                    return multiMatrix[index++];
                }
                return null;
            }
        };
    }
}
