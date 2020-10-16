package org.jojo.flow.model.data;

import java.util.Arrays;

import org.jojo.flow.model.api.IData;
import org.jojo.flow.model.api.IDataArray;
import org.jojo.flow.model.api.IDataSignature;

public class DataArray extends RecursiveCheckable implements IDataArray {
    /**
     * 
     */
    private static final long serialVersionUID = -8746884178319083769L;
    private final IData[] data;
    private final IDataSignature dataSignature;
    
    public DataArray(final IData[] data, final IDataSignature iDataSignature) throws DataTypeIncompatException {
        this.data = data;
        if (!iDataSignature.isCheckingRecursive()) {
            throw new DataTypeIncompatException("the component signature must be checking recursive");
        }
        if (Arrays.stream(data).anyMatch(x -> !x.hasSameType(iDataSignature))) {
            throw new DataTypeIncompatException("all data must have this signature: " + iDataSignature);
        }
        this.dataSignature = new RecursiveSignature(this);
    }
    
    @Override
    public IData get(int index) {
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
    public IDataSignature getDataSignature() {
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

}
