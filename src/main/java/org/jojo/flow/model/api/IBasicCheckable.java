package org.jojo.flow.model.api;

/**
 * Represents a basic checkable IData, i.e. a data which is not recursive in its nature, 
 * i.e. it usually does not contain other IData as data.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public interface IBasicCheckable extends IData {
    
    /**
     * Gets the sizes of this data. Usually the sizes are the length(es) of strings, arrays, lists.
     * Each element of the returned sizes array represents a dimension. <br/>
     * If this data has no sizes, {NO_SIZES}, i.e. {-2} is returned.
     * 
     * @return the sizes of this data
     */
    int[] getSizes();
    
    /**
     * 
     * @return the UnitSignature or {@code null} if this data does not have a unit signature
     */
    UnitSignature getUnitSignature();
    
    /**
     * 
     * @return the BasicType or {@code null} if this data does not have a basic type
     */
    BasicType getBasicType();
}
