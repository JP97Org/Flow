package org.jojo.flow.model.data;

import java.util.Objects;

public class UnitDataSignature extends BasicSignatureComponentSignature {
    private final UnitSignature unit;
    
    protected UnitDataSignature(final UnitSignature unit) {
        super(BASIC_COMPONENT_UNIT);
        this.unit = Objects.requireNonNull(unit);
    }

    @Override
    public DataSignature getCopy() {
        return new UnitDataSignature(new UnitSignature(this.unit));
    }
    
    @Override
    public boolean equals(final Object other) {
        if (super.equals(other)) {
            return this.unit.equals(((UnitDataSignature) other).unit);
        }
        return false;
    }
}
