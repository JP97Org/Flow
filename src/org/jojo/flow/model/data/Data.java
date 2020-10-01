package org.jojo.flow.model.data;

public abstract class Data {
    protected abstract int getDataId();
    
    public abstract DataSignature getDataSignature();
    
    public boolean hasSameType(final DataSignature other) {
        return getDataSignature().equals(other);
    }
    
    @Override
    public int hashCode() {
        return getDataSignature().hashCode();
    }
    
    @Override
    public boolean equals(final Object other) {
        if (other != null && other instanceof Data) {
            return hasSameType(((Data)other).getDataSignature());
        }
        return false;
    }
    
    @Override
    public abstract String toString();
}
