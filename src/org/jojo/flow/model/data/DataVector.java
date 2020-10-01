package org.jojo.flow.model.data;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class DataVector extends RecursiveCheckable implements Iterable<Data> {
    private final List<Data> data;
    private final DataSignature dataSignature;
    
    public DataVector(final List<Data> data, final DataSignature componentSignature) throws DataTypeIncompatException {
        this.data = data;
        if (!componentSignature.isCheckingRecursive()) {
            throw new DataTypeIncompatException("the component signature must be checking recursive");
        }
        if (data.stream().anyMatch(x -> !x.hasSameType(componentSignature))) {
            throw new DataTypeIncompatException("all data must have this signature: " + componentSignature);
        }
        this.dataSignature = new RecursiveSignature(this);
    }
    
    @Override
    public Data get(int index) {
        return this.data.get(index);
    }

    @Override
    public int size() {
        return this.data.size();
    }

    @Override
    public boolean isSizeConstant() {
        return false;
    }

    @Override
    protected int getDataId() {
        return DataSignature.VECTOR;
    }

    @Override
    public DataSignature getDataSignature() {
        return this.dataSignature;
    }

    @Override
    public String toString() {
        return "" + this.data;
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(this.data);
    }
    
    @Override
    public boolean equals(final Object other) {
        if (super.equals(other)) {
            final DataVector otherM = (DataVector)other;
            return this.data.equals(otherM.data);
        }
        return false;
    }

    @Override
    public Iterator<Data> iterator() {
        return new Iterator<>() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < size();
            }

            @Override
            public Data next() {
                if (hasNext()) {
                    return get(index++);
                }
                return null;
            }
        };
    }

}
