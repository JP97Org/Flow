package org.jojo.flow.model.data;

abstract class BasicCheckable extends Data {
    public abstract int[] getSizes();
    public abstract UnitSignature getUnitSignature();
    public abstract BasicType getBasicType();
}
