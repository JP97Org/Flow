package org.jojo.flow;

import org.jojo.flow.model.flowChart.FlowChart;
import org.jojo.flow.model.storeLoad.DynamicClassLoader;

public class Main {
    // TODO at the moment only test main class
    public static void main(String[] args) {
        FlowChart flowChart = new FlowChart(0);
        flowChart.addModule(DynamicClassLoader.loadModule(""));
        flowChart.addConnection(DynamicClassLoader.loadConnection(""));
        final var dom = flowChart.getDOM();
        flowChart.restoreFromDOM(dom);
    }
}
