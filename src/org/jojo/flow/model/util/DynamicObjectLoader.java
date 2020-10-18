package org.jojo.flow.model.util;

import static org.jojo.flow.model.storeLoad.OK.ok;

import java.awt.Point;
import java.awt.Window;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.jojo.flow.exc.ConnectionException;
import org.jojo.flow.exc.FlowException;
import org.jojo.flow.exc.ParsingException;
import org.jojo.flow.exc.ValidationException;
import org.jojo.flow.exc.Warning;
import org.jojo.flow.model.api.IDataSignature;
import org.jojo.flow.model.api.IDefaultArrow;
import org.jojo.flow.model.api.IExternalConfig;
import org.jojo.flow.model.api.IInputPin;
import org.jojo.flow.model.api.IInternalConfig;
import org.jojo.flow.model.api.IModulePin;
import org.jojo.flow.model.api.IOutputPin;
import org.jojo.flow.model.api.IRigidConnection;
import org.jojo.flow.model.api.IRigidPin;
import org.jojo.flow.model.data.Data;
import org.jojo.flow.model.data.Fraction;
import org.jojo.flow.model.data.StringDataSet;
import org.jojo.flow.model.data.units.Frequency;
import org.jojo.flow.model.flowChart.FlowChart;
import org.jojo.flow.model.flowChart.FlowChartGR;
import org.jojo.flow.model.flowChart.GraphicalRepresentation;
import org.jojo.flow.model.flowChart.LabelGR;
import org.jojo.flow.model.flowChart.connections.Connection;
import org.jojo.flow.model.flowChart.connections.ConnectionLineGR;
import org.jojo.flow.model.flowChart.connections.OneConnectionGR;
import org.jojo.flow.model.flowChart.connections.RigidConnection;
import org.jojo.flow.model.flowChart.connections.RigidConnectionGR;
import org.jojo.flow.model.flowChart.connections.DefaultArrow;
import org.jojo.flow.model.flowChart.connections.DefaultArrowGR;
import org.jojo.flow.model.flowChart.modules.ExternalConfig;
import org.jojo.flow.model.flowChart.modules.FlowModule;
import org.jojo.flow.model.flowChart.modules.InputPin;
import org.jojo.flow.model.flowChart.modules.ModuleGR;
import org.jojo.flow.model.flowChart.modules.ModulePin;
import org.jojo.flow.model.flowChart.modules.ModulePinGR;
import org.jojo.flow.model.flowChart.modules.OutputPin;
import org.jojo.flow.model.flowChart.modules.RigidPin;
import org.jojo.flow.model.flowChart.modules.RigidPinGR;
import org.jojo.flow.model.storeLoad.DOM;
import org.jojo.flow.model.storeLoad.GraphicalRepresentationDOM;
import org.jojo.flow.model.storeLoad.ModulePinDOM;
import org.jojo.flow.model.storeLoad.OK;
import org.jojo.flow.model.storeLoad.PointDOM;
import org.jojo.flow.model.flowChart.modules.DefaultInputPinGR;
import org.jojo.flow.model.flowChart.modules.DefaultOutputPinGR;
import org.jojo.flow.model.flowChart.modules.DefaultPin;

public final class DynamicObjectLoader {
    private static final Point INPOS = new Point(50, 50);
    
    private static final Point RIGID_ONE_POS = new Point(10, 10);
    public static final Point RIGID_ONE_POS() {
        return new Point(RIGID_ONE_POS.x, RIGID_ONE_POS.y);
    }
    private static final Point RIGID_TWO_POS = new Point(20, 10); 
    public static final Point RIGID_TWO_POS() {
        return new Point(RIGID_TWO_POS.x, RIGID_TWO_POS.y);
    }
    
    private DynamicObjectLoader() {
        
    }
    
    public static FlowChart loadEmptyFlowChart(final int id) {
        return new FlowChart(id, new FlowChartGR());
    }
    
    public static FlowChart loadFlowChartFromDOM(final DOM flowChartDOM) {
        return restoreFlowChartFromDOM(loadEmptyFlowChart(0), flowChartDOM);
    }
    
    public static FlowChart restoreFlowChartFromDOM(final FlowChart flowChart, final DOM flowChartDOM) {
        Objects.requireNonNull(flowChart);
        Objects.requireNonNull(flowChartDOM);
        if (flowChart.isDOMValid(flowChartDOM)) {
            Objects.requireNonNull(flowChart).restoreFromDOM(flowChartDOM);
            DOM.resetDocument();
            return flowChart;
        }
        return null;
    }
    
    public static Connection loadConnection(final String className) {
        Objects.requireNonNull(className);
        try {
            if (className.equals(DefaultArrow.class.getName())) {
                Data data = new StringDataSet(className);
                return new DefaultArrow(0,
                        new OutputPin(new DefaultPin(loadModule(MockModule.class.getName()), data ), 
                                new DefaultOutputPinGR(new Point(0,0), className, 10, 10)), 
                        new InputPin(new DefaultPin(loadModule(MockModule.class.getName()), data), 
                                new DefaultInputPinGR(INPOS, className, 10, 10)), className);
            } else if (className.equals(RigidConnection.class.getName())) {
                return new RigidConnection(0,
                        new RigidPin(loadModule(MockModule.class.getName()), 
                                new RigidPinGR(RIGID_ONE_POS, className, 10, 10)), 
                        new RigidPin(loadModule(MockModule.class.getName()), 
                                new RigidPinGR(RIGID_TWO_POS, className, 10, 10)), className);
            } else {
                new Warning(null, "invalid connection classname= " + className, true).reportWarning();
                return null;
            }
        } catch (ConnectionException e) {
            // should not happen
            new Warning(null, e.toString(), true).reportWarning();
            return null;
        }
    }
    
    public static IDefaultArrow loadConnection(final int id, final IOutputPin from, final IInputPin to, final String name) {
        Objects.requireNonNull(from);
        Objects.requireNonNull(to);
        try {
            return (IDefaultArrow) load(DynamicObjectLoader.class.getClassLoader(), DefaultArrow.class.getName(),
                        new Class<?>[] {int.class, IOutputPin.class, IInputPin.class, String.class}, id, from, to, name);
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
                    | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new IllegalArgumentException("an exception occured: " + e.toString());
        }
    }
    
    public static IRigidConnection loadConnection(final int id, final IRigidPin asFrom, final IRigidPin asTo, final String name) {
        Objects.requireNonNull(asFrom);
        Objects.requireNonNull(asTo);
        try {
            return (IRigidConnection) load(DynamicObjectLoader.class.getClassLoader(), RigidConnection.class.getName(),
                        new Class<?>[] {int.class, IRigidPin.class, IRigidPin.class, String.class}, id, asFrom, asTo, name);
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
                    | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new IllegalArgumentException("an exception occured: " + e.toString());
        }
    }
    
    public static GraphicalRepresentation loadGR(final String className) {
        return loadGR(className, true);
    }
    
    public static GraphicalRepresentation loadGR(final String className, final boolean hasDefaultPin) {
        final String mmc = MockModuleGR.class.getName();
        if (className.equals(mmc)) {
            return new MockModuleGR(new Point(0,0), 10, 10, "Mock");
        } else if (className.equals(FlowChartGR.class.getName())) {
            return new FlowChartGR();
        } else if (className.equals(LabelGR.class.getName())) {
            return new LabelGR(null, "", new Point(0,0), 1, 1);
        } else if (className.equals(ConnectionLineGR.class.getName())) {
            return new ConnectionLineGR(new Point(0,0), new Point(0,1));
        } else if (className.equals(OneConnectionGR.class.getName())) {
            if (hasDefaultPin) {
                return new OneConnectionGR(new DefaultOutputPinGR(new Point(0,0), "", 1, 1), new DefaultInputPinGR(new Point(0,1), "", 1, 1));
            } else {
                return new OneConnectionGR(new RigidPinGR(new Point(0,0), "", 1, 1), new RigidPinGR(new Point(0,1), "", 1, 1));
            }
        } else if (className.equals(RigidConnectionGR.class.getName())) {
            return new RigidConnectionGR(new RigidPinGR(new Point(0,0), "", 1, 1), new RigidPinGR(new Point(0,1), "", 1, 1));
        } else if (className.equals(DefaultArrowGR.class.getName())) {
            return new DefaultArrowGR(new DefaultOutputPinGR(new Point(0,0), "", 1, 1), new DefaultInputPinGR(new Point(0,1), "", 1, 1), null);
        } else if (className.equals(RigidPinGR.class.getName())) {
            return new RigidPinGR(new Point(0,0), "", 1, 1);
        } else if (className.equals(DefaultInputPinGR.class.getName())) {
            return new DefaultInputPinGR(new Point(0,0), "", 1, 1);
        } else if (className.equals(DefaultOutputPinGR.class.getName())) {
            return new DefaultOutputPinGR(new Point(0,0), "", 1, 1);
        }
        else {
            new Warning(null, "invalid GR classname= " + className, true).reportWarning();
            return null;
        }
    }
    
    public static ModulePin loadPin(final String className, final String classNameImp) {
        final FlowModule mock = loadModule(MockModule.class.getName());
        return loadPin(className, classNameImp, mock);
    }
    
    public static ModulePin loadPin(final String className, final String classNameImp, final FlowModule module) {
        if (className.equals(InputPin.class.getName())) {
            if (classNameImp.equals(DefaultPin.class.getName())) {
                return new InputPin(new DefaultPin(module, new StringDataSet("")), (DefaultInputPinGR)loadGR(DefaultInputPinGR.class.getName()));
            } else if (classNameImp.equals(RigidPin.class.getName())) {
                final RigidPin rigidPin = loadRigidPin(module);
                return rigidPin.getInputPin();
            }
        } else if (className.equals(OutputPin.class.getName())) {
            if (classNameImp.equals(DefaultPin.class.getName())) {
                return new OutputPin(new DefaultPin(module, new StringDataSet("")), (DefaultOutputPinGR)loadGR(DefaultOutputPinGR.class.getName()));
            } else if (classNameImp.equals(RigidPin.class.getName())) {
                final RigidPin rigidPin = loadRigidPin(module);
                return rigidPin.getOutputPin();
            }
        }
        
        new Warning(null, "invalid pin classname= " + className, true).reportWarning();
        return null;
    }
    
    public static RigidPin loadRigidPin(final FlowModule moduleArg) {
        final FlowModule mock = loadModule(MockModule.class.getName());
        final FlowModule module = moduleArg == null ? mock : moduleArg;
        final RigidPinGR gr = (RigidPinGR)loadGR(RigidPinGR.class.getName());
        return new RigidPin(module, gr);
    }
    
    private static FlowModule loadModule(final String className) {
        return loadModule(className, 0);
    }
    
    public static FlowModule loadModule(final String className, final int id) {
        return loadModule(DynamicObjectLoader.class.getClassLoader(), className, id);
    }
    
    public static FlowModule loadModule(final ClassLoader classLoader, final String className, final int id) {
        final IExternalConfig config = getNewMockExternalConfig();
        return loadModule(classLoader, className, id, config.getName(), config.getPriority());
    }
    
    public static FlowModule loadModule(final ClassLoader classLoader, final String className, final int id,
            final String name, final int priority) {
        try {
            final IExternalConfig config = new ExternalConfig(name, priority);
            final Object modObj = load(classLoader, className, 
                    new Class<?>[] {int.class, ExternalConfig.class}, id, config);
            final FlowModule ret = (FlowModule)modObj;
            
            return ret;
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | 
                InstantiationException | IllegalAccessException | IllegalArgumentException | 
                InvocationTargetException e) {
            new Warning(null, e.toString(), true).reportWarning();
        }
        return null;
    }
    
    public static Object load(final ClassLoader classLoader, final String className, final Class<?>[] parameterTypes, final Object... initArgs) throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        final Class<?> moduleToLoadClass = Class.forName(className, true, classLoader);
        final var constr = moduleToLoadClass.getConstructor(parameterTypes);
        return constr.newInstance(initArgs);
    }
    
    public static IExternalConfig getNewMockExternalConfig() {
        return new ExternalConfig("NAME", 0);
    }
    
    public static class MockModule extends FlowModule {
        private final MockModuleGR gr;
        private ModulePin pinOut;
        private ModulePin pinIn;
        private List<ModulePin> rigidPins;
        
        public MockModule(int id, ExternalConfig externalConfig) {
            super(id, externalConfig);
            this.gr = (MockModuleGR) loadGR(MockModuleGR.class.getName());
            this.gr.setModuleMock(this);
            this.getExternalConfig().setModule(this);
        }
        
        @Override
        public List<IModulePin> getAllModulePins() {
            final List<IModulePin> ret = new ArrayList<>();
            this.pinOut = this.pinOut == null ? loadPin(OutputPin.class.getName(), DefaultPin.class.getName(), this) : this.pinOut;
            this.pinIn = this.pinIn == null ? loadPin(InputPin.class.getName(), DefaultPin.class.getName(), this) : this.pinIn;
            this.pinIn.getGraphicalRepresentation().setPosition(INPOS);
            ret.add(this.pinOut);
            ret.add(this.pinIn);
            if (this.rigidPins == null) {
                this.rigidPins = new ArrayList<>();
                final RigidPin rigidPinOne = loadRigidPin(this);
                ((ModulePinGR) rigidPinOne.getOutputPin().getGraphicalRepresentation()).setLinePoint(RIGID_ONE_POS);
                
                final RigidPin rigidPinTwo = loadRigidPin(this);
                ((ModulePinGR) rigidPinTwo.getOutputPin().getGraphicalRepresentation()).setLinePoint(RIGID_TWO_POS);
                this.rigidPins.add(rigidPinOne.getOutputPin());
                this.rigidPins.add(rigidPinOne.getInputPin());
                this.rigidPins.add(rigidPinTwo.getOutputPin());
                this.rigidPins.add(rigidPinTwo.getInputPin());
            }
            ret.addAll(this.rigidPins);
            return ret;
        }

        @Override
        public Frequency<Fraction> getFrequency() {
            return Frequency.getFractionConstant(new Fraction(1));
        }
        
        @Override
        public IDefaultArrow validate() throws ValidationException {
            getAllModulePins(); // if initializing is necessary
            final IDataSignature before = 
                    ((DefaultPin)this.pinIn.getModulePinImp()).getCheckDataSignature().getCopy();
            final IDataSignature checkingDataSignature = 
                    ((DefaultPin)this.pinOut.getModulePinImp()).getCheckDataSignature().getCopy();
            try {
                ((DefaultPin)this.pinIn.getModulePinImp()).setCheckDataSignature(checkingDataSignature);
            } catch (FlowException e) {
                // should not happen
                e.printStackTrace();
            }
            final IDefaultArrow ret = super.validate();
            try {
                ((DefaultPin)this.pinIn.getModulePinImp()).setCheckDataSignature(before);
            } catch (FlowException e) {
                // should not happen
                e.printStackTrace();
            }
            return ret;
        }

        @Override
        public void run() throws Exception {
            Thread.sleep(100);
            /*for(long i = 0; i < 1000000000; i++) {
                
            }*/
        }

        @Override
        protected void setAllModulePins(final DOM pinsDom) {
            if (isPinsDOMValid(pinsDom)) {
                if (this.rigidPins == null) {
                    this.rigidPins = new ArrayList<>();
                }
                this.rigidPins.clear();
                final Map<String, Object> domMap = pinsDom.getDOMMap();
                int i = 0;
                for(var pinObj : domMap.values()) {
                    if (pinObj instanceof DOM) {
                        final DOM pinDom = (DOM) pinObj;
                        final DOM pinCnDom = (DOM)pinDom.getDOMMap().get(ModulePinDOM.NAME_CLASSNAME);
                        final String pinCn = pinCnDom.elemGet();
                        final DOM pinCnDomImp = (DOM)pinDom.getDOMMap().get(ModulePinDOM.NAME_CLASSNAME_IMP);
                        final String pinCnImp = pinCnDomImp.elemGet();
                        if (pinCnImp.equals(DefaultPin.class.getName())) {
                            if (pinCn.equals(OutputPin.class.getName())) {
                                this.pinOut = loadPin(pinCn, pinCnImp, this);
                                this.pinOut.restoreFromDOM(pinDom);
                            } else {
                                this.pinIn = loadPin(pinCn, pinCnImp, this);
                                this.pinIn.restoreFromDOM(pinDom);
                            }
                        } else {
                            if (this.rigidPins.isEmpty()) {
                                this.rigidPins.add(loadPin(pinCn, pinCnImp, this));
                            } else {
                                Point lastLinePoint = null;
                                final Point thisLinePoint = PointDOM.pointOf((DOM) ((DOM) pinDom.getDOMMap()
                                        .get(GraphicalRepresentationDOM.NAME)).getDOMMap()
                                        .get("linePoint"));
                                int index = 0;
                                inner:
                                for (; index < this.rigidPins.size(); index++) {
                                    lastLinePoint = ((ModulePinGR) this.rigidPins.get(index)
                                            .getGraphicalRepresentation()).getLinePoint();
                                    if (thisLinePoint.equals(lastLinePoint)) {
                                        break inner;
                                    }
                                }
                                index = index == this.rigidPins.size() ? 0 : index;
                                
                                if (pinCn.equals(OutputPin.class.getName())) {
                                    if (thisLinePoint.equals(lastLinePoint)) {
                                        this.rigidPins.add(((RigidPin) this.rigidPins.get(index)
                                                .getModulePinImp()).getOutputPin());
                                    } else {
                                        this.rigidPins.add(loadPin(pinCn, pinCnImp, this));
                                    }
                                } else {
                                    if (thisLinePoint.equals(lastLinePoint)) {
                                        this.rigidPins.add(((RigidPin) this.rigidPins.get(index)
                                                .getModulePinImp()).getInputPin());
                                    } else {
                                        this.rigidPins.add(loadPin(pinCn, pinCnImp, this));
                                    }
                                }
                            }
                            this.rigidPins.get(this.rigidPins.size() - 1).restoreFromDOM(pinDom);
                        }
                        i++;
                    }
                }
                assert (this.rigidPins.size() == 4);
                assert (i == 6);
            }
        }

        @Override
        protected boolean isPinsDOMValid(final DOM pinsDom) {
            Objects.requireNonNull(pinsDom);
            final Map<String, Object> domMap = pinsDom.getDOMMap();
            try {
                int i = 0;
                for(var pinObj : domMap.values()) {
                    if (pinObj instanceof DOM) {
                        ok(pinObj instanceof DOM, OK.ERR_MSG_WRONG_CAST);
                        final DOM pinDom = (DOM) pinObj;
                        ok(pinDom.getDOMMap().get(ModulePinDOM.NAME_CLASSNAME) instanceof DOM, OK.ERR_MSG_WRONG_CAST);
                        final DOM pinCnDom = (DOM)pinDom.getDOMMap().get(ModulePinDOM.NAME_CLASSNAME);
                        final String pinCn = pinCnDom.elemGet();
                        ok(pinCn != null, OK.ERR_MSG_NULL);
                        ok(pinDom.getDOMMap().get(ModulePinDOM.NAME_CLASSNAME_IMP) instanceof DOM, OK.ERR_MSG_WRONG_CAST);
                        final DOM pinCnDomImp = (DOM)pinDom.getDOMMap().get(ModulePinDOM.NAME_CLASSNAME_IMP);
                        final String pinCnImp = pinCnDomImp.elemGet();
                        ok(pinCnImp != null, OK.ERR_MSG_NULL);
                        final ModulePin pin = ok(x -> loadPin(pinCn, pinCnImp, this), "");
                        ok(pin.isDOMValid(pinDom), "PIN " + OK.ERR_MSG_DOM_NOT_VALID);
                        i++;
                    }
                }
                ok(i == 6, "to many or not enough pins should= 6, is = " + i);
                return true;
            } catch (ParsingException e) {
                e.getWarning().setAffectedElement(this).reportWarning();
                return false;
            }
        }

        @Override
        public IInternalConfig getInternalConfig() {
            return null;
        }
        
        @Override
        public void setInternalConfig(DOM internalConfigDOM) {
            
        }
        
        @Override
        public boolean isInternalConfigDOMValid(DOM internalConfigDOM) {
            return true;
        } 

        @Override
        public GraphicalRepresentation getGraphicalRepresentation() {
            return this.gr;
        }

        @Override
        public IInternalConfig serializeInternalConfig() {
            return null;
        }

        @Override
        public void restoreSerializedInternalConfig(IInternalConfig internalConfig) {
            
        }

        @Override
        public String serializeSimulationState() {
            return null;
        }

        @Override
        public void restoreSerializedSimulationState(String simulationState) {
            
        }
        
        public void setId(final int id) {
            super.setId(id);
        }
    }
    
    public static class MockModuleGR extends ModuleGR {
        public MockModuleGR(Point position, int height, int width, String iconText) {
            super(position, height, width, iconText);
        }

        @Override
        public String getInfoText() {
            return "info: mock";
        }

        @Override
        public Window getInternalConfigWindow() {
            return null;
        }
    }
}
