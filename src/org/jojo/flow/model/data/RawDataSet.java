package org.jojo.flow.model.data;

import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;

import org.jojo.flow.api.BasicType;

public final class RawDataSet extends BasicCheckable {
    /**
     * 
     */
    private static final long serialVersionUID = -2317113448843956394L;
    private final byte[] data;
    private final DataSignature dataSignature;
    
    public RawDataSet(final byte[] data) {
        this.data = Objects.requireNonNull(data);
        this.dataSignature = new BasicSignature(this);
    }
    
    public byte[] getData() {
        return this.data;
    }

    @Override
    public int[] getSizes() {
        return new int[] {this.data.length};
    }

    @Override
    public UnitSignature getUnitSignature() {
        return null;
    }

    @Override
    public BasicType getBasicType() {
        return null;
    }

    @Override
    protected int getDataId() {
        return DataSignature.RAW;
    }

    @Override
    public DataSignature getDataSignature() {
        return this.dataSignature;
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.data);
    }
    
    @Override
    public boolean equals(final Object other) {
        if (super.equals(other)) {
            return Arrays.equals(this.data, ((RawDataSet)other).data);
        }
        return false;
    }

    @Override
    public String toString() {
        return Base64.getEncoder().encodeToString(this.data);
    }
}
