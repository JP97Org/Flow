package org.jojo.flow.model.storeLoad;

import java.awt.Point;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;

import org.jojo.flow.model.data.Data;
import org.jojo.flow.model.data.Fraction;
import org.jojo.flow.model.data.StringDataSet;
import org.jojo.flow.model.data.Unit;
import org.jojo.flow.model.data.UnitSignature;
import org.jojo.flow.model.flowChart.GraphicalRepresentation;
import org.jojo.flow.model.flowChart.connections.Connection;
import org.jojo.flow.model.flowChart.connections.ConnectionException;
import org.jojo.flow.model.flowChart.connections.StdArrow;
import org.jojo.flow.model.flowChart.modules.ExternalConfig;
import org.jojo.flow.model.flowChart.modules.FlowModule;
import org.jojo.flow.model.flowChart.modules.InputPin;
import org.jojo.flow.model.flowChart.modules.InternalConfig;
import org.jojo.flow.model.flowChart.modules.ModuleGR;
import org.jojo.flow.model.flowChart.modules.ModulePin;
import org.jojo.flow.model.flowChart.modules.OutputPin;
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
    
    public static FlowModule loadModule(final String className) {
        /*try {
            final Class<?> moduleToLoadClass = Class.forName(className);
            //TODO implement
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/
        
        // TODO create correct module instance
        return new MockModule(0, new ExternalConfig(className, 0));
    }
    
    private static class MockModule extends FlowModule {
        public MockModule(int id, ExternalConfig externalConfig) {
            super(id, externalConfig);
            // TODO Auto-generated constructor stub
        }
        
        @Override
        public List<ModulePin> getAllModulePins() {
            return new ArrayList<>();
        }

        @Override
        public Unit<Fraction> getFrequency() {
            return Unit.getFractionConstant(new Fraction(1)).multiply(UnitSignature.HERTZ);
        }

        @Override
        public void run() throws Exception {

        }

        @Override
        public void setInternalConfig(InternalConfig internalConfig) {

        }

        @Override
        public InternalConfig getInternalConfig() {
            return null;
        }

        @Override
        public GraphicalRepresentation getGraphicalRepresentation() {
            return new ModuleGR(new Point(0,0), this, 10, 10, "Mock") {

                @Override
                public String getInfoText() {
                    return "info: mock";
                }

                @Override
                public Window getInternalConfigWindow() {
                    return null;
                }
                
            };
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
}
