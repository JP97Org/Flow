package org.jojo.flow.model.storeLoad;

public interface DOMable {
    DOM getDOM();
    void restoreFromDOM(DOM dom);
    boolean isDOMValid(DOM dom);
}
