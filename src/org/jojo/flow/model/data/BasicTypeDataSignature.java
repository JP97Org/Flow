package org.jojo.flow.model.data;

import java.util.Objects;

public class BasicTypeDataSignature extends BasicSignatureComponentSignature {
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
            return this.basicType.equals(((BasicTypeDataSignature) other).basicType);
        }
        return false;
    }
}
