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
import org.junit.*;

public class ModulePinsTest {
	private IFlowModule mock;
	private IOutputPin defOut;
	private IInputPin defIn;
	private IRigidPin rigid;
	
	@Before
	public void setUp() {
		this.mock = DynamicObjectLoader.loadModule(DynamicObjectLoader.MockModule.class.getName(), 100);
		this.defOut = (IOutputPin) DynamicObjectLoader.loadPin(OutputPin.class.getName(), DefaultPin.class.getName(), mock);
		this.defIn = (IInputPin) DynamicObjectLoader.loadPin(InputPin.class.getName(), DefaultPin.class.getName(), mock);
		this.rigid = DynamicObjectLoader.loadRigidPin(mock);
	}
	
	@Test 
	public void get() {
		Assert.assertTrue(defOut.getGraphicalRepresentation() != null);
		Assert.assertTrue(defIn.getGraphicalRepresentation() != null);
		Assert.assertTrue(defOut.getModule() == mock);
		Assert.assertTrue(defIn.getModule() == mock);
		final IOutputPin rigidOut = rigid.getOutputPin();
		final IInputPin rigidIn = rigid.getInputPin();
		Assert.assertTrue(rigidOut.getModule() == mock);
		Assert.assertTrue(rigidIn.getModule() == mock);
		Assert.assertTrue(rigidOut != null && rigidOut == rigid.getOutputPin());
		Assert.assertTrue(rigidIn != null && rigidIn == rigid.getInputPin());
		Assert.assertTrue(rigidOut.getGraphicalRepresentation() != null);
		Assert.assertTrue(rigidIn.getGraphicalRepresentation() != null);
		Assert.assertTrue(defOut.getConnections().isEmpty());
		Assert.assertTrue(defIn.getConnections().isEmpty());
		Assert.assertTrue(rigidOut.getConnections().isEmpty());
		Assert.assertTrue(rigidIn.getConnections().isEmpty());
		Assert.assertTrue(defOut.getModulePinImp() instanceof IDefaultPin);
		Assert.assertTrue(defIn.getModulePinImp() instanceof IDefaultPin);
		Assert.assertTrue(((IDefaultPin)defOut.getModulePinImp()).getCheckDataSignature() != null);
		Assert.assertTrue(((IDefaultPin)defIn.getModulePinImp()).getCheckDataSignature() != null);
		Assert.assertTrue(rigidOut.getModulePinImp() == rigid);
		Assert.assertTrue(rigidIn.getModulePinImp() == rigid);
		Assert.assertTrue(defOut.isDOMValid(defOut.getDOM()));
		Assert.assertTrue(defIn.isDOMValid(defIn.getDOM()));
		Assert.assertTrue(rigidOut.isDOMValid(rigidOut.getDOM()));
		Assert.assertTrue(rigidIn.isDOMValid(rigidIn.getDOM()));
		Assert.assertTrue(defIn.getData() == defIn.getDefaultData());
	}
	
	@Test
	public void set() throws FlowException {
		final IConnection def = new DefaultArrow(200, defOut, defIn, "def");
		final IConnection rig = new RigidConnection(300, rigid, rigid, "rig");
		Assert.assertTrue(def.connect());
		Assert.assertTrue(rig.connect());
		Assert.assertTrue(defOut.getConnections().size() == 1);
		Assert.assertTrue(defIn.getConnections().size() == 1);
		Assert.assertTrue(rigid.getConnections().size() == 1);
		Assert.assertTrue(defOut.getConnections().get(0) == defIn.getConnections().get(0));
		final IDataSignature outOne = ((IDefaultPin)defOut.getModulePinImp()).getCheckDataSignature().getCopy();
		outOne.deactivateChecking();
		final IDataSignature outTwo = ((IDefaultPin)defOut.getModulePinImp()).getCheckDataSignature().getCopy();
		final IDataSignature inOne = ((IDefaultPin)defOut.getModulePinImp()).getCheckDataSignature().getCopy();
		inOne.deactivateChecking();
		final IDataSignature inTwo = ((IDefaultPin)defOut.getModulePinImp()).getCheckDataSignature().getCopy();
		def.disconnect();
		Assert.assertTrue(defOut.getConnections().isEmpty());
		Assert.assertTrue(defIn.getConnections().isEmpty());
		((IDefaultPin)defOut.getModulePinImp()).setCheckDataSignature(outOne);
		Assert.assertTrue(((IDefaultPin)defOut.getModulePinImp()).getCheckDataSignature().matches(outTwo));
		Assert.assertTrue(((IDefaultPin)defOut.getModulePinImp()).getCheckDataSignature().equals(outOne));
		((IDefaultPin)defIn.getModulePinImp()).setCheckDataSignature(inOne);
		Assert.assertTrue(((IDefaultPin)defIn.getModulePinImp()).getCheckDataSignature().matches(inTwo));
		Assert.assertTrue(((IDefaultPin)defIn.getModulePinImp()).getCheckDataSignature().equals(inOne));
		Assert.assertTrue(def.connect());
		((IDefaultPin)defOut.getModulePinImp()).setCheckDataSignature(outTwo);
		Assert.assertTrue(((IDefaultPin)defOut.getModulePinImp()).getCheckDataSignature().equals(outTwo));
		Assert.assertTrue(((IDefaultPin)defOut.getModulePinImp()).getCheckDataSignature().matches(outOne));
		((IDefaultPin)defIn.getModulePinImp()).setCheckDataSignature(inTwo);
		Assert.assertTrue(((IDefaultPin)defIn.getModulePinImp()).getCheckDataSignature().equals(inTwo));
		Assert.assertTrue(((IDefaultPin)defIn.getModulePinImp()).getCheckDataSignature().matches(inOne));
	}
}
