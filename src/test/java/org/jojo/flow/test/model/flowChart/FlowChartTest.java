package org.jojo.flow.test.model.flowChart;

import org.jojo.flow.exc.FlowException;
import org.jojo.flow.model.api.IDefaultArrow;
import org.jojo.flow.model.api.IFlowChart;
import org.jojo.flow.model.flowChart.FlowChart;
import org.jojo.flow.model.flowChart.FlowChartGR;
import org.jojo.flow.model.util.DynamicObjectLoader;
import org.jojo.flow.model.util.DynamicObjectLoader.MockModule;
import org.junit.jupiter.api.*;

public class FlowChartTest {
    private IFlowChart fc;
    
    @BeforeEach
    public void setUp() {
        this.fc = new FlowChart(0, new FlowChartGR());
    }
    
    @Test
    public void get() {
        Assertions.assertTrue(this.fc.getModules() != null);
        Assertions.assertTrue(this.fc.getConnections() != null);
        Assertions.assertTrue(this.fc.getArrows() != null);
        Assertions.assertTrue(this.fc.getGraphicalRepresentation() != null);
        Assertions.assertTrue(this.fc.isDOMValid(this.fc.getDOM()));
    }
    
    @Test
    public void set() {
        setHelper();
    }
    
    private void setHelper() {
        final MockModule mock = (MockModule) DynamicObjectLoader.loadModule(DynamicObjectLoader.MockModule.class.getName(), 100);
        this.fc.addModule(mock);
        Assertions.assertTrue(this.fc.getModules().size() == 1);
        Assertions.assertTrue(this.fc.getModules().get(0) == mock);
        final var from = mock.getDefaultOutputs().get(0);
        final var to = mock.getDefaultInputs().get(0);
        final IDefaultArrow arrow = DynamicObjectLoader.loadConnection(200, from, to, "Arrow");
        this.fc.addConnection(arrow);
        Assertions.assertTrue(this.fc.getConnections().size() == 1);
        Assertions.assertTrue(this.fc.getConnections().get(0) == arrow);
        Assertions.assertTrue(this.fc.getArrows().size() == 1);
        Assertions.assertTrue(this.fc.getArrows().get(0) == arrow);
    }
    
    @Test
    public void validate() throws FlowException {
        Assertions.assertTrue(this.fc.validate() == null);
        setHelper();
        Assertions.assertTrue(this.fc.validate() == null);
        FlowModuleTest.modWronging(this.fc.getModules().get(0));
        Assertions.assertTrue(this.fc.validate() == this.fc.getArrows().get(0));
    }
}
