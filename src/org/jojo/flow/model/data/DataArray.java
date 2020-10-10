package org.jojo.flow.model.data;

import java.util.Arrays;
import java.util.Iterator;

public class DataArray extends RecursiveCheckable implements Iterable<Data> {
    /**
     * 
     */
    private static final long serialVersionUID = -8746884178319083769L;
    private final Data[] data;
    private final DataSignature dataSignature;
    
    public DataArray(final Data[] data, final DataSignature componentSignature) throws DataTypeIncompatException {
        this.data = data;
        if (!componentSignature.isCheckingRecursive()) {
            throw new DataTypeIncompatException("the component signature must be checking recursive");
        }
        if (Arrays.stream(data).anyMatch(x -> !x.hasSameType(componentSignature))) {
            throw new DataTypeIncompatException("all data must have this signature: " + componentSignature);
        }
        this.dataSignature = new RecursiveSignature(this);
    }
    
    @Override
    public Data get(int index) {
        return this.data[index];
    }

    @Override
    public int size() {
        return this.data.length;
    }

    @Override
    public boolean isSizeConstant() {
        return true;
    }

    @Override
    protected int getDataId() {
        return DataSignature.ARRAY;
    }

    @Override
    public DataSignature getDataSignature() {
        return this.dataSignature;
    }

    @Override
    public String toString() {
        return Arrays.toString(this.data);
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.data);
    }
    
    @Override
    public boolean equals(final Object other) {
        if (super.equals(other)) {
            final DataArray otherM = (DataArray)other;
            return Arrays.equals(this.data, otherM.data);
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
