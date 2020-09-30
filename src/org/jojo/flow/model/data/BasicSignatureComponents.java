package org.jojo.flow.model.data;

public enum BasicSignatureComponents {
    SIZES(0), BASIC_TYPE(1), UNIT(2);
    
    public final int index;
    
    BasicSignatureComponents(final int index) {
        this.index = index;
    }
}
