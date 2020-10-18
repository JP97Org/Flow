package org.jojo.flow.model.api;

import org.jojo.flow.model.storeLoad.DOM;

public interface IDOMable extends IAPI {
    
    DOM getDOM(); //TODO org.jojo.flow.model.storeLoad.DOM is the only implementation for restoring at the moment
    
    void restoreFromDOM(DOM dom);
    
    boolean isDOMValid(DOM dom);
}
