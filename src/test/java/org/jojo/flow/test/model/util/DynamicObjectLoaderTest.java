package org.jojo.flow.test.model.util;

import org.jojo.flow.model.api.IConnection;
import org.jojo.flow.model.api.IConnectionLineGR;
import org.jojo.flow.model.api.IDefaultArrow;
import org.jojo.flow.model.api.IDefaultArrowGR;
import org.jojo.flow.model.api.IDefaultInputPinGR;
import org.jojo.flow.model.api.IDefaultOutputPinGR;
import org.jojo.flow.model.api.IFlowChart;
import org.jojo.flow.model.api.IFlowChartElement;
import org.jojo.flow.model.api.IFlowChartGR;
import org.jojo.flow.model.api.IFlowModule;
import org.jojo.flow.model.api.IGraphicalRepresentation;
import org.jojo.flow.model.api.IInputPin;
import org.jojo.flow.model.api.IModuleGR;
import org.jojo.flow.model.api.IOneConnectionGR;
import org.jojo.flow.model.api.IOutputPin;
import org.jojo.flow.model.api.IRigidConnection;
import org.jojo.flow.model.api.IRigidConnectionGR;
import org.jojo.flow.model.api.IRigidPinGR;
import org.jojo.flow.model.flowChart.connections.DefaultArrow;
import org.jojo.flow.model.flowChart.connections.RigidConnection;
import org.jojo.flow.model.flowChart.modules.DefaultPin;
import org.jojo.flow.model.flowChart.modules.InputPin;
import org.jojo.flow.model.flowChart.modules.OutputPin;
import org.jojo.flow.model.util.DynamicObjectLoader.MockModule;
import org.jojo.flow.test.model.flowChart.GRTest;
import org.junit.*;
import static org.jojo.flow.model.util.DynamicObjectLoader.*;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

public class DynamicObjectLoaderTest {
    private Object loaded;
    
    @Before
    public void setUp() {
        this.loaded = null;
    }
    
    @Test
    public void loadAnObjectTest() throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        this.loaded = load(getClass().getClassLoader(), String.class.getName(), new Class<?>[] {
            String.class}, "Test");
        Assert.assertTrue(this.loaded instanceof String);
        Assert.assertEquals("Test", this.loaded);
    }
    
    @Test
    public void loadTest() {
        this.loaded = loadEmptyFlowChart(100);
        Assert.assertTrue(this.loaded instanceof IFlowChart);
        checkId();
        
        this.loaded = loadConnection(DefaultArrow.class.getName());
        Assert.assertTrue(this.loaded instanceof DefaultArrow);
        this.loaded = loadConnection(RigidConnection.class.getName());
        Assert.assertTrue(this.loaded instanceof RigidConnection);
        
        final var from = (IOutputPin) loadPin(OutputPin.class.getName(), DefaultPin.class.getName());
        final var to = (IInputPin) loadPin(InputPin.class.getName(), DefaultPin.class.getName());
        this.loaded = loadConnection(100, from, to, "Connection");
        Assert.assertTrue(this.loaded instanceof IDefaultArrow);
        checkId();
        Assert.assertTrue(((IConnection)this.loaded).getName().equals("Connection"));
        final var asFrom = loadRigidPin(from.getModule());
        final var asTo = loadRigidPin(to.getModule());
        this.loaded = loadConnection(100, asFrom, asTo, "Connection");
        Assert.assertTrue(this.loaded instanceof IRigidConnection);
        checkId();
        Assert.assertTrue(((IConnection)this.loaded).getName().equals("Connection"));
        
        final List<IGraphicalRepresentation> grs = Arrays.asList(GRTest.getAllGRs());
        Assert.assertTrue(grs.size() == 10);
        Assert.assertTrue(grs.stream().anyMatch(g -> g instanceof IFlowChartGR));
        Assert.assertTrue(grs.stream().anyMatch(g -> g instanceof IConnectionLineGR));
        Assert.assertTrue(grs.stream().anyMatch(g -> g instanceof IDefaultArrowGR));
        Assert.assertTrue(grs.stream().anyMatch(g -> g instanceof IOneConnectionGR));
        Assert.assertTrue(grs.stream().filter(g -> g instanceof IOneConnectionGR)
                .map(g -> (IOneConnectionGR)g)
                .filter(g -> g.getFromPin() instanceof IDefaultOutputPinGR)
                .filter(g -> g.getToPin() instanceof IDefaultInputPinGR)
                .count() == 1);
        Assert.assertTrue(grs.stream().filter(g -> g instanceof IOneConnectionGR)
                .map(g -> (IOneConnectionGR)g)
                .filter(g -> g.getFromPin() instanceof IRigidPinGR)
                .filter(g -> g.getToPin() instanceof IRigidPinGR)
                .count() == 1);
        Assert.assertTrue(grs.stream().anyMatch(g -> g instanceof IRigidConnectionGR));
        Assert.assertTrue(grs.stream().anyMatch(g -> g instanceof IDefaultInputPinGR));
        Assert.assertTrue(grs.stream().anyMatch(g -> g instanceof IDefaultOutputPinGR));
        Assert.assertTrue(grs.stream().anyMatch(g -> g instanceof IRigidPinGR));
        Assert.assertTrue(grs.stream().anyMatch(g -> g instanceof IModuleGR));
        
        this.loaded = loadModule(MockModule.class.getName(), 100);
        Assert.assertTrue(this.loaded instanceof MockModule);
        checkId();
        this.loaded = null;
        this.loaded = loadModule(getClass().getClassLoader(), MockModule.class.getName(), 
                100, "Mock", 50);
        Assert.assertTrue(this.loaded instanceof MockModule);
        checkId();
        final IFlowModule mod = (IFlowModule)this.loaded;
        Assert.assertEquals("Mock", mod.getExternalConfig().getName());
        Assert.assertEquals(50, mod.getExternalConfig().getPriority());
        
        Assert.assertTrue(getNewMockExternalConfig() != null);
    }
    
    private void checkId() {
        Assert.assertTrue(getFCE().getId() == 100);
    }
    
    private IFlowChartElement getFCE() {
        return (IFlowChartElement)loaded;
    }
}
