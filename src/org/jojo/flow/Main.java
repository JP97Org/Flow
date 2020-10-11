package org.jojo.flow;

import java.io.File;

import org.jojo.flow.model.ModelFacade;
import org.jojo.flow.model.data.Data;
import org.jojo.flow.model.flowChart.FlowChart;
import org.jojo.flow.model.flowChart.FlowChartElement;
import org.jojo.flow.model.flowChart.FlowChartGR;
import org.jojo.flow.model.flowChart.connections.Connection;
import org.jojo.flow.model.flowChart.connections.ConnectionException;
import org.jojo.flow.model.flowChart.modules.FlowModule;
import org.jojo.flow.model.storeLoad.DOM;
import org.jojo.flow.model.storeLoad.DynamicObjectLoader;
import org.jojo.flow.model.storeLoad.FlowDOM;
import org.jojo.flow.model.storeLoad.StoreLoadFacade;
import org.jojo.flow.model.storeLoad.DynamicObjectLoader.MockModule;
import org.jojo.flow.model.storeLoad.FlowChartDOM;

public class Main {
    // TODO at the moment only test main class
    public static void main(String[] args) {
        FlowChart flowChart = new FlowChart(0, new FlowChartGR());
        ModelFacade.flowChart = flowChart;
        final MockModule mod = (MockModule)DynamicObjectLoader.loadModule(DynamicObjectLoader.MockModule.class.getName());
        System.out.println(FlowChartElement.GENERIC_ERROR_ELEMENT.getWarnings());
        mod.setId(100);
        flowChart.addModule(mod);
        final Connection con = DynamicObjectLoader.loadConnection("");
        con.removeToPin(0);
        try {
            con.addToPin(mod.getAllInputs().get(0));
        } catch (ConnectionException e) {
            e.printStackTrace();
        }
        System.out.println(flowChart.addConnection(con));
        System.out.println(flowChart.connectAll());
        final String original0 = flowChart.toString();
        System.out.println(original0);
        final var dom = flowChart.getDOM();
        System.out.println(flowChart.isDOMValid(dom));
        flowChart.restoreFromDOM(dom);
        System.out.println(flowChart.getWarnings());
        System.out.println(ModelFacade.mock.getWarnings());
        System.out.println(FlowChartElement.GENERIC_ERROR_ELEMENT.getWarnings());
        final DOM flowDom = new FlowDOM(dom);
        final String originalFcStr = flowChart.toString();
        System.out.println(originalFcStr);
        new StoreLoadFacade().storeFlowChart(new File("/home/jojo/Schreibtisch/flow.xml"), flowDom);
        System.out.println(FlowChartElement.GENERIC_ERROR_ELEMENT.getWarnings());
        
        ModelFacade.flowChart = new StoreLoadFacade().loadFlowChart(new File("/home/jojo/Schreibtisch/flow.xml"));
        flowChart = ModelFacade.flowChart;
        System.out.println(flowChart.getWarnings());
        System.out.println(FlowChartElement.GENERIC_ERROR_ELEMENT.getWarnings());
        final DOM newFcDom = flowChart.getDOM();
        System.out.println(flowChart.isDOMValid(dom));
        final DOM newDom = new FlowDOM(newFcDom);
        System.out.println(newDom.getDOMMap());
        final DOM newDomOfFc = flowChart.getDOM();
        System.out.println(flowChart);
        System.out.println(flowChart.toString().equals(original0));
        System.out.println(original0.equals(originalFcStr));
        System.out.print(dom.equals(newDomOfFc));
    }
}
