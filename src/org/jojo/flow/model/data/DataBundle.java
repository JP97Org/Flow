package org.jojo.flow.model.data;

import java.util.Arrays;
import java.util.List;

import org.jojo.flow.model.api.IData;
import org.jojo.flow.model.api.IDataBundle;
import org.jojo.flow.model.api.IDataSignature;

public class DataBundle extends RecursiveCheckable implements IDataBundle {
    /**
     * 
     */
    private static final long serialVersionUID = -1411478059148157427L;
    private final IData[] data;
    private final IDataSignature dataSignature;
    
    public DataBundle(final List<IData> data) throws DataTypeIncompatException  {
        this(data.toArray(new IData[data.size()]));
    }
    
    public DataBundle(final IData[] data) throws DataTypeIncompatException {
        this.data = data;
        final DataSignature[] componentSignatures = Arrays.stream(data)
                .map(x -> x.getDataSignature())
                .toArray(DataSignature[]::new);
        if (Arrays.stream(componentSignatures).allMatch(x -> !x.isCheckingRecursive())) {
            throw new DataTypeIncompatException("all component signatures must be checking recursive");
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
        return DataSignature.BUNDLE;
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
            final DataBundle otherM = (DataBundle)other;
            return Arrays.equals(this.data, otherM.data);
        }
        return false;
    }
}
