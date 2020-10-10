package org.jojo.flow.model.data;

import java.util.Objects;

public class BasicTypeDataSignature extends BasicSignatureComponentSignature {
    /**
     * 
     */
    private static final long serialVersionUID = -3366317648465388657L;
    private final BasicType basicType;
    
    protected BasicTypeDataSignature(final BasicType basicType) {
        super(BASIC_COMPONENT_TYPE);
        this.basicType = Objects.requireNonNull(basicType);
    }

    @Override
    public DataSignature getCopy() {
        return new BasicTypeDataSignature(this.basicType);
    }
    
    @Override
    public boolean equals(final Object other) {
        if (super.equals(other)) {
            if (!isChecking() || !((DataSignature)other).isChecking()) {
                return true;
            }
            return this.basicType.equals(((BasicTypeDataSignature) other).basicType);
        }
        return false;
    }
    
    @Override
    public String toString() {
        return toStringDs() + this.basicType;
    }
    
    @Override
    public DataSignature ofString(final String info) {
        final BasicType type = BasicType.of(info);
        return type != null ? new BasicTypeDataSignature(type) : null;
    }
}
