package org.jojo.flow.test.model.storeLoad;

import java.util.Arrays;

import org.jojo.flow.model.api.IDOM;
import org.jojo.flow.model.storeLoad.DOM;
import org.junit.*;

public abstract class DOMTest {
    protected abstract IDOM[] getDomsUnderTest();
    
    @Test
    public void resetTest() {
        final var doc = DOM.getDocumentForCreatingElements();
        Assert.assertTrue(doc == DOM.getDocumentForCreatingElements());
        IDOM.resetDocument();
        Assert.assertFalse(doc == DOM.getDocumentForCreatingElements());
    }
    
    @Test
    public void getDocumentTest() {
        Arrays
            .stream(getDomsUnderTest())
            .forEach(dom -> 
                Assert.assertEquals(DOM.getDocumentForCreatingElements(), dom.getDocument()));
    }
    
    // append tests and more exact DOMMap tests indirectly in integration tests (loading from document
    // and saving to document)
    
    @Test
    public void getDOMMapTest() {
        Arrays
            .stream(getDomsUnderTest())
            .forEach(dom -> Assert.assertTrue(dom.getDOMMap() != null));
    }
}
