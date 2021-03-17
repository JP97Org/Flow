package org.jojo.flow.test.model.storeLoad;

import java.awt.Point;
import java.util.Arrays;

import org.jojo.flow.exc.FlowException;
import org.jojo.flow.model.api.IDOM;
import org.jojo.flow.model.storeLoad.ConfigDOM;
import org.jojo.flow.model.storeLoad.ConnectionDOM;
import org.jojo.flow.model.storeLoad.FlowChartDOM;
import org.jojo.flow.model.storeLoad.FlowDOM;
import org.jojo.flow.model.storeLoad.GraphicalRepresentationDOM;
import org.jojo.flow.model.storeLoad.ModuleDOM;
import org.jojo.flow.model.storeLoad.ModulePinDOM;
import org.jojo.flow.model.storeLoad.PointDOM;
import org.junit.*;

public class SimpleDOMTests extends DOMTest {
    private IDOM[] domsUnderTest;
    
    @Before
    public void setUp() throws FlowException {
        this.domsUnderTest = new IDOM[]{
                ConfigDOM.getExternal(), 
                ConfigDOM.getInternal(),
                new ConnectionDOM(),
                new FlowChartDOM(),
                new FlowDOM(new FlowChartDOM()),
                new GraphicalRepresentationDOM(),
                new ModuleDOM(),
                new ModulePinDOM(),
                PointDOM.of("Test", new Point(0,0))
                };
    }

    @Override
    protected IDOM[] getDomsUnderTest() {
        return this.domsUnderTest;
    }
    
    @Test
    public void pointTest() {
        Assert.assertEquals(new Point(0, 0), PointDOM.pointOf(
                Arrays.stream(this.domsUnderTest)
                    .filter(x -> x instanceof PointDOM)
                    .findFirst().orElse(null)));
        
    }
}
