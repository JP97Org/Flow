package org.jojo.flow;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import org.jojo.flow.model.ModelFacade;
import org.jojo.flow.model.flowChart.FlowChart;
import org.jojo.flow.model.flowChart.FlowChartElement;
import org.jojo.flow.model.flowChart.FlowChartGR;
import org.jojo.flow.model.flowChart.modules.FlowModule;
import org.jojo.flow.model.storeLoad.DOM;
import org.jojo.flow.model.storeLoad.DynamicObjectLoader;
import org.jojo.flow.model.storeLoad.FlowDOM;
import org.jojo.flow.model.storeLoad.StoreLoadFacade;
import org.jojo.flow.model.storeLoad.DynamicObjectLoader.MockModule;

public class Main {
    // TODO at the moment only test main class
    public static void main(String[] args) {
        FlowChart flowChart = new FlowChart(0, new FlowChartGR());
        ModelFacade.flowChart = flowChart;
        final MockModule mod = (MockModule)DynamicObjectLoader.loadModule(DynamicObjectLoader.MockModule.class.getName());
        System.out.println(FlowChartElement.GENERIC_ERROR_ELEMENT.getWarnings());
        mod.setId(100);
        flowChart.addModule(mod);
        flowChart.addConnection(DynamicObjectLoader.loadConnection(""));
        final var dom = flowChart.getDOM();
        System.out.println(flowChart.isDOMValid(dom));
        flowChart.restoreFromDOM(dom);
        System.out.println(flowChart.getWarnings());
        System.out.println(ModelFacade.mock.getWarnings());
        System.out.println(FlowChartElement.GENERIC_ERROR_ELEMENT.getWarnings());
        final DOM flowDom = new FlowDOM(dom);
        new StoreLoadFacade().storeFlowChart(new File("/home/jojo/Schreibtisch/flow.xml"), flowDom);
        System.out.println(FlowChartElement.GENERIC_ERROR_ELEMENT.getWarnings());
        /*
        flowChart.restoreFromDOM(new DOM(DOM.getDocumentForCreatingElements(), DOM.getDocumentForCreatingElements()) {});
        System.out.println(flowChart.isDOMValid(new DOM(DOM.getDocumentForCreatingElements(), DOM.getDocumentForCreatingElements()) {}));
        System.out.println(flowChart.getWarnings());
        */
    }
}
