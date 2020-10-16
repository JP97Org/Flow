package org.jojo.flow.model.data;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.jojo.flow.api.IDataSignature;

public class DataVector extends RecursiveCheckable implements Iterable<Data> {
    /**
     * 
     */
    private static final long serialVersionUID = 4082764662720440203L;
    private int lastKnownSize;
    private final List<Data> data;
    private final IDataSignature componentSignature;
    private IDataSignature dataSignature;
    
    public DataVector(final List<Data> data, final IDataSignature iDataSignature) throws DataTypeIncompatException {
        Objects.requireNonNull(data);
        this.lastKnownSize = data.size();
        this.data = data;
        if (!iDataSignature.isCheckingRecursive()) {
            throw new DataTypeIncompatException("the component signature must be checking recursive");
        }
        if (data.stream().anyMatch(x -> !x.hasSameType(iDataSignature))) {
            throw new DataTypeIncompatException("all data must have this signature: " + iDataSignature);
        }
        this.componentSignature = iDataSignature;
        this.dataSignature = new RecursiveSignature(this);
    }
    
    public void add(final Data toAdd) throws DataTypeIncompatException {
        if (!toAdd.hasSameType(this.componentSignature)) {
            throw new DataTypeIncompatException("all data must have this signature: " + componentSignature);
        }
        this.data.add(toAdd);
    }
    
    public void remove(final int index) {
        this.data.remove(index);
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
    public IDataSignature getDataSignature() {
        return this.dataSignature;
    }

    @Override
    public String toString() {
        return "" + this.data;
    }
    
    @Override
    public int hashCode() {
        return Arrays.deepHashCode(this.data.toArray());
    }
    
    private void update() {
        if (this.lastKnownSize != this.data.size()) {
            this.lastKnownSize = this.data.size();
            this.dataSignature = new RecursiveSignature(this);
        }
    }
    
    @Override
    public boolean equals(final Object other) {
        update();
        if (other instanceof DataVector) {
            ((DataVector) other).update();
        }
        
        if (super.equals(other)) {
            final DataVector otherM = (DataVector)other;
            return Arrays.deepEquals(this.data.toArray(), otherM.data.toArray());
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
