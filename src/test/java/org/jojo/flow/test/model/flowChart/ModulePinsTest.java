package org.jojo.flow.test.model.flowChart;

import org.jojo.flow.exc.FlowException;
import org.jojo.flow.model.api.IConnection;
import org.jojo.flow.model.api.IDataSignature;
import org.jojo.flow.model.api.IDefaultPin;
import org.jojo.flow.model.api.IFlowModule;
import org.jojo.flow.model.api.IInputPin;
import org.jojo.flow.model.api.IOutputPin;
import org.jojo.flow.model.api.IRigidPin;
import org.jojo.flow.model.flowChart.connections.DefaultArrow;
import org.jojo.flow.model.flowChart.connections.RigidConnection;
import org.jojo.flow.model.flowChart.modules.DefaultPin;
import org.jojo.flow.model.flowChart.modules.InputPin;
import org.jojo.flow.model.flowChart.modules.OutputPin;
import org.jojo.flow.model.util.DynamicObjectLoader;
import org.junit.jupiter.api.*;

public class ModulePinsTest {
    private IFlowModule mock;
    private IOutputPin defOut;
    private IInputPin defIn;
    private IRigidPin rigid;

    @BeforeEach
    public void setUp() {
        this.mock = DynamicObjectLoader.loadModule(DynamicObjectLoader.MockModule.class.getName(), 100);
        this.defOut = (IOutputPin) DynamicObjectLoader.loadPin(OutputPin.class.getName(), DefaultPin.class.getName(),
                mock);
        this.defIn = (IInputPin) DynamicObjectLoader.loadPin(InputPin.class.getName(), DefaultPin.class.getName(),
                mock);
        this.rigid = DynamicObjectLoader.loadRigidPin(mock);
    }

    @Test
    public void get() {
        Assertions.assertTrue(defOut.getGraphicalRepresentation() != null);
        Assertions.assertTrue(defIn.getGraphicalRepresentation() != null);
        Assertions.assertTrue(defOut.getModule() == mock);
        Assertions.assertTrue(defIn.getModule() == mock);
        final IOutputPin rigidOut = rigid.getOutputPin();
        final IInputPin rigidIn = rigid.getInputPin();
        Assertions.assertTrue(rigidOut.getModule() == mock);
        Assertions.assertTrue(rigidIn.getModule() == mock);
        Assertions.assertTrue(rigidOut != null && rigidOut == rigid.getOutputPin());
        Assertions.assertTrue(rigidIn != null && rigidIn == rigid.getInputPin());
        Assertions.assertTrue(rigidOut.getGraphicalRepresentation() != null);
        Assertions.assertTrue(rigidIn.getGraphicalRepresentation() != null);
        Assertions.assertTrue(defOut.getConnections().isEmpty());
        Assertions.assertTrue(defIn.getConnections().isEmpty());
        Assertions.assertTrue(rigidOut.getConnections().isEmpty());
        Assertions.assertTrue(rigidIn.getConnections().isEmpty());
        Assertions.assertTrue(defOut.getModulePinImp() instanceof IDefaultPin);
        Assertions.assertTrue(defIn.getModulePinImp() instanceof IDefaultPin);
        Assertions.assertTrue(((IDefaultPin) defOut.getModulePinImp()).getCheckDataSignature() != null);
        Assertions.assertTrue(((IDefaultPin) defIn.getModulePinImp()).getCheckDataSignature() != null);
        Assertions.assertTrue(rigidOut.getModulePinImp() == rigid);
        Assertions.assertTrue(rigidIn.getModulePinImp() == rigid);
        Assertions.assertTrue(defOut.isDOMValid(defOut.getDOM()));
        Assertions.assertTrue(defIn.isDOMValid(defIn.getDOM()));
        Assertions.assertTrue(rigidOut.isDOMValid(rigidOut.getDOM()));
        Assertions.assertTrue(rigidIn.isDOMValid(rigidIn.getDOM()));
        Assertions.assertTrue(defIn.getData() == defIn.getDefaultData());
    }

    @Test
    public void set() throws FlowException {
        final IConnection def = new DefaultArrow(200, defOut, defIn, "def");
        final IConnection rig = new RigidConnection(300, rigid, rigid, "rig");
        Assertions.assertTrue(def.connect());
        Assertions.assertTrue(rig.connect());
        Assertions.assertTrue(defOut.getConnections().size() == 1);
        Assertions.assertTrue(defIn.getConnections().size() == 1);
        Assertions.assertTrue(rigid.getConnections().size() == 1);
        Assertions.assertTrue(defOut.getConnections().get(0) == defIn.getConnections().get(0));
        
        Assertions.assertTrue(defOut.putData(defOut.getDefaultData()));
        Assertions.assertTrue(defOut.putData(null));
        
        final IDataSignature outOne = ((IDefaultPin) defOut.getModulePinImp()).getCheckDataSignature().getCopy();
        outOne.deactivateChecking();
        final IDataSignature outTwo = ((IDefaultPin) defOut.getModulePinImp()).getCheckDataSignature().getCopy();
        final IDataSignature inOne = ((IDefaultPin) defOut.getModulePinImp()).getCheckDataSignature().getCopy();
        inOne.deactivateChecking();
        final IDataSignature inTwo = ((IDefaultPin) defOut.getModulePinImp()).getCheckDataSignature().getCopy();
        def.disconnect();
        Assertions.assertTrue(defOut.getConnections().isEmpty());
        Assertions.assertTrue(defIn.getConnections().isEmpty());
        ((IDefaultPin) defOut.getModulePinImp()).setCheckDataSignature(outOne);
        Assertions.assertTrue(((IDefaultPin) defOut.getModulePinImp()).getCheckDataSignature().matches(outTwo));
        Assertions.assertTrue(((IDefaultPin) defOut.getModulePinImp()).getCheckDataSignature().equals(outOne));
        ((IDefaultPin) defIn.getModulePinImp()).setCheckDataSignature(inOne);
        Assertions.assertTrue(((IDefaultPin) defIn.getModulePinImp()).getCheckDataSignature().matches(inTwo));
        Assertions.assertTrue(((IDefaultPin) defIn.getModulePinImp()).getCheckDataSignature().equals(inOne));
        Assertions.assertTrue(def.connect());
        ((IDefaultPin) defOut.getModulePinImp()).setCheckDataSignature(outTwo);
        Assertions.assertTrue(((IDefaultPin) defOut.getModulePinImp()).getCheckDataSignature().equals(outTwo));
        Assertions.assertTrue(((IDefaultPin) defOut.getModulePinImp()).getCheckDataSignature().matches(outOne));
        ((IDefaultPin) defIn.getModulePinImp()).setCheckDataSignature(inTwo);
        Assertions.assertTrue(((IDefaultPin) defIn.getModulePinImp()).getCheckDataSignature().equals(inTwo));
        Assertions.assertTrue(((IDefaultPin) defIn.getModulePinImp()).getCheckDataSignature().matches(inOne));
    }
}
