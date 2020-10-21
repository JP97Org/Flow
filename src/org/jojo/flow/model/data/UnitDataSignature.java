package org.jojo.flow.model.data;

import java.util.Objects;

import org.jojo.flow.model.api.IDataSignature;
import org.jojo.flow.model.api.UnitSignature;

public class UnitDataSignature extends BasicSignatureComponentSignature {
    /**
     * 
     */
    private static final long serialVersionUID = 7235258735846170579L;
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
    public boolean matches(final IDataSignature other) {
        if (super.matches(other)) {
            if (!isChecking() || !((DataSignature)other).isChecking()) {
                return true;
            }
            return this.unit.equals(((UnitDataSignature) other).unit);
        }
        return false;
    }
    
    @Override
    public String toString() {
        return toStringDs() + this.unit;
    }
    
    @Override
    public DataSignature ofString(final String info) {
        final UnitSignature unitLocal = UnitSignature.ofString(info);
        return unitLocal != null ? new UnitDataSignature(unitLocal) : null;
    }
}
