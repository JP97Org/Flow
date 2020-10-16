package org.jojo.flow.model.api;

public interface IBasicCheckable extends IData {
    
    int[] getSizes();
    
    UnitSignature getUnitSignature();
    
    BasicType getBasicType();
}
