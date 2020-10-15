package org.jojo.flow;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.jojo.flow.model.ModelFacade;
import org.jojo.flow.model.data.BasicSignatureComponents;
import org.jojo.flow.model.data.Data;
import org.jojo.flow.model.flowChart.FlowChart;
import org.jojo.flow.model.flowChart.FlowChartElement;
import org.jojo.flow.model.flowChart.FlowChartGR;
import org.jojo.flow.model.flowChart.connections.Connection;
import org.jojo.flow.model.flowChart.connections.ConnectionException;
import org.jojo.flow.model.flowChart.connections.DefaultArrow;
import org.jojo.flow.model.flowChart.connections.RigidConnection;
import org.jojo.flow.model.flowChart.modules.DefaultPin;
import org.jojo.flow.model.flowChart.modules.FlowModule;
import org.jojo.flow.model.flowChart.modules.InputPin;
import org.jojo.flow.model.flowChart.modules.ModulePinGR;
import org.jojo.flow.model.flowChart.modules.OutputPin;
import org.jojo.flow.model.flowChart.modules.RigidPin;
import org.jojo.flow.model.storeLoad.DOM;
import org.jojo.flow.model.storeLoad.DynamicClassLoader;
import org.jojo.flow.model.storeLoad.DynamicObjectLoader;
import org.jojo.flow.model.storeLoad.FlowDOM;
import org.jojo.flow.model.storeLoad.ModuleClassesList;
import org.jojo.flow.model.storeLoad.StoreLoadFacade;
import org.jojo.flow.model.storeLoad.DynamicObjectLoader.MockModule;
import org.jojo.flow.model.storeLoad.FlowChartDOM;

public class Main {
    // TODO at the moment only test main class
    public static void main(String[] args) {
        FlowChart flowChart = new FlowChart(0, new FlowChartGR());
        ModelFacade.flowChart = flowChart;
        final MockModule mod = (MockModule)DynamicObjectLoader.loadModule(DynamicObjectLoader.MockModule.class.getName(), 100);
        System.out.println(FlowChartElement.GENERIC_ERROR_ELEMENT.getWarnings());
        flowChart.addModule(mod);
        final Connection con = DynamicObjectLoader.loadConnection(DefaultArrow.class.getName());
        final Connection rigidCon = DynamicObjectLoader.loadConnection(RigidConnection.class.getName());
        try {
            final var field = FlowChartElement.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(con, 1000);
            field.set(rigidCon, 2000);
            field.setAccessible(false);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e1) {
            e1.printStackTrace();
        }
        con.removeToPin(0);
        try {
            rigidCon.setFromPin(mod.getAllOutputs().stream()
                    .filter(p -> p instanceof OutputPin)
                    .filter(p -> ((ModulePinGR)p.getGraphicalRepresentation())
                            .getLinePoint().equals(DynamicObjectLoader.RIGID_ONE_POS))
                    .findFirst().orElse(null));
        } catch (ConnectionException e1) {
            e1.printStackTrace();
        }
        rigidCon.removeToPin(0);
        try {
            con.setFromPin(mod.getAllOutputs().get(0));
            ((DefaultPin)(mod.getAllInputs().get(0).getModulePinImp()))
                .getCheckDataSignature()
                .getComponent(BasicSignatureComponents.SIZES.index)
                .deactivateChecking();
            con.addToPin(mod.getAllInputs().get(0));
            rigidCon.addToPin(mod.getAllInputs().stream()
                    .filter(p -> p instanceof InputPin)
                    .filter(p -> ((ModulePinGR)p.getGraphicalRepresentation())
                            .getLinePoint().equals(DynamicObjectLoader.RIGID_TWO_POS))
                    .findFirst().orElse(null));
        } catch (ConnectionException e) {
            e.printStackTrace();
        }
        System.out.println(flowChart.addConnection(con));
        System.out.println(flowChart.addConnection(rigidCon));
        //System.out.println(flowChart.connectAll());
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
        System.out.println(original0);
        System.out.println(originalFcStr);
        System.out.println(flowChart.toString().equals(original0));
        System.out.println(original0.equals(originalFcStr));
        System.out.println(dom.equals(newDomOfFc));
        
        //Class Loader Test
        final var classLoader = new DynamicClassLoader(new File("/home/jojo/tmp/flow"));
        try {
            final List<Class<? extends FlowModule>> moduleClasses = new ModuleClassesList(classLoader, 
                    true, 
                    new File("/home/jojo/qfpm.jar")).getModuleClassesList();
            System.out.println(moduleClasses);
            final FlowModule loaded = DynamicObjectLoader.loadModule(classLoader, moduleClasses.get(0).getName(), 400);
            System.out.println(FlowChartElement.GENERIC_ERROR_ELEMENT.getWarnings());
            flowChart = new FlowChart(0, new FlowChartGR());
            ModelFacade.flowChart = flowChart;
            flowChart.addModule(loaded);
            System.out.println(flowChart);
        } catch (ClassNotFoundException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
    }
}
