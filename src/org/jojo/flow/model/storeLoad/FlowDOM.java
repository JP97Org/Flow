package org.jojo.flow.model.storeLoad;

import org.jojo.flow.exc.Warning;
import org.jojo.flow.model.api.IDOM;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class FlowDOM extends DOM {
    protected static final String NAME = "Flow";
    
    private final IDOM flowChartDOM;
    
    public FlowDOM(final IDOM dom) {
        super(DOM.getDocumentForCreatingElements(), DOM.getDocumentForCreatingElements().hasChildNodes() 
                ? DOM.getDocumentForCreatingElements().getElementsByTagName(NAME).item(0)
                        : DOM.getDocumentForCreatingElements().createElement(NAME));
        this.flowChartDOM = dom;
        if (!getDocument().hasChildNodes()) {
            appendCustomDOM(this.flowChartDOM);
            getDocument().appendChild(getParentNode());
        }
    }

    public static FlowDOM of(final Document xmlDocument) {
        final NodeList docNodeList = xmlDocument.getChildNodes();
        Node flowNode = null;
        for (int i = 0; i < docNodeList.getLength(); i++) {
            final Node node = docNodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals(NAME)) {
                flowNode = node;
                break;
            }
        }
        if (flowNode == null) {
            new Warning(null, "no flow node found", true).reportWarning();
            return null; 
        }
        final NodeList flowNodeList = flowNode.getChildNodes();
        Node flowChartNode = null;
        for (int i = 0; i < flowNodeList.getLength(); i++) {
            final Node node = flowNodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals(FlowChartDOM.NAME)) {
                flowChartNode = node;
                break;
            }
        }
        return new FlowDOM(new DOM(xmlDocument, flowChartNode) {});
    }

    public IDOM getFlowChartDOM() {
        return this.flowChartDOM;
    }
}
