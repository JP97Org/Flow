package org.jojo.flow;

import org.jojo.flow.model.ModelFacade;
import org.jojo.flow.model.flowChart.FlowChart;
import org.jojo.flow.model.flowChart.FlowChartGR;
import org.jojo.flow.model.storeLoad.DOM;
import org.jojo.flow.model.storeLoad.DynamicClassLoader;

public class Main {
    // TODO at the moment only test main class
    public static void main(String[] args) {
        FlowChart flowChart = new FlowChart(0, new FlowChartGR());
        ModelFacade.flowChart = flowChart;
        flowChart.addModule(DynamicClassLoader.loadModule(""));
        flowChart.addConnection(DynamicClassLoader.loadConnection(""));
        final var dom = flowChart.getDOM();
        System.out.println(flowChart.isDOMValid(dom));
        flowChart.restoreFromDOM(dom);
        System.out.println(flowChart.getWarnings());
        System.out.println(ModelFacade.mock.getWarnings());
        /*
        flowChart.restoreFromDOM(new DOM(DOM.getDocumentForCreatingElements(), DOM.getDocumentForCreatingElements()) {});
        System.out.println(flowChart.isDOMValid(new DOM(DOM.getDocumentForCreatingElements(), DOM.getDocumentForCreatingElements()) {}));
        System.out.println(flowChart.getWarnings());
        */
    }
}
