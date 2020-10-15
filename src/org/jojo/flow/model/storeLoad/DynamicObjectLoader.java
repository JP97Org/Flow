package org.jojo.flow.model.storeLoad;

import static org.jojo.flow.model.storeLoad.OK.ok;

import java.awt.Point;
import java.awt.Window;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.jojo.flow.model.ModelFacade;
import org.jojo.flow.model.Warning;
import org.jojo.flow.model.data.Data;
import org.jojo.flow.model.data.Fraction;
import org.jojo.flow.model.data.StringDataSet;
import org.jojo.flow.model.data.units.Frequency;
import org.jojo.flow.model.flowChart.FlowChart;
import org.jojo.flow.model.flowChart.FlowChartGR;
import org.jojo.flow.model.flowChart.GraphicalRepresentation;
import org.jojo.flow.model.flowChart.LabelGR;
import org.jojo.flow.model.flowChart.connections.Connection;
import org.jojo.flow.model.flowChart.connections.ConnectionException;
import org.jojo.flow.model.flowChart.connections.ConnectionLineGR;
import org.jojo.flow.model.flowChart.connections.OneConnectionGR;
import org.jojo.flow.model.flowChart.connections.RigidConnection;
import org.jojo.flow.model.flowChart.connections.RigidConnectionGR;
import org.jojo.flow.model.flowChart.connections.DefaultArrow;
import org.jojo.flow.model.flowChart.connections.DefaultArrowGR;
import org.jojo.flow.model.flowChart.modules.ExternalConfig;
import org.jojo.flow.model.flowChart.modules.FlowModule;
import org.jojo.flow.model.flowChart.modules.InputPin;
import org.jojo.flow.model.flowChart.modules.InternalConfig;
import org.jojo.flow.model.flowChart.modules.ModuleGR;
import org.jojo.flow.model.flowChart.modules.ModulePin;
import org.jojo.flow.model.flowChart.modules.ModulePinGR;
import org.jojo.flow.model.flowChart.modules.OutputPin;
import org.jojo.flow.model.flowChart.modules.RigidPin;
import org.jojo.flow.model.flowChart.modules.RigidPinGR;
import org.jojo.flow.model.flowChart.modules.DefaultInputPinGR;
import org.jojo.flow.model.flowChart.modules.DefaultOutputPinGR;
import org.jojo.flow.model.flowChart.modules.DefaultPin;

public class DynamicObjectLoader {
    private static final Point INPOS = new Point(50, 50);
    
    //TODO werden spaeter evtl. private
    public static final Point RIGID_ONE_POS = new Point(10, 10);
    public static final Point RIGID_TWO_POS = new Point(20, 10); 
    
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
                //TODO exc
                return null;
            }
        } catch (ConnectionException e) {
            // should not happen
            e.printStackTrace();
            return null;
        }
    }
    
    public static GraphicalRepresentation loadGR(final String className) {
        return loadGR(className, true);
    }
    
    public static GraphicalRepresentation loadGR(final String className, final boolean hasDefaultPin) {
        final String mmc = MockModuleGR.class.getName();
        if (className.equals(mmc)) {
            return new MockModuleGR(new Point(0,0), 10, 10, "Mock"); //TODO mock noch durch echte ModGR erstellung korrigieren
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
            System.err.println("class not found: " + className + "!"); //TODO EXC
            return null;
        }
    }
    
    public static ModulePin loadPin(final String className, final String classNameImp) {
        //TODO evtl. auch noch anders machen anstatt das MockModule zur erzeugung
        final FlowModule mock = ModelFacade.mock;
        return loadPin(className, classNameImp, mock);
    }
    
    public static ModulePin loadPin(final String className, final String classNameImp, final FlowModule module) {
        if (className.equals(InputPin.class.getName())) {
            if (classNameImp.equals(DefaultPin.class.getName())) {
                return new InputPin(new DefaultPin(module, new StringDataSet("")), (DefaultInputPinGR)loadGR(DefaultInputPinGR.class.getName()));
            } else if (classNameImp.equals(RigidPin.class.getName())) {
                final RigidPinGR gr = (RigidPinGR)loadGR(RigidPinGR.class.getName());
                return new InputPin(new RigidPin(module, gr), gr);
            }
        } else if (className.equals(OutputPin.class.getName())) {
            if (classNameImp.equals(DefaultPin.class.getName())) {
                return new OutputPin(new DefaultPin(module, new StringDataSet("")), (DefaultOutputPinGR)loadGR(DefaultOutputPinGR.class.getName()));
            } else if (classNameImp.equals(RigidPin.class.getName())) {
                final RigidPinGR gr = (RigidPinGR)loadGR(RigidPinGR.class.getName());
                return new OutputPin(new RigidPin(module, gr), gr);
            }
        }
        
        System.err.println("class not found: " + className + "!"); //TODO EXC
        return null;
    }
    
    public static FlowModule loadModule(final String className) {
        try {
            final Class<?> moduleToLoadClass = Class.forName(className);
            final var constr = moduleToLoadClass.getConstructor(int.class, ExternalConfig.class);
            final Object modObj = constr.newInstance(0, new ExternalConfig("NAME", 0));
            final FlowModule ret = (FlowModule)modObj;
            
            // TODO remove this if clause, it is only for debugging
            if (ModelFacade.mock == null) {
                ModelFacade.mock = (MockModule) ret;
            }
            
            return ret;
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | 
                InstantiationException | IllegalAccessException | IllegalArgumentException | 
                InvocationTargetException e) {
            new Warning(null, e.toString(), true).reportWarning();
        }
        return null;
    }
    
    public static class MockModule extends FlowModule {
        private ModulePin pinOut;
        private ModulePin pinIn;
        private List<ModulePin> rigidPins;
        
        public MockModule(int id, ExternalConfig externalConfig) {
            super(id, externalConfig);
        }
        
        @Override
        public List<ModulePin> getAllModulePins() {
            final List<ModulePin> ret = new ArrayList<>();
            this.pinOut = this.pinOut == null ? loadPin(OutputPin.class.getName(), DefaultPin.class.getName(), this) : this.pinOut;
            this.pinIn = this.pinIn == null ? loadPin(InputPin.class.getName(), DefaultPin.class.getName(), this) : this.pinIn;
            this.pinIn.getGraphicalRepresentation().setPosition(INPOS);
            ret.add(this.pinOut);
            ret.add(this.pinIn);
            if (this.rigidPins == null) {
                this.rigidPins = new ArrayList<>();
                this.rigidPins.addAll(Arrays.asList(loadPin(OutputPin.class.getName(), RigidPin.class.getName(), this), 
                        loadPin(InputPin.class.getName(), RigidPin.class.getName(), this)));
                this.rigidPins.forEach(p -> ((ModulePinGR)p.getGraphicalRepresentation()).setLinePoint(RIGID_ONE_POS));
                final List<ModulePin> rigidPins = Arrays.asList(loadPin(OutputPin.class.getName(), RigidPin.class.getName(), this), 
                        loadPin(InputPin.class.getName(), RigidPin.class.getName(), this));
                rigidPins.forEach(p -> ((ModulePinGR)p.getGraphicalRepresentation()).setLinePoint(RIGID_TWO_POS));
                this.rigidPins.addAll(rigidPins);
            }
            ret.addAll(this.rigidPins);
            return ret;
        }

        @Override
        public Frequency<Fraction> getFrequency() {
            return Frequency.getFractionConstant(new Fraction(1));
        }

        @Override
        public void run() throws Exception {

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
                            this.rigidPins.add(loadPin(pinCn, pinCnImp, this));
                            this.rigidPins.get(this.rigidPins.size() - 1).restoreFromDOM(pinDom);
                        }
                        i++;
                    }
                }
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
        public InternalConfig getInternalConfig() {
            return null;
        }
        
        @Override
        public void setInternalConfig(DOM internalConfigDOM) {
            // TODO Auto-generated method stub
            
        }
        
        @Override
        public boolean isInternalConfigDOMValid(DOM internalConfigDOM) {
            // TODO Auto-generated method stub
            return true;
        } 

        @Override
        public GraphicalRepresentation getGraphicalRepresentation() {
            MockModuleGR ret = new MockModuleGR(new Point(0,0), 10, 10, "Mock");
            ret.setModuleMock(this);
            return ret;
        }

        @Override
        public InternalConfig serializeInternalConfig() {
            return null;
        }

        @Override
        public void restoreSerializedInternalConfig(InternalConfig internalConfig) {
            
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
