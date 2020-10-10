package org.jojo.flow.model.storeLoad;

import java.awt.Point;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;

import org.jojo.flow.model.ModelFacade;
import org.jojo.flow.model.data.Data;
import org.jojo.flow.model.data.Fraction;
import org.jojo.flow.model.data.StringDataSet;
import org.jojo.flow.model.data.Unit;
import org.jojo.flow.model.data.UnitSignature;
import org.jojo.flow.model.flowChart.FlowChartGR;
import org.jojo.flow.model.flowChart.GraphicalRepresentation;
import org.jojo.flow.model.flowChart.LabelGR;
import org.jojo.flow.model.flowChart.connections.Connection;
import org.jojo.flow.model.flowChart.connections.ConnectionException;
import org.jojo.flow.model.flowChart.connections.ConnectionLineGR;
import org.jojo.flow.model.flowChart.connections.OneConnectionGR;
import org.jojo.flow.model.flowChart.connections.RigidConnectionGR;
import org.jojo.flow.model.flowChart.connections.StdArrow;
import org.jojo.flow.model.flowChart.connections.StdArrowGR;
import org.jojo.flow.model.flowChart.modules.ExternalConfig;
import org.jojo.flow.model.flowChart.modules.FlowModule;
import org.jojo.flow.model.flowChart.modules.InputPin;
import org.jojo.flow.model.flowChart.modules.InternalConfig;
import org.jojo.flow.model.flowChart.modules.ModuleGR;
import org.jojo.flow.model.flowChart.modules.ModulePin;
import org.jojo.flow.model.flowChart.modules.OutputPin;
import org.jojo.flow.model.flowChart.modules.RigidPin;
import org.jojo.flow.model.flowChart.modules.RigidPinGR;
import org.jojo.flow.model.flowChart.modules.StdInputPinGR;
import org.jojo.flow.model.flowChart.modules.StdOutputPinGR;
import org.jojo.flow.model.flowChart.modules.StdPin;

public class DynamicClassLoader {
    private DynamicClassLoader() {
        
    }
    
    public static Connection loadConnection(final String className) {
        //TODO implement
        try {
            Data data = new StringDataSet(className);
            return new StdArrow(1000, //TODO remove this mock arrow
                    new OutputPin(new StdPin(loadModule(""), data ), 
                            new StdOutputPinGR(new Point(0,0), className, 10, 10)), 
                    new InputPin(new StdPin(loadModule(""), data), 
                            new StdInputPinGR(new Point(0,10), className, 10, 10)), className);
        } catch (ConnectionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }
    
    public static GraphicalRepresentation loadGR(final String className) {
        return loadGR(className, true);
    }
    
    public static GraphicalRepresentation loadGR(final String className, final boolean hasStdPin) {
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
            if (hasStdPin) {
                return new OneConnectionGR(new StdOutputPinGR(new Point(0,0), "", 1, 1), new StdInputPinGR(new Point(0,1), "", 1, 1));
            } else {
                return new OneConnectionGR(new RigidPinGR(new Point(0,0), "", 1, 1), new RigidPinGR(new Point(0,1), "", 1, 1));
            }
        } else if (className.equals(RigidConnectionGR.class.getName())) {
            return new RigidConnectionGR(new RigidPinGR(new Point(0,0), "", 1, 1), new RigidPinGR(new Point(0,1), "", 1, 1));
        } else if (className.equals(StdArrowGR.class.getName())) {
            return new StdArrowGR(new StdOutputPinGR(new Point(0,0), "", 1, 1), new StdInputPinGR(new Point(0,1), "", 1, 1), null);
        } else if (className.equals(RigidPinGR.class.getName())) {
            return new RigidPinGR(new Point(0,0), "", 1, 1);
        } else if (className.equals(StdInputPinGR.class.getName())) {
            return new StdInputPinGR(new Point(0,0), "", 1, 1);
        } else if (className.equals(StdOutputPinGR.class.getName())) {
            return new StdOutputPinGR(new Point(0,0), "", 1, 1);
        }
        else {
            System.err.println("class not found: " + className + "!"); //TODO EXC
            return null;
        }
    }
    
    public static ModulePin loadPin(final String className, final String classNameImp) {
        //TODO evtl. auch noch anders machen anstatt das MockModule zur erzeugung
        final FlowModule mock = ModelFacade.mock;
        if (className.equals(InputPin.class.getName())) {
            if (classNameImp.equals(StdPin.class.getName())) {
                return new InputPin(new StdPin(mock, new StringDataSet("")), (StdInputPinGR)loadGR(StdInputPinGR.class.getName()));
            } else if (classNameImp.equals(RigidPin.class.getName())) {
                final RigidPinGR gr = (RigidPinGR)loadGR(RigidPinGR.class.getName());
                return new InputPin(new RigidPin(mock, gr), gr);
            }
        } else if (className.equals(OutputPin.class.getName())) {
            if (classNameImp.equals(StdPin.class.getName())) {
                return new OutputPin(new StdPin(mock, new StringDataSet("")), (StdOutputPinGR)loadGR(StdOutputPinGR.class.getName()));
            } else if (classNameImp.equals(RigidPin.class.getName())) {
                final RigidPinGR gr = (RigidPinGR)loadGR(RigidPinGR.class.getName());
                return new OutputPin(new RigidPin(mock, gr), gr);
            }
        }
        
        System.err.println("class not found: " + className + "!"); //TODO EXC
        return null;
    }
    
    public static FlowModule loadModule(final String className) {
        /*try {
            final Class<?> moduleToLoadClass = Class.forName(className);
            //TODO implement
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/
        
        // TODO create correct module instance
        if (ModelFacade.mock == null) {
            ModelFacade.mock = new MockModule(100, new ExternalConfig(className, 0));
        }
        return ModelFacade.mock;
    }
    
    public static class MockModule extends FlowModule {
        private ModulePin pin;
        
        public MockModule(int id, ExternalConfig externalConfig) {
            super(id, externalConfig);
        }
        
        @Override
        public List<ModulePin> getAllModulePins() {
            final List<ModulePin> ret = new ArrayList<>();
            this.pin = this.pin == null ? loadPin(OutputPin.class.getName(), StdPin.class.getName()) : this.pin;
            ret.add(this.pin);
            return ret;
        }

        @Override
        public Unit<Fraction> getFrequency() {
            return Unit.getFractionConstant(new Fraction(1)).multiply(UnitSignature.HERTZ);
        }

        @Override
        public void run() throws Exception {

        }

        @Override
        protected void setAllModulePins(DOM pinsDom) {
            // TODO Auto-generated method stub
            
        }

        @Override
        protected boolean isPinsDOMValid(DOM pinsDom) {
            // TODO Auto-generated method stub
            return true;
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
