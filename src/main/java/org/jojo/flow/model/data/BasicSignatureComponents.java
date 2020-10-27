package org.jojo.flow.model.data;

/**
 * This enum represents the components of a {@link BasicSignature} and contains the respective indices.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public enum BasicSignatureComponents {
    SIZES(0), BASIC_TYPE(1), UNIT(2);
    
    /**
     * the component index
     */
    public final int index;
    
    BasicSignatureComponents(final int index) {
        this.index = index;
    }
}
