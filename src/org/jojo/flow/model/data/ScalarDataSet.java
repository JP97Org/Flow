package org.jojo.flow.model.data;

import java.util.Objects;

import org.jojo.flow.api.BasicType;

public final class ScalarDataSet<T extends Number> extends BasicCheckable {
    /**
     * 
     */
    private static final long serialVersionUID = 4809978069421225901L;
    private final Unit<T> scalar;
    private final DataSignature dataSignature;
    
    public ScalarDataSet(final T value, final UnitSignature unit) {
        this.scalar = new Unit<T>(BasicType.of(value), Objects.requireNonNull(value), unit);
        this.dataSignature = new BasicSignature(this);
    }
    
    public ScalarDataSet(final Unit<T> unit) {
        this(unit.value, unit.unit);
    }
    
    public Unit<T> getScalar() {
        return this.scalar;
    }

    @Override
    public int[] getSizes() {
        return new int[] {DataSignature.NO_SIZES};
    }

    @Override
    public UnitSignature getUnitSignature() {
        return this.scalar.unit;
    }

    @Override
    public BasicType getBasicType() {
        return BasicType.of(this.scalar.type);
    }

    @Override
    protected int getDataId() {
        return DataSignature.SCALAR;
    }

    @Override
    public DataSignature getDataSignature() {
        return this.dataSignature;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.scalar);
    }
    
    @Override
    public boolean equals(final Object other) {
        if (super.equals(other)) {
            return this.scalar.equals(((ScalarDataSet<?>)other).scalar);
        }
        return false;
    }

    @Override
    public String toString() {
        return "" + this.scalar;
    }
}
