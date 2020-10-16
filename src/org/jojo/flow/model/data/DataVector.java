package org.jojo.flow.model.data;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.jojo.flow.model.api.IData;
import org.jojo.flow.model.api.IDataSignature;
import org.jojo.flow.model.api.IDataVector;

public class DataVector extends RecursiveCheckable implements IDataVector {
    /**
     * 
     */
    private static final long serialVersionUID = 4082764662720440203L;
    private int lastKnownSize;
    private final List<IData> data;
    private final IDataSignature componentSignature;
    private IDataSignature dataSignature;
    
    public DataVector(final List<IData> data, final IDataSignature iDataSignature) throws DataTypeIncompatException {
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
    
    public void add(final IData toAdd) throws DataTypeIncompatException {
        if (!toAdd.hasSameType(this.componentSignature)) {
            throw new DataTypeIncompatException("all data must have this signature: " + componentSignature);
        }
        this.data.add(toAdd);
    }
    
    public void remove(final int index) {
        this.data.remove(index);
    }
    
    @Override
    public IData get(int index) {
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

}
