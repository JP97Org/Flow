package org.jojo.flow.test.model.flowChart;

import java.util.List;
import java.util.stream.Collectors;

import org.jojo.flow.exc.FlowException;
import org.jojo.flow.model.api.IDataSignature;
import org.jojo.flow.model.api.IDefaultArrow;
import org.jojo.flow.model.api.IDefaultPin;
import org.jojo.flow.model.api.IOutputPin;
import org.jojo.flow.model.api.IRigidConnection;
import org.jojo.flow.model.api.IRigidPin;
import org.jojo.flow.model.api.UnitSignature;
import org.jojo.flow.model.data.MultiMatrix;
import org.jojo.flow.model.data.StringDataSet;
import org.jojo.flow.model.flowChart.connections.DefaultArrowGR;
import org.jojo.flow.model.flowChart.connections.RigidConnectionGR;
import org.jojo.flow.model.util.DynamicObjectLoader;
import org.jojo.flow.model.util.DynamicObjectLoader.MockModule;
import org.junit.jupiter.api.*;

public class ConnectionTest {
    private MockModule mock;
    private IDefaultArrow arrow;
    private IRigidConnection rig;
    
    @BeforeEach
    public void setUp() {
        this.mock = (MockModule)DynamicObjectLoader.loadModule(
                DynamicObjectLoader.MockModule.class.getName(), 100);
        final var from = this.mock.getDefaultOutputs().get(0);
        final var to = this.mock.getDefaultInputs().get(0);
        this.arrow = DynamicObjectLoader.loadConnection(200, from, to, "Arrow");
        final List<IRigidPin> rigPins = this.mock.getAllModulePins()
                .stream()
                .filter(p -> p.getModulePinImp() instanceof IRigidPin)
                .filter(p -> p instanceof IOutputPin)
                .map(p -> (IRigidPin)p.getModulePinImp())
                .collect(Collectors.toList());
        this.rig = DynamicObjectLoader.loadConnection(300, rigPins.get(0), rigPins.get(1), "Rig");
    }
    
    @Test
    public void get() {
        Assertions.assertTrue(this.arrow.getName() != null);
        Assertions.assertTrue(this.rig.getName() != null);
        Assertions.assertTrue(this.arrow.getInfo() != null);
        Assertions.assertTrue(this.rig.getInfo() != null);
        Assertions.assertTrue(this.arrow.getFromPin() != null);
        Assertions.assertTrue(this.rig.getFromPin() != null);
        Assertions.assertTrue(this.arrow.getToPins() != null && !this.arrow.getToPins().isEmpty());
        Assertions.assertTrue(this.rig.getToPins() != null && !this.rig.getToPins().isEmpty());
        Assertions.assertTrue(this.arrow.getConnectedModules() != null 
                && this.arrow.getConnectedModules().size() == 1
                && this.arrow.getConnectedModules().iterator().next() == this.mock);
        Assertions.assertTrue(this.rig.getConnectedModules() != null 
                && this.rig.getConnectedModules().size() == 1
                && this.rig.getConnectedModules().iterator().next() == this.mock);
        Assertions.assertTrue(this.arrow.getGraphicalRepresentation() instanceof DefaultArrowGR);
        Assertions.assertTrue(this.rig.getGraphicalRepresentation() instanceof RigidConnectionGR);
        
        Assertions.assertTrue(this.arrow.getDataSignature() != null);
        final IDataSignature before = this.arrow.getDataSignature().getCopy();
        final var pin = this.mock.getAllOutputs().get(0);
        Assertions.assertTrue(this.arrow.putData(pin.getDefaultData()));
        Assertions.assertTrue(this.arrow.getData() != null);
        Assertions.assertTrue(this.arrow.putData(null));
        Assertions.assertTrue(this.arrow.getData() == null);
        final IDataSignature inactive = new StringDataSet("").getDataSignature().deactivateChecking();
        Assertions.assertFalse(this.arrow.putDataSignature(inactive));
        Assertions.assertFalse(this.arrow.getDataSignature().equals(inactive));
        this.arrow.forcePutDataSignature(inactive);
        Assertions.assertTrue(this.arrow.getDataSignature().equals(inactive));
        Assertions.assertTrue(this.arrow.putDataSignature(before));
        
        Assertions.assertTrue(this.arrow.isDOMValid(this.arrow.getDOM()));
        Assertions.assertTrue(this.rig.isDOMValid(this.rig.getDOM()));
    }
    
    @Test
    public void succeedConnecting() {
        Assertions.assertTrue(this.arrow.connect());
        Assertions.assertTrue(this.rig.connect());
        Assertions.assertTrue(this.arrow.reconnect());
        Assertions.assertTrue(this.rig.reconnect());
        this.arrow.disconnect();
        this.rig.disconnect();
        Assertions.assertTrue(this.arrow.connect());
        Assertions.assertTrue(this.rig.connect());
    }
    
    @Test
    public void failConnecting() throws FlowException {
        Assertions.assertTrue(this.arrow.connect());
        Assertions.assertTrue(this.rig.connect());
        Assertions.assertFalse(this.arrow.connect());
        Assertions.assertFalse(this.rig.connect());
        this.arrow.disconnect();
        this.rig.disconnect();
        
        final IDataSignature before = this.arrow.getDataSignature().getCopy();
        final IDataSignature sign = new MultiMatrix<Integer>(new int[] {1}, UnitSignature.NO_UNIT).getDataSignature();
        final IDataSignature inactive = sign.getCopy().deactivateChecking();
        final var pin = this.mock.getAllOutputs().get(0);
        ((IDefaultPin)pin.getModulePinImp()).setCheckDataSignature(inactive);
        ((IDefaultPin)pin.getModulePinImp()).setCheckDataSignature(sign);
        Assertions.assertFalse(this.arrow.connect());
        Assertions.assertFalse(this.arrow.reconnect());
        ((IDefaultPin)pin.getModulePinImp()).setCheckDataSignature(inactive);
        ((IDefaultPin)pin.getModulePinImp()).setCheckDataSignature(before);
        Assertions.assertTrue(this.arrow.connect());
        Assertions.assertTrue(this.arrow.reconnect());
        Assertions.assertFalse(this.arrow.connect());
        ((IDefaultPin)pin.getModulePinImp()).setCheckDataSignature(inactive);
        ((IDefaultPin)pin.getModulePinImp()).setCheckDataSignature(sign);
        Assertions.assertFalse(this.arrow.reconnect());
        
        ((IDefaultPin)pin.getModulePinImp()).setCheckDataSignature(inactive);
        ((IDefaultPin)pin.getModulePinImp()).setCheckDataSignature(before);
    }
}
