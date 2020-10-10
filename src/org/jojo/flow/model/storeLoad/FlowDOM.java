package org.jojo.flow.model.storeLoad;

import org.w3c.dom.Document;

public class FlowDOM extends DOM {
    protected static final String NAME = "Flow";
    
    private final DOM flowChartDOM;
    
    public FlowDOM(final DOM flowChartDOM) {
        super(DOM.getDocumentForCreatingElements(), DOM.getDocumentForCreatingElements().createElement(NAME));
        this.flowChartDOM = flowChartDOM;
        appendCustomDOM(this.flowChartDOM);
        getDocument().appendChild(getParentNode());
    }

    public static FlowDOM of(final Document xmlDocument) {
        return new FlowDOM(new DOM(xmlDocument, xmlDocument.getFirstChild()) {});
    }

    public DOM getFlowChartDOM() {
        return this.flowChartDOM;
    }
}
