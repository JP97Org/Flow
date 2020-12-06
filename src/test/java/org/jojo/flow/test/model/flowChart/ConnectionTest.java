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
import org.junit.*;

public class ConnectionTest {
    private MockModule mock;
    private IDefaultArrow arrow;
    private IRigidConnection rig;
    
    @Before
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
        Assert.assertTrue(this.arrow.getName() != null);
        Assert.assertTrue(this.rig.getName() != null);
        Assert.assertTrue(this.arrow.getInfo() != null);
        Assert.assertTrue(this.rig.getInfo() != null);
        Assert.assertTrue(this.arrow.getFromPin() != null);
        Assert.assertTrue(this.rig.getFromPin() != null);
        Assert.assertTrue(this.arrow.getToPins() != null && !this.arrow.getToPins().isEmpty());
        Assert.assertTrue(this.rig.getToPins() != null && !this.rig.getToPins().isEmpty());
        Assert.assertTrue(this.arrow.getConnectedModules() != null 
                && this.arrow.getConnectedModules().size() == 1
                && this.arrow.getConnectedModules().iterator().next() == this.mock);
        Assert.assertTrue(this.rig.getConnectedModules() != null 
                && this.rig.getConnectedModules().size() == 1
                && this.rig.getConnectedModules().iterator().next() == this.mock);
        Assert.assertTrue(this.arrow.getGraphicalRepresentation() instanceof DefaultArrowGR);
        Assert.assertTrue(this.rig.getGraphicalRepresentation() instanceof RigidConnectionGR);
        
        Assert.assertTrue(this.arrow.getDataSignature() != null);
        final IDataSignature before = this.arrow.getDataSignature().getCopy();
        final var pin = this.mock.getAllOutputs().get(0);
        Assert.assertTrue(this.arrow.putData(pin.getDefaultData()));
        Assert.assertTrue(this.arrow.getData() != null);
        Assert.assertTrue(this.arrow.putData(null));
        Assert.assertTrue(this.arrow.getData() == null);
        final IDataSignature inactive = new StringDataSet("").getDataSignature().deactivateChecking();
        Assert.assertFalse(this.arrow.putDataSignature(inactive));
        Assert.assertFalse(this.arrow.getDataSignature().equals(inactive));
        this.arrow.forcePutDataSignature(inactive);
        Assert.assertTrue(this.arrow.getDataSignature().equals(inactive));
        Assert.assertTrue(this.arrow.putDataSignature(before));
        
        Assert.assertTrue(this.arrow.isDOMValid(this.arrow.getDOM()));
        Assert.assertTrue(this.rig.isDOMValid(this.rig.getDOM()));
    }
    
    @Test
    public void succeedConnecting() {
        Assert.assertTrue(this.arrow.connect());
        Assert.assertTrue(this.rig.connect());
        Assert.assertTrue(this.arrow.reconnect());
        Assert.assertTrue(this.rig.reconnect());
        this.arrow.disconnect();
        this.rig.disconnect();
        Assert.assertTrue(this.arrow.connect());
        Assert.assertTrue(this.rig.connect());
    }
    
    @Test
    public void failConnecting() throws FlowException {
        Assert.assertTrue(this.arrow.connect());
        Assert.assertTrue(this.rig.connect());
        Assert.assertFalse(this.arrow.connect());
        Assert.assertFalse(this.rig.connect());
        this.arrow.disconnect();
        this.rig.disconnect();
        
        final IDataSignature before = this.arrow.getDataSignature().getCopy();
        final IDataSignature sign = new MultiMatrix<Integer>(new int[] {1}, UnitSignature.NO_UNIT).getDataSignature();
        final IDataSignature inactive = sign.getCopy().deactivateChecking();
        final var pin = this.mock.getAllOutputs().get(0);
        ((IDefaultPin)pin.getModulePinImp()).setCheckDataSignature(inactive);
        ((IDefaultPin)pin.getModulePinImp()).setCheckDataSignature(sign);
        Assert.assertFalse(this.arrow.connect());
        Assert.assertFalse(this.arrow.reconnect());
        ((IDefaultPin)pin.getModulePinImp()).setCheckDataSignature(inactive);
        ((IDefaultPin)pin.getModulePinImp()).setCheckDataSignature(before);
        Assert.assertTrue(this.arrow.connect());
        Assert.assertTrue(this.arrow.reconnect());
        Assert.assertFalse(this.arrow.connect());
        ((IDefaultPin)pin.getModulePinImp()).setCheckDataSignature(inactive);
        ((IDefaultPin)pin.getModulePinImp()).setCheckDataSignature(sign);
        Assert.assertFalse(this.arrow.reconnect());
        
        ((IDefaultPin)pin.getModulePinImp()).setCheckDataSignature(inactive);
        ((IDefaultPin)pin.getModulePinImp()).setCheckDataSignature(before);
    }
}
