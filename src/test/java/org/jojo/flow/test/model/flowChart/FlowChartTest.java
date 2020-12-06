package org.jojo.flow.test.model.flowChart;

import org.jojo.flow.exc.FlowException;
import org.jojo.flow.model.api.IDefaultArrow;
import org.jojo.flow.model.api.IFlowChart;
import org.jojo.flow.model.flowChart.FlowChart;
import org.jojo.flow.model.flowChart.FlowChartGR;
import org.jojo.flow.model.util.DynamicObjectLoader;
import org.jojo.flow.model.util.DynamicObjectLoader.MockModule;
import org.junit.*;

public class FlowChartTest {
    private IFlowChart fc;
    
    @Before
    public void setUp() {
        this.fc = new FlowChart(0, new FlowChartGR());
    }
    
    @Test
    public void get() {
        Assert.assertTrue(this.fc.getModules() != null);
        Assert.assertTrue(this.fc.getConnections() != null);
        Assert.assertTrue(this.fc.getArrows() != null);
        Assert.assertTrue(this.fc.getGraphicalRepresentation() != null);
        Assert.assertTrue(this.fc.isDOMValid(this.fc.getDOM()));
    }
    
    @Test
    public void set() {
        setHelper();
    }
    
    private void setHelper() {
        final MockModule mock = (MockModule) DynamicObjectLoader.loadModule(DynamicObjectLoader.MockModule.class.getName(), 100);
        this.fc.addModule(mock);
        Assert.assertTrue(this.fc.getModules().size() == 1);
        Assert.assertTrue(this.fc.getModules().get(0) == mock);
        final var from = mock.getDefaultOutputs().get(0);
        final var to = mock.getDefaultInputs().get(0);
        final IDefaultArrow arrow = DynamicObjectLoader.loadConnection(200, from, to, "Arrow");
        this.fc.addConnection(arrow);
        Assert.assertTrue(this.fc.getConnections().size() == 1);
        Assert.assertTrue(this.fc.getConnections().get(0) == arrow);
        Assert.assertTrue(this.fc.getArrows().size() == 1);
        Assert.assertTrue(this.fc.getArrows().get(0) == arrow);
    }
    
    @Test
    public void validate() throws FlowException {
        Assert.assertTrue(this.fc.validate() == null);
        setHelper();
        Assert.assertTrue(this.fc.validate() == null);
        FlowModuleTest.modWronging(this.fc.getModules().get(0));
        Assert.assertTrue(this.fc.validate() == this.fc.getArrows().get(0));
    }
}
