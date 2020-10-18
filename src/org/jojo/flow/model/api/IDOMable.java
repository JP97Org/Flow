package org.jojo.flow.model.api;

import org.jojo.flow.model.storeLoad.DOM;

public interface IDOMable extends IAPI {
    DOM getDOM();
    void restoreFromDOM(DOM dom);
    boolean isDOMValid(DOM dom);
}
