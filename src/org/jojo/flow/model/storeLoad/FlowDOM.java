package org.jojo.flow.model.storeLoad;

import java.util.Objects;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class FlowDOM extends DOM {
    protected static final String NAME = "Flow";
    
    private final FlowChartDOM flowChartDOM;
    
    public FlowDOM(final Document document, final Node parent) {
        super(Objects.requireNonNull(document), parent);
        final Element flowChartElement = document.createElement(FlowChartDOM.NAME);
        addElement(flowChartElement);
        this.flowChartDOM = new FlowChartDOM(document, flowChartElement);
    }

    public FlowChartDOM getFlowChartDOM() {
        return this.flowChartDOM;
    }
}
