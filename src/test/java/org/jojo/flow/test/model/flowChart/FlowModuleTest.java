package org.jojo.flow.test.model.flowChart;


import org.jojo.flow.exc.FlowException;
import org.jojo.flow.model.api.IDataSignature;
import org.jojo.flow.model.api.IDefaultArrow;
import org.jojo.flow.model.api.IDefaultPin;
import org.jojo.flow.model.api.IFlowModule;
import org.jojo.flow.model.api.UnitSignature;
import org.jojo.flow.model.data.MultiMatrix;
import org.jojo.flow.model.data.StringDataSet;
import org.jojo.flow.model.util.DynamicObjectLoader;
import org.junit.*;

public class FlowModuleTest {
    private IFlowModule module;
    
    @Before
    public void setUp() {
        this.module = DynamicObjectLoader.loadModule(
                DynamicObjectLoader.MockModule.class.getName(), 100);
    }
    
    @Test
    public void get() {
        Assert.assertTrue(this.module.getAllModulePins() != null);
        Assert.assertTrue(this.module.getFrequency() != null);
        Assert.assertTrue(this.module.getFrequency().toFractionUnit().value.doubleValue() > 0.0);
        Assert.assertTrue(this.module.getExternalConfig() != null);
        Assert.assertTrue(this.module.getDefaultInputs() != null);
        Assert.assertTrue(this.module.getDefaultOutputs() != null);
        Assert.assertTrue(this.module.getAllInputs() != null);
        Assert.assertTrue(this.module.getAllOutputs() != null);
        Assert.assertTrue(this.module.getDefaultDependencyList() != null);
        Assert.assertTrue(this.module.getDefaultAdjacencyList() != null);
        Assert.assertTrue(this.module.isDOMValid(this.module.getDOM()));
    }
    
    @Test
    public void run() throws Exception {
        this.module.run();
    }
    
    @Test
    public void validate() throws FlowException {
        Assert.assertTrue(this.module.validate() == null);
        if (!this.module.getDefaultOutputs().isEmpty() 
                && !this.module.getDefaultInputs().isEmpty()) {
            final var from = this.module.getDefaultOutputs().get(0);
            final var to = this.module.getDefaultInputs().get(0);
            final IDefaultArrow arrow = DynamicObjectLoader.loadConnection(200, from, to, "Arrow");
            Assert.assertTrue(arrow.connect());
            Assert.assertTrue(this.module.validate() == null);
            IDataSignature wrong = new MultiMatrix<Integer>(new int[] {1}, UnitSignature.NO_UNIT).getDataSignature();
            IDataSignature inactive = new StringDataSet("").getDataSignature().deactivateChecking();
            ((IDefaultPin)from.getModulePinImp()).setCheckDataSignature(inactive);
            ((IDefaultPin)from.getModulePinImp()).setCheckDataSignature(wrong);
            Assert.assertTrue(this.module.validate() == arrow);
        }
    }
}
