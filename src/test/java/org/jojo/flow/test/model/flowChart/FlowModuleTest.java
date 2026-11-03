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
import org.junit.jupiter.api.*;

public class FlowModuleTest {
    private IFlowModule module;
    
    @BeforeEach
    public void setUp() {
        this.module = DynamicObjectLoader.loadModule(
                DynamicObjectLoader.MockModule.class.getName(), 100);
    }
    
    @Test
    public void get() {
        Assertions.assertTrue(this.module.getAllModulePins() != null);
        Assertions.assertTrue(this.module.getFrequency() != null);
        Assertions.assertTrue(this.module.getFrequency().toFractionUnit().value.doubleValue() > 0.0);
        Assertions.assertTrue(this.module.getExternalConfig() != null);
        Assertions.assertTrue(this.module.getDefaultInputs() != null);
        Assertions.assertTrue(this.module.getDefaultOutputs() != null);
        Assertions.assertTrue(this.module.getAllInputs() != null);
        Assertions.assertTrue(this.module.getAllOutputs() != null);
        Assertions.assertTrue(this.module.getDefaultDependencyList() != null);
        Assertions.assertTrue(this.module.getDefaultAdjacencyList() != null);
        Assertions.assertTrue(this.module.isDOMValid(this.module.getDOM()));
    }
    
    @Test
    public void run() throws Exception {
        this.module.run();
    }
    
    @Test
    public void validate() throws FlowException {
        Assertions.assertTrue(this.module.validate() == null);
        if (!this.module.getDefaultOutputs().isEmpty() 
                && !this.module.getDefaultInputs().isEmpty()) {
            final var from = this.module.getDefaultOutputs().get(0);
            final var to = this.module.getDefaultInputs().get(0);
            final IDefaultArrow arrow = DynamicObjectLoader.loadConnection(200, from, to, "Arrow");
            Assertions.assertTrue(arrow.connect());
            Assertions.assertTrue(this.module.validate() == null);
            modWronging(this.module);
            Assertions.assertTrue(this.module.validate() == arrow);
        }
    }
    
    public static void modWronging(final IFlowModule moduleArg) throws FlowException {
        final var from = moduleArg.getDefaultOutputs().get(0);
        IDataSignature wrong = new MultiMatrix<Integer>(new int[] {1}, UnitSignature.NO_UNIT).getDataSignature();
        IDataSignature inactive = new StringDataSet("").getDataSignature().deactivateChecking();
        ((IDefaultPin)from.getModulePinImp()).setCheckDataSignature(inactive);
        ((IDefaultPin)from.getModulePinImp()).setCheckDataSignature(wrong);
    }
}
