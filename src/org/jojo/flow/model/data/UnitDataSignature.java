package org.jojo.flow.model.data;

import java.util.Objects;

public class UnitDataSignature<T extends Number> extends BasicSignatureComponentSignature {
    private final Unit<T> unit;
    
    protected UnitDataSignature(final Unit<T> unit) {
        super(BASIC_COMPONENT_UNIT);
        this.unit = Objects.requireNonNull(unit);
    }

    @Override
    public DataSignature getCopy() {
        return new UnitDataSignature<T>(new Unit<T>(this.unit));
    }
    
    @Override
    public boolean equals(final Object other) {
        if (super.equals(other)) {
            return this.unit.equals(((UnitDataSignature<?>) other).unit);
        }
        return false;
    }
}
