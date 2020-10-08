package org.jojo.flow.model.storeLoad;

public class FlowDOM extends DOM {
    protected static final String NAME = "Flow";
    
    private final FlowChartDOM flowChartDOM;
    
    public FlowDOM(final FlowChartDOM flowChartDOM) {
        super(DOM.getDocumentForCreatingElements(), DOM.getDocumentForCreatingElements().createElement(NAME));
        this.flowChartDOM = flowChartDOM;
    }

    public FlowChartDOM getFlowChartDOM() {
        return this.flowChartDOM;
    }
}
