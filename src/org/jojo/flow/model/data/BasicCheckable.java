package org.jojo.flow.model.data;

import org.jojo.flow.api.BasicType;

abstract class BasicCheckable extends Data {
    /**
     * 
     */
    private static final long serialVersionUID = 4799013536930152152L;
    public abstract int[] getSizes();
    public abstract UnitSignature getUnitSignature();
    public abstract BasicType getBasicType();
}
