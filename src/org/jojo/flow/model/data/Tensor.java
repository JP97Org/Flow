package org.jojo.flow.model.data;

import java.util.Arrays;
import java.util.Objects;

import org.jojo.flow.model.api.BasicType;
import org.jojo.flow.model.api.ITensor;
import org.jojo.flow.model.api.Pair;
import org.jojo.flow.model.api.UnitSignature;

public final class Tensor<T> extends BasicCheckable implements ITensor<T> {
    /**
     * 
     */
    private static final long serialVersionUID = 5201610391007567526L;
    private final T[][][] tensor;
    private final UnitSignature unit;
    private final DataSignature dataSignature;
    
    public Tensor(final T[][][] tensor, final UnitSignature unit) throws IllegalArgumentException {
        this.tensor = tensor;
        final boolean ok = this.tensor.length > 0 
                && this.tensor[0].length > 0 
                && this.tensor[0][0].length > 0
                && Arrays.stream(tensor).allMatch(x -> x.length == tensor[0].length)
                && Arrays.stream(tensor[0]).allMatch(x -> x.length == tensor[0][0].length);
        if (!ok) {
            throw new IllegalArgumentException("tensor must be 3D-rectangular and must contain at least one value");
        }
        this.unit = Objects.requireNonNull(unit);
        this.dataSignature = new BasicSignature(this);
    }
    
    @Override
    public Pair<T[][][], UnitSignature> getTensor() {
        return new Pair<T[][][], UnitSignature>(this.tensor, this.unit);
    }

    @Override
    public int[] getSizes() {
        return new int[] {this.tensor.length, this.tensor[0].length, this.tensor[0][0].length};
    }

    @Override
    public UnitSignature getUnitSignature() {
        return this.unit;
    }

    @Override
    public BasicType getBasicType() {
        return BasicType.of(this.tensor[0][0][0]);
    }

    @Override
    protected int getDataId() {
        return DataSignature.TENSOR;
    }

    @Override
    public DataSignature getDataSignature() {
        return this.dataSignature;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.unit, Arrays.deepHashCode(this.tensor));
    }
    
    @Override
    public boolean equals(final Object other) {
        if (super.equals(other)) {
            final Tensor<?> otherM = (Tensor<?>)other;
            return unit.equals(otherM.unit) && Arrays.deepEquals(this.tensor , otherM.tensor);
        }
        return false;
    }

    @Override
    public String toString() {
        return Arrays.deepToString(this.tensor) + " " + this.unit;
    }
}
