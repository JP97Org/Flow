package org.jojo.flow;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.jojo.flow.exc.ConnectionException;
import org.jojo.flow.exc.FlowException;
import org.jojo.flow.exc.ValidationException;
import org.jojo.flow.exc.Warning;
import org.jojo.flow.model.ModelFacade;
import org.jojo.flow.model.api.IAPI;
import org.jojo.flow.model.api.IConnection;
import org.jojo.flow.model.api.IDOM;
import org.jojo.flow.model.api.IData;
import org.jojo.flow.model.api.IDataArray;
import org.jojo.flow.model.api.IDataBundle;
import org.jojo.flow.model.api.IDataSignature;
import org.jojo.flow.model.api.IDefaultArrow;
import org.jojo.flow.model.api.IFlowChart;
import org.jojo.flow.model.api.IFlowModule;
import org.jojo.flow.model.api.IFraction;
import org.jojo.flow.model.api.IMathMatrix;
import org.jojo.flow.model.api.IMatrix;
import org.jojo.flow.model.api.IModuleClassesList;
import org.jojo.flow.model.api.ISettings;
import org.jojo.flow.model.api.UnitSignature;
import org.jojo.flow.model.data.BasicSignatureComponents;
import org.jojo.flow.model.data.StringDataSet;
import org.jojo.flow.model.data.units.Time;
import org.jojo.flow.model.flowChart.FlowChart;
import org.jojo.flow.model.flowChart.FlowChartElement;
import org.jojo.flow.model.flowChart.FlowChartGR;
import org.jojo.flow.model.flowChart.connections.DefaultArrow;
import org.jojo.flow.model.flowChart.connections.RigidConnection;
import org.jojo.flow.model.flowChart.modules.DefaultPin;
import org.jojo.flow.model.flowChart.modules.InputPin;
import org.jojo.flow.model.flowChart.modules.ModulePinGR;
import org.jojo.flow.model.flowChart.modules.OutputPin;
import org.jojo.flow.model.simulation.Simulation;
import org.jojo.flow.model.simulation.SimulationConfiguration;
import org.jojo.flow.model.storeLoad.FlowDOM;
import org.jojo.flow.model.storeLoad.StoreLoadFacade;
import org.jojo.flow.model.util.DynamicObjectLoader;
import org.jojo.flow.model.util.DynamicObjectLoader.MockModule;

public class Main {
    // TODO at the moment only test main class
    public static void main(String[] args) {
        final long base = System.currentTimeMillis();
        //Setting settings
        IAPI.initialize();
        ISettings settings = ISettings.getDefaultImplementation();
        //settings.setLocationTmpDir(new File("/home/jojo/tmp/flow")); //TODO not used at the moment
        //TODO toggle next line comment for xml/serial write of data on arrows
        settings.setLocationXMLSerialTransformerJar(new File("/home/jojo/Dokumente/NewMainWorkspace/xmlSerial/target/xmlSerial.jar"));
        
        IFlowChart flowChart = new FlowChart(0, new FlowChartGR());
        new ModelFacade().setMainFlowChart(flowChart);
        final MockModule mod = (MockModule)DynamicObjectLoader.loadModule(DynamicObjectLoader.MockModule.class.getName(), 100);
        System.out.println(FlowChartElement.GENERIC_ERROR_ELEMENT.getWarnings());
        flowChart.addModule(mod);
        final IConnection con = DynamicObjectLoader.loadConnection(DefaultArrow.class.getName());
        final IConnection rigidCon = DynamicObjectLoader.loadConnection(RigidConnection.class.getName());
        try {
            final var field = FlowChartElement.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(con, 1000);
            field.set(rigidCon, 2000);
            field.setAccessible(false);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e1) {
            e1.printStackTrace();
        }
        con.removeToPin(con.getToPins().get(0));
        try {
            rigidCon.setFromPin(mod.getAllOutputs().stream()
                    .filter(p -> p instanceof OutputPin)
                    .filter(p -> ((ModulePinGR)p.getGraphicalRepresentation())
                            .getLinePoint().equals(DynamicObjectLoader.RIGID_ONE_POS()))
                    .findFirst().orElse(null));
        } catch (ConnectionException e1) {
            e1.printStackTrace();
        }
        rigidCon.removeToPin(rigidCon.getToPins().get(0));
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
                            .getLinePoint().equals(DynamicObjectLoader.RIGID_TWO_POS()))
                    .findFirst().orElse(null));
        } catch (ConnectionException e) {
            e.printStackTrace();
        }
        System.out.println(flowChart.addConnection(con));
        System.out.println(flowChart.addConnection(rigidCon));
        final String original0 = flowChart.toString();
        System.out.println(original0);
        final var dom = flowChart.getDOM();
        System.out.println(flowChart.isDOMValid(dom));
        flowChart.restoreFromDOM(dom);
        System.out.println(flowChart.getWarnings());
        System.out.println(flowChart.getModules().get(0).getWarnings());
        System.out.println(FlowChartElement.GENERIC_ERROR_ELEMENT.getWarnings());
        final IDOM flowDom = new FlowDOM(dom);
        final String originalFcStr = flowChart.toString();
        System.out.println(originalFcStr);
        new StoreLoadFacade().storeFlowChart(new File("/home/jojo/Schreibtisch/flow.xml"), flowDom);
        System.out.println(FlowChartElement.GENERIC_ERROR_ELEMENT.getWarnings());
        
        new ModelFacade().setMainFlowChart(new StoreLoadFacade().loadFlowChart(new File("/home/jojo/Schreibtisch/flow.xml")));
        flowChart = new ModelFacade().getMainFlowChart();
        System.out.println(FlowChartElement.GENERIC_ERROR_ELEMENT.getWarnings());
        System.out.println(flowChart.getWarnings());
        final IDOM newFcDom = flowChart.getDOM();
        System.out.println(flowChart.isDOMValid(dom));
        final IDOM newDom = new FlowDOM(newFcDom);
        System.out.println(newDom.getDOMMap());
        System.out.println(flowChart);
        System.out.println(original0);
        System.out.println(originalFcStr);
        System.out.println(flowChart.toString().equals(original0));
        System.out.println(original0.equals(originalFcStr));
        try {
            final IDefaultArrow arrow = flowChart.validate();
            System.out.println(arrow);
            System.out.println(arrow == null);
        } catch (ValidationException e1) {
            e1.printStackTrace();
        }
        
        //Simulation Test
        final Time<Double> timeout = Time.getDoubleConstant(1);
        try {
            Simulation sim = new Simulation(flowChart, new SimulationConfiguration(timeout));
            sim.start();
            System.out.println(sim.isRunning());
            Thread.sleep(1000);
            sim.stop();
            for (int i = 0; sim.isRunning(); i++) {
                Thread.sleep(10);
                if (i == 500) {
                    System.err.println("TIMEOUT of 5000ms exceeded by waiting for simulation to stop!");
                    break;
                }
            }
            System.out.println(flowChart.getWarnings());
            System.out.println(!sim.isRunning());
        } catch (FlowException | InterruptedException e1) {
            e1.printStackTrace();
        }
        
        //Class Loader Test
        try {
            final IModuleClassesList list = new StoreLoadFacade().getNewModuleClassesList(
                        new File("/home/jojo/Dokumente/NewMainWorkspace/qfpm/target/qfpm.jar"))
                    .loadAll();
            final List<Class<? extends IFlowModule>> moduleClasses = list.getModuleClassesList();
            System.out.println(moduleClasses);
            final IFlowModule loaded = DynamicObjectLoader.loadModule(list.getClassLoader().getClassLoader(), moduleClasses.get(0).getName(), 400);
            System.out.println(FlowChartElement.GENERIC_ERROR_ELEMENT.getWarnings());
            flowChart = new FlowChart(0, new FlowChartGR());
            new ModelFacade().setMainFlowChart(flowChart);
            flowChart.addModule(loaded);
            System.out.println(flowChart);
            IDOM.resetDocument();
            final IDOM flowDom2 = new FlowDOM(flowChart.getDOM());
            new StoreLoadFacade().storeFlowChart(new File("/home/jojo/Schreibtisch/flow2.xml"), flowDom2);
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        
        //API test
        IDataSignature sign = IDataSignature.getDefaultImplementation();
        System.out.println(sign);
        IFraction frac = IFraction.getDefaultImplementation(3L, 5L);
        System.out.println(frac);
        final IData[] arr = new IData[] {new StringDataSet("A")};
        final IDataArray dataArray = IDataArray.getDefaultImplementation(arr, arr[0].getDataSignature()); 
        System.out.println(dataArray);
        final IDataBundle dataBundle = IDataBundle.getDefaultImplementation(Arrays.asList(arr)); 
        System.out.println(dataBundle);
        final IMathMatrix<Integer> mm = IMathMatrix.getDefaultImplementation(new Integer[][] {{1}}, UnitSignature.NO_UNIT);
        System.out.println(mm);
        final IMatrix<Integer> m = IMatrix.getDefaultImplementation(new Integer[][] {{1}}, UnitSignature.NO_UNIT);
        System.out.println(m);
        
        //Warning Log empty?
        System.out.println(Warning.getWarningLog());
        System.out.println(Warning.getWarningLog().isEmpty());
        
        //Time needed
        System.out.println((System.currentTimeMillis() - base) + "ms");
    }
}
