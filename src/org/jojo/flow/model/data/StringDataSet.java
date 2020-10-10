package org.jojo.flow.model.data;

import java.util.Objects;

public final class StringDataSet extends BasicCheckable {
    /**
     * 
     */
    private static final long serialVersionUID = 6185260321487339088L;
    private final String str;
    private final DataSignature dataSignature;
    
    public StringDataSet(final String str) {
        this.str = Objects.requireNonNull(str);
        this.dataSignature = new BasicSignature(this);
    }

    @Override
    public int[] getSizes() {
        return new int[] {this.str.length()};
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
        return DataSignature.STRING;
    }

    @Override
    public DataSignature getDataSignature() {
        return this.dataSignature;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.str);
    }
    
    @Override
    public boolean equals(final Object other) {
        if (super.equals(other)) {
            return this.str.equals(((StringDataSet)other).str);
        }
        return false;
    }

    @Override
    public String toString() {
        return this.str;
    }
}
