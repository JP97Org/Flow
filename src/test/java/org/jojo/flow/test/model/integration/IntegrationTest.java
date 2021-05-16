package org.jojo.flow.test.model.integration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.jojo.flow.exc.ConnectionException;
import org.jojo.flow.exc.FlowException;
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
import org.junit.*;

public class IntegrationTest {
    // SETTINGS
    // //////////////////////////////////////////////////////////////////////////////

    private static final File XML_SERIAL_LOCATION = new File(
            "/home/jojo/Dokumente/NewMainWorkspace/xmlSerial/target/xmlSerial.jar");
    private static final File QFPM_LOCATION = new File("/home/jojo/Dokumente/NewMainWorkspace/qfpm/target/qfpm.jar");

    private static final String DIRECTORY_OF_FLOW_DOCUMENTS = "/home/jojo/Schreibtisch/";

    //////////////////////////////////////////////////////////////////////////////////////////

    @Before
    public void setUp() {
        // Setting settings
        IAPI.initialize();
        ISettings settings = ISettings.getDefaultImplementation();
        // settings.setLocationTmpDir(new File("/home/jojo/tmp/flow")); //TODO not used
        // at the moment
        // TODO toggle next line comment for xml/serial write of data on arrows
        settings.setLocationXMLSerialTransformerJar(XML_SERIAL_LOCATION);
    }

    @Test
    public void simpleTests() throws InterruptedException, FlowException, ClassNotFoundException, IOException {
        IFlowChart flowChart = new FlowChart(0, new FlowChartGR());
        new ModelFacade().setMainFlowChart(flowChart);
        final MockModule mod = (MockModule) DynamicObjectLoader
                .loadModule(DynamicObjectLoader.MockModule.class.getName(), 100);
        System.out.println(FlowChartElement.GENERIC_ERROR_ELEMENT.getWarnings());
        Assert.assertEquals("[]", FlowChartElement.GENERIC_ERROR_ELEMENT.getWarnings().toString());
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
            rigidCon.setFromPin(mod.getAllOutputs().stream().filter(p -> p instanceof OutputPin)
                    .filter(p -> ((ModulePinGR) p.getGraphicalRepresentation()).getLinePoint()
                            .equals(DynamicObjectLoader.RIGID_ONE_POS()))
                    .findFirst().orElse(null));
        } catch (ConnectionException e1) {
            e1.printStackTrace();
        }
        rigidCon.removeToPin(rigidCon.getToPins().get(0));
        try {
            con.setFromPin(mod.getAllOutputs().get(0));
            ((DefaultPin) (mod.getAllInputs().get(0).getModulePinImp())).getCheckDataSignature()
                    .getComponent(BasicSignatureComponents.SIZES.index).deactivateChecking();
            con.addToPin(mod.getAllInputs().get(0));
            rigidCon.addToPin(mod.getAllInputs().stream().filter(p -> p instanceof InputPin)
                    .filter(p -> ((ModulePinGR) p.getGraphicalRepresentation()).getLinePoint()
                            .equals(DynamicObjectLoader.RIGID_TWO_POS()))
                    .findFirst().orElse(null));
        } catch (ConnectionException e) {
            e.printStackTrace();
        }
        Assert.assertTrue(flowChart.addConnection(con));
        Assert.assertTrue(flowChart.addConnection(rigidCon));
        final String original0 = flowChart.toString();
        System.out.println(original0);
        Assert.assertEquals("ID= 0 | " + "modules= [ID= 100 | ModuleName= NAME, priority= 0 | " + "allPins= "
                + "[InputPin with GR= DefaultInputPinGR with position= java.awt.Point[x=50,y=50] and linePoint= java.awt.Point[x=0,y=0] | "
                + "IMP= DefaultPin with checkDataSignature= org.jojo.flow.model.data.StringDataSet | [not checking | [0], null, null] | defaultData= , "
                + "InputPin with GR= RigidPinGR with position= java.awt.Point[x=10,y=10] and linePoint= java.awt.Point[x=10,y=10] | "
                + "IMP= RigidPin with defaultData= null, "
                + "InputPin with GR= RigidPinGR with position= java.awt.Point[x=20,y=10] and linePoint= java.awt.Point[x=20,y=10] | "
                + "IMP= RigidPin with defaultData= null, "
                + "OutputPin with GR= DefaultOutputPinGR with position= java.awt.Point[x=0,y=0] and linePoint= java.awt.Point[x=0,y=0] | "
                + "IMP= DefaultPin with checkDataSignature= org.jojo.flow.model.data.StringDataSet | [sizes | [0], null, null] | defaultData= , "
                + "OutputPin with GR= RigidPinGR with position= java.awt.Point[x=10,y=10] and linePoint= java.awt.Point[x=10,y=10] | "
                + "IMP= RigidPin with defaultData= null, "
                + "OutputPin with GR= RigidPinGR with position= java.awt.Point[x=20,y=10] and linePoint= java.awt.Point[x=20,y=10] | "
                + "IMP= RigidPin with defaultData= null] | " + "allConnectionsOfAllPins= ["
                + "[ID= 1000 | DefaultArrow from \"100\" to \"[100]\"], "
                + "[ID= 2000 | RigidConnection from \"100\" to \"[100]\"], "
                + "[ID= 2000 | RigidConnection from \"100\" to \"[100]\"], "
                + "[ID= 1000 | DefaultArrow from \"100\" to \"[100]\"], "
                + "[ID= 2000 | RigidConnection from \"100\" to \"[100]\"], "
                + "[ID= 2000 | RigidConnection from \"100\" to \"[100]\"]]] | " + "connections= "
                + "[ID= 1000 | DefaultArrow from \"100\" to \"[100]\", "
                + "ID= 2000 | RigidConnection from \"100\" to \"[100]\"]" + "", original0);
        final var dom = flowChart.getDOM();
        Assert.assertTrue(flowChart.isDOMValid(dom));
        flowChart.restoreFromDOM(dom);
        System.out.println(flowChart.getWarnings());
        Assert.assertTrue(flowChart.getWarnings().isEmpty());
        System.out.println(flowChart.getModules().get(0).getWarnings());
        Assert.assertTrue(flowChart.getModules().get(0).getWarnings().isEmpty());
        System.out.println(FlowChartElement.GENERIC_ERROR_ELEMENT.getWarnings());
        Assert.assertTrue(FlowChartElement.GENERIC_ERROR_ELEMENT.getWarnings().isEmpty());
        final IDOM flowDom = new FlowDOM(dom);
        final String originalFcStr = flowChart.toString();
        // System.out.println(originalFcStr);
        new StoreLoadFacade().storeFlowChart(new File(DIRECTORY_OF_FLOW_DOCUMENTS + "flow.xml"), flowDom);
        System.out.println(FlowChartElement.GENERIC_ERROR_ELEMENT.getWarnings());
        Assert.assertTrue(FlowChartElement.GENERIC_ERROR_ELEMENT.getWarnings().isEmpty());

        new ModelFacade().setMainFlowChart(
                new StoreLoadFacade().loadFlowChart(new File(DIRECTORY_OF_FLOW_DOCUMENTS + "flow.xml")));
        flowChart = new ModelFacade().getMainFlowChart();
        System.out.println(FlowChartElement.GENERIC_ERROR_ELEMENT.getWarnings());
        Assert.assertTrue(FlowChartElement.GENERIC_ERROR_ELEMENT.getWarnings().isEmpty());
        System.out.println(flowChart.getWarnings());
        Assert.assertTrue(flowChart.getWarnings().isEmpty());
        final IDOM newFcDom = flowChart.getDOM();
        Assert.assertTrue(flowChart.isDOMValid(dom));
        final IDOM newDom = new FlowDOM(newFcDom);
        System.out.println(newDom.getDOMMap());
        System.out.println(flowChart);
        System.out.println(original0);
        System.out.println(originalFcStr);
        Assert.assertTrue(flowChart.toString().equals(original0));
        Assert.assertTrue(original0.equals(originalFcStr));

        final IDefaultArrow arrow = flowChart.validate();
        System.out.println(arrow);
        Assert.assertNull(arrow);

        // Simulation Test
        final Time<Double> timeout = Time.getDoubleConstant(1);
        Simulation sim = new Simulation(flowChart, new SimulationConfiguration(timeout));
        sim.start();
        Assert.assertTrue(sim.isRunning());
        Thread.sleep(1000);
        sim.stop();
        for (int i = 0; sim.isRunning(); i++) {
            Thread.sleep(10);
            if (i == 500) {
                System.err.println("TIMEOUT of 5000ms exceeded by waiting for simulation to stop!");
                Assert.assertFalse(true);
                break;
            }
        }
        System.out.println(FlowChartElement.GENERIC_ERROR_ELEMENT.getWarnings());
        Assert.assertTrue(FlowChartElement.GENERIC_ERROR_ELEMENT.getWarnings().isEmpty());
        System.out.println(flowChart.getWarnings());
        Assert.assertTrue(flowChart.getWarnings().isEmpty());
        Assert.assertFalse(sim.isRunning());

        // Class Loader Test
        final IModuleClassesList list = new StoreLoadFacade()
                .getNewModuleClassesList(QFPM_LOCATION)
                .loadAll();
        final List<Class<? extends IFlowModule>> moduleClasses = list.getModuleClassesList();
        System.out.println(moduleClasses);
        Assert.assertEquals("[class org.jojo.qfpm.QuadraticFourPinModule]", moduleClasses.toString());
        final IFlowModule loaded = DynamicObjectLoader.loadModule(list.getClassLoader().getClassLoader(),
                moduleClasses.get(0).getName(), 400);
        System.out.println(FlowChartElement.GENERIC_ERROR_ELEMENT.getWarnings());
        Assert.assertTrue(FlowChartElement.GENERIC_ERROR_ELEMENT.getWarnings().isEmpty());
        flowChart = new FlowChart(0, new FlowChartGR());
        new ModelFacade().setMainFlowChart(flowChart);
        flowChart.addModule(loaded);
        System.out.println(flowChart);
        Assert.assertEquals("ID= 0 | modules= [ID= 400 | ModuleName= NAME, priority= 0 | "
                + "allPins= "
                + "[InputPin with GR= DefaultInputPinGR with position= java.awt.Point[x=-10,y=100] and linePoint= java.awt.Point[x=-10,y=100] | "
                + "IMP= DefaultPin with checkDataSignature= org.jojo.flow.model.data.StringDataSet | [sizes | [0], null, null] | defaultData= , "
                + "InputPin with GR= RigidPinGR with position= java.awt.Point[x=100,y=-10] and linePoint= java.awt.Point[x=100,y=-10] | "
                + "IMP= RigidPin with defaultData= null, OutputPin with GR= RigidPinGR with position= java.awt.Point[x=100,y=-10] and linePoint= java.awt.Point[x=100,y=-10] | "
                + "IMP= RigidPin with defaultData= null, "
                + "InputPin with GR= RigidPinGR with position= java.awt.Point[x=100,y=200] and linePoint= java.awt.Point[x=100,y=200] | "
                + "IMP= RigidPin with defaultData= null, OutputPin with GR= DefaultOutputPinGR with position= java.awt.Point[x=200,y=100] and linePoint= java.awt.Point[x=200,y=100] | "
                + "IMP= DefaultPin with checkDataSignature= org.jojo.flow.model.data.StringDataSet | [sizes | [0], null, null] | defaultData= , "
                + "OutputPin with GR= RigidPinGR with position= java.awt.Point[x=100,y=200] and linePoint= java.awt.Point[x=100,y=200] | "
                + "IMP= RigidPin with defaultData= null] | "
                + "allConnectionsOfAllPins= [[], [], [], [], [], []]] | "
                + "connections= []"
                + "", flowChart.toString());
        IDOM.resetDocument();
        final IDOM flowDom2 = new FlowDOM(flowChart.getDOM());
        new StoreLoadFacade().storeFlowChart(new File(DIRECTORY_OF_FLOW_DOCUMENTS + "flow2.xml"), flowDom2);
        
        // API test
        IDataSignature sign = IDataSignature.getDefaultImplementation();
        System.out.println(sign);
        Assert.assertEquals("not checking | ", sign.toString());
        IFraction frac = IFraction.getDefaultImplementation(3L, 5L);
        System.out.println(frac);
        Assert.assertEquals("3 / 5", frac.toString());
        final IData[] arr = new IData[] { new StringDataSet("A") };
        final IDataArray dataArray = IDataArray.getDefaultImplementation(arr, arr[0].getDataSignature());
        System.out.println(dataArray);
        Assert.assertEquals("[A]", dataArray.toString());
        final IDataBundle dataBundle = IDataBundle.getDefaultImplementation(Arrays.asList(arr));
        System.out.println(dataBundle);
        Assert.assertEquals("[A]", dataBundle.toString());
        final IMathMatrix<Integer> mm = IMathMatrix.getDefaultImplementation(new Integer[][] { { 1 } },
                UnitSignature.NO_UNIT);
        System.out.println(mm);
        Assert.assertEquals("[[1]] ", mm.toString());
        final IMatrix<Integer> m = IMatrix.getDefaultImplementation(new Integer[][] { { 1 } }, UnitSignature.NO_UNIT);
        Assert.assertEquals("[[1]] ", m.toString());
        System.out.println(m);

        // Warning Log empty?
        System.out.println(Warning.getWarningLog());
        Assert.assertTrue(Warning.getWarningLog().isEmpty());
    }
}
