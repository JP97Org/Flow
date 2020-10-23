package org.jojo.flow.model.data;

import org.jojo.flow.model.api.BasicType;
import org.jojo.flow.model.api.IBasicCheckable;
import org.jojo.flow.model.api.UnitSignature;

abstract class BasicCheckable extends Data implements IBasicCheckable {
    /**
     * 
     */
    private static final long serialVersionUID = 4799013536930152152L;
    
    @Override
    public abstract int[] getSizes();
    
    @Override
    public abstract UnitSignature getUnitSignature();
    
    @Override
    public abstract BasicType getBasicType();
}
