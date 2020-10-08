package org.jojo.flow.model.storeLoad;

import org.w3c.dom.Document;

public class XMLDocumentCreator {
    private final Document xmlDocument;
    
    public XMLDocumentCreator(final FlowDOM flowDom) {
        this.xmlDocument = DOM.getDocumentForCreatingElements();
        this.xmlDocument.appendChild(flowDom.getParentNode());
    }
    
    public Document getXMLDocument() {
        return this.xmlDocument;
    }
}
