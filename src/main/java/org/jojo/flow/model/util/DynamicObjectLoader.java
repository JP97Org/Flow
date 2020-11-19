package org.jojo.flow.model.util;

import static org.jojo.flow.model.util.OK.ok;

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
import org.jojo.flow.model.api.DOMStringUnion;
import org.jojo.flow.model.api.IConnection;
import org.jojo.flow.model.api.IDOM;
import org.jojo.flow.model.api.IDataSignature;
import org.jojo.flow.model.api.IDefaultArrow;
import org.jojo.flow.model.api.IExternalConfig;
import org.jojo.flow.model.api.IFlowChart;
import org.jojo.flow.model.api.IFlowModule;
import org.jojo.flow.model.api.IGraphicalRepresentation;
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
import org.jojo.flow.model.flowChart.modules.ModulePinGR;
import org.jojo.flow.model.flowChart.modules.OutputPin;
import org.jojo.flow.model.flowChart.modules.RigidPin;
import org.jojo.flow.model.flowChart.modules.RigidPinGR;
import org.jojo.flow.model.storeLoad.GraphicalRepresentationDOM;
import org.jojo.flow.model.storeLoad.ModulePinDOM;
import org.jojo.flow.model.storeLoad.PointDOM;
import org.jojo.flow.model.flowChart.modules.DefaultInputPinGR;
import org.jojo.flow.model.flowChart.modules.DefaultOutputPinGR;
import org.jojo.flow.model.flowChart.modules.DefaultPin;

/**
 * This utility class can be used for loading objects of several types dynamically at runtime.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public final class DynamicObjectLoader {
    private static final Point INPOS = new Point(50, 50);
    
    private static final Point RIGID_ONE_POS = new Point(10, 10);
    
    /**
     * 
     * @return a new instance of the default point of the first rigid pin
     */
    public static final Point RIGID_ONE_POS() {
        return new Point(RIGID_ONE_POS.x, RIGID_ONE_POS.y);
    }
    
    private static final Point RIGID_TWO_POS = new Point(20, 10); 
    
    /**
     * 
     * @return a new instance of the default point of the second rigid pin
     */
    public static final Point RIGID_TWO_POS() {
        return new Point(RIGID_TWO_POS.x, RIGID_TWO_POS.y);
    }
    
    private DynamicObjectLoader() {
        
    }
    
    /**
     * Loads an empty flow chart with the given ID.
     * 
     * @param id - the given ID
     * @return an empty flow chart with the given ID
     */
    public static IFlowChart loadEmptyFlowChart(final int id) {
        return new FlowChart(id, new FlowChartGR());
    }
    
    /**
     * Restores a flow chart from a given DOM and resets the document afterwards.
     * 
     * @param fcDom - the DOM for restoring the flow chart
     * @return the restored flow chart
     * @see IDOM#resetDocument()
     */
    public static IFlowChart loadFlowChartFromDOM(final IDOM fcDom) {
        return restoreFlowChartFromDOM(loadEmptyFlowChart(0), fcDom);
    }
    
    /**
     * Restores a flow chart from a given DOM and resets the document afterwards.
     * 
     * @param flowChart - the flow chart instance
     * @param flowChartDOM - the DOM for restoring the flow chart instance
     * @return the restored flow chart instance
     * @see IDOM#resetDocument()
     */
    public static IFlowChart restoreFlowChartFromDOM(final IFlowChart flowChart, final IDOM flowChartDOM) {
        Objects.requireNonNull(flowChart);
        Objects.requireNonNull(flowChartDOM);
        if (flowChart.isDOMValid(flowChartDOM)) {
            flowChart.restoreFromDOM(flowChartDOM);
            IDOM.resetDocument();
            return flowChart;
        }
        return null;
    }
    
    /**
     * Loads a default connection with the given class name.
     * 
     * @param className - the class name
     * @return a default connection with the given class name or 
     * {@code null} if the class name is not a valid connection class name
     */
    public static IConnection loadConnection(final String className) {
        Objects.requireNonNull(className);
        try {
            if (className.equals(DefaultArrow.class.getName())) {
                Data data = new StringDataSet("");
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
    
    /**
     * Loads an {@link IDefaultArrow}.
     * 
     * @param id - the id
     * @param from - the from pin
     * @param to - the to pin
     * @param name - the name of the arrow
     * @return an {@link IDefaultArrow} with the given parameters
     * @throws IllegalArgumentException if an argument is invalid
     */
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
    
    /**
     * Loads an {@link IRigidConnection}.
     * 
     * @param id - the id
     * @param asFrom - the as from pin
     * @param asTo - the as to pin
     * @param name - the name of the connection
     * @return an {@link IRigidConnection} with the given parameters
     * @throws IllegalArgumentException if an argument is invalid
     */
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
    
    /**
     * Loads a graphical representation with the given class name and
     * a default pin if it is a {@link org.jojo.flow.model.flowChart.connections.OneConnectionGR}.
     * 
     * @param className - the class name
     * @return a fitting graphical representation or {@code null} if none is fitting
     * @see #loadGR(String, boolean)
     */
    public static IGraphicalRepresentation loadGR(final String className) {
        return loadGR(className, true);
    }
    
    /**
     * Loads a graphical representation with the given class name and the definition whether it has
     * a default pin if it is a {@link org.jojo.flow.model.flowChart.connections.OneConnectionGR}.
     * 
     * @param className - the class name
     * @param hasDefaultPin - whether the GR has
     * an default pin if it is a {@link org.jojo.flow.model.flowChart.connections.OneConnectionGR}
     * @return a fitting graphical representation or {@code null} if none is fitting
     */
    public static IGraphicalRepresentation loadGR(final String className, final boolean hasDefaultPin) {
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
    
    /**
     * Loads a module pin with the given class names for the pin and its imp. The pin is connected to a
     * new {@link MockModule} loaded by {@link #loadModule(String)}.
     * 
     * @param className - the class name
     * @param classNameImp - the imp's class name
     * @return a fitting module pin or {@code null} if none is fitting
     */
    public static IModulePin loadPin(final String className, final String classNameImp) {
        final IFlowModule mock = loadModule(MockModule.class.getName());
        return loadPin(className, classNameImp, mock);
    }
    
    /**
     * Loads a module pin with the given class names for the pin and its imp. The pin is connected to the
     * given flow module.
     * 
     * @param className - the class name
     * @param classNameImp - the imp's class name
     * @param module - the flow module to which this pin belongs
     * @return a fitting module pin or {@code null} if none is fitting
     * @see #loadRigidPin(FlowModule)
     */
    public static IModulePin loadPin(final String className, final String classNameImp, final IFlowModule module) {
        if (className.equals(InputPin.class.getName())) {
            if (classNameImp.equals(DefaultPin.class.getName())) {
                return new InputPin(new DefaultPin(module, new StringDataSet("")), (DefaultInputPinGR)loadGR(DefaultInputPinGR.class.getName()));
            } else if (classNameImp.equals(RigidPin.class.getName())) {
                final IRigidPin rigidPin = loadRigidPin(module);
                return rigidPin.getInputPin();
            }
        } else if (className.equals(OutputPin.class.getName())) {
            if (classNameImp.equals(DefaultPin.class.getName())) {
                return new OutputPin(new DefaultPin(module, new StringDataSet("")), (DefaultOutputPinGR)loadGR(DefaultOutputPinGR.class.getName()));
            } else if (classNameImp.equals(RigidPin.class.getName())) {
                final IRigidPin rigidPin = loadRigidPin(module);
                return rigidPin.getOutputPin();
            }
        }
        
        new Warning(null, "invalid pin classname= " + className, true).reportWarning();
        return null;
    }
    
    /**
     * Loads a rigid pin for the given flow module.
     * 
     * @param moduleArg - the given module
     * @return a rigid pin for the given flow module
     * @see #loadPin(String, String, FlowModule)
     */
    public static IRigidPin loadRigidPin(final IFlowModule moduleArg) {
        final IFlowModule mock = loadModule(MockModule.class.getName());
        final IFlowModule module = moduleArg == null ? mock : moduleArg;
        final RigidPinGR gr = (RigidPinGR)loadGR(RigidPinGR.class.getName());
        return new RigidPin(module, gr);
    }
    
    /**
     * Loads a flow module with ID {@code 0}
     * (using the class loader returned by {@code DynamicObjectLoader.class.getClassLoader()})
     * fitting the given parameters and containing a mock external config.
     * If it fails, {@code null} is returned and an error {@link org.jojo.flow.exc.Warning} is reported.
     * 
     * @param className - the flow module's class name
     * @return a flow module fitting the given parameters or {@code null} if loading fails
     * @see #loadModule(ClassLoader, String, int, String, int)
     */
    private static IFlowModule loadModule(final String className) {
        return loadModule(className, 0);
    }
    
    /**
     * Loads a flow module 
     * (using the class loader returned by {@code DynamicObjectLoader.class.getClassLoader()})
     * fitting the given parameters and containing a mock external config.
     * If it fails, {@code null} is returned and an error {@link org.jojo.flow.exc.Warning} is reported.
     * 
     * @param className - the flow module's class name
     * @param id - the id
     * @return a flow module fitting the given parameters or {@code null} if loading fails
     * @see #loadModule(ClassLoader, String, int, String, int)
     */
    public static IFlowModule loadModule(final String className, final int id) {
        return loadModule(DynamicObjectLoader.class.getClassLoader(), className, id);
    }
    
    /**
     * Loads a flow module fitting the given parameters and containing a mock external config.
     * If it fails, {@code null} is returned and an error {@link org.jojo.flow.exc.Warning} is reported.
     * 
     * @param classLoader - the class loader which should be used for loading the flow module's class
     * @param className - the flow module's class name
     * @param id - the id
     * @return a flow module fitting the given parameters or {@code null} if loading fails
     * @see #loadModule(ClassLoader, String, int, String, int)
     */
    public static IFlowModule loadModule(final ClassLoader classLoader, final String className, final int id) {
        final IExternalConfig config = getNewMockExternalConfig();
        return loadModule(classLoader, className, id, config.getName(), config.getPriority());
    }
    
    /**
     * Loads a flow module fitting the given parameters. If it fails, {@code null} is returned and
     * an error {@link org.jojo.flow.exc.Warning} is reported.
     * 
     * @param classLoader - the class loader which should be used for loading the flow module's class
     * @param className - the flow module's class name
     * @param id - the id
     * @param name - the name
     * @param priority - the priority
     * @return a flow module fitting the given parameters or {@code null} if loading fails
     * @see #load(ClassLoader, String, Class[], Object...)
     */
    public static IFlowModule loadModule(final ClassLoader classLoader, final String className, final int id,
            final String name, final int priority) {
        try {
            final IExternalConfig config = new ExternalConfig(name, priority);
            final Object modObj = load(classLoader, className, 
                    new Class<?>[] {int.class, ExternalConfig.class}, id, config);
            if (modObj instanceof FlowModule) {
                final FlowModule ret = (FlowModule)modObj;
                return ret;
            } else {
                new Warning(null, "loaded object is not an instance of ", true).reportWarning();
            }
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | 
                InstantiationException | IllegalAccessException | IllegalArgumentException | 
                InvocationTargetException e) {
            new Warning(null, e.toString(), true).reportWarning();
        }
        return null;
    }
    
    /**
     * Loads an object fitting the given parameters.
     * 
     * @param classLoader - the class loader for loading the object's class
     * @param className - the object's class name
     * @param parameterTypes - the parameter types of the constructor to be called
     * @param initArgs - the initial arguments passed to the constructor
     * @return an object fitting the given parameters
     * @throws ClassNotFoundException if a class is not found
     * @throws InvocationTargetException if the invoked constructor throws an exception
     * @throws IllegalAccessException if the access of the constructor is not possible
     * @throws InstantiationException  if a class cannot be instatiated
     * @throws SecurityException if the security manager does not allow the access
     * @throws NoSuchMethodException if a method was not found
     * @throws IllegalArgumentException if the number of actual and formal parameters differ; 
     * if an unwrapping conversion for primitive arguments fails; 
     * or if, after possible unwrapping, a parameter value cannot be converted to the corresponding 
     * formal parameter type by a method invocation conversion; if this constructor pertains to an 
     * enum type
     * @see Class#forName(String, boolean, ClassLoader)
     * @see Class#getConstructor(Class...)
     * @see java.lang.reflect.Constructor#newInstance(Object...)
     */
    public static Object load(final ClassLoader classLoader, final String className, 
            final Class<?>[] parameterTypes, final Object... initArgs) 
                    throws ClassNotFoundException, NoSuchMethodException, SecurityException, 
                    InstantiationException, IllegalAccessException, IllegalArgumentException, 
                    InvocationTargetException {
        final Class<?> moduleToLoadClass = Class.forName(className, true, classLoader);
        final var constr = moduleToLoadClass.getConstructor(parameterTypes);
        return constr.newInstance(initArgs);
    }
    
    /**
     * 
     * @return a new mock external config
     */
    public static IExternalConfig getNewMockExternalConfig() {
        return new ExternalConfig("NAME", 0);
    }
    
    /**
     * This class represents a mock flow module with two default module pins (one input, one output)
     * and two rigid pins.
     * 
     * @author Jonathan Schenkenberger
     * @version 1.0
     */
    public static class MockModule extends FlowModule {
        private final MockModuleGR gr;
        private IModulePin pinOut;
        private IModulePin pinIn;
        private List<IModulePin> rigidPins;
        
        /**
         * Creates a new mock module with the given ID and external config.
         * 
         * @param id - the given ID
         * @param externalConfig - the given external config
         */
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
                final IRigidPin rigidPinOne = loadRigidPin(this);
                ((ModulePinGR) rigidPinOne.getOutputPin().getGraphicalRepresentation()).setLinePoint(RIGID_ONE_POS);
                
                final IRigidPin rigidPinTwo = loadRigidPin(this);
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
            	new Warning(null, e.toString(), true).reportWarning();
                e.printStackTrace();
            }
            final IDefaultArrow ret = super.validate();
            try {
                ((DefaultPin)this.pinIn.getModulePinImp()).setCheckDataSignature(before);
            } catch (FlowException e) {
                // should not happen
            	new Warning(null, e.toString(), true).reportWarning();
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
        protected void setAllModulePins(final IDOM pinsDom) {
            if (isPinsDOMValid(pinsDom)) {
                if (this.rigidPins == null) {
                    this.rigidPins = new ArrayList<>();
                }
                this.rigidPins.clear();
                final Map<String, DOMStringUnion> domMap = pinsDom.getDOMMap();
                int i = 0;
                for(var pinObj : domMap.values()) {
                    if (pinObj.isDOM()) {
                        final IDOM pinDom = (IDOM) pinObj.getValue();
                        final IDOM pinCnDom = (IDOM)pinDom.getDOMMap().get(ModulePinDOM.NAME_CLASSNAME).getValue();
                        final String pinCn = pinCnDom.elemGet();
                        final IDOM pinCnDomImp = (IDOM)pinDom.getDOMMap().get(ModulePinDOM.NAME_CLASSNAME_IMP).getValue();
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
                                final Point thisLinePoint = PointDOM.pointOf((IDOM) ((IDOM) pinDom.getDOMMap()
                                        .get(GraphicalRepresentationDOM.NAME).getValue()).getDOMMap()
                                        .get("linePoint").getValue());
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
        protected boolean isPinsDOMValid(final IDOM pinsDom) {
            Objects.requireNonNull(pinsDom);
            final Map<String, DOMStringUnion> domMap = pinsDom.getDOMMap();
            try {
                int i = 0;
                for(var pinObj : domMap.values()) {
                    if (pinObj.isDOM()) {
                        ok(pinObj.isDOM(), OK.ERR_MSG_WRONG_CAST);
                        final IDOM pinDom = (IDOM) pinObj.getValue();
                        ok(pinDom.getDOMMap().get(ModulePinDOM.NAME_CLASSNAME).isDOM(), OK.ERR_MSG_WRONG_CAST);
                        final IDOM pinCnDom = (IDOM)pinDom.getDOMMap().get(ModulePinDOM.NAME_CLASSNAME).getValue();
                        final String pinCn = pinCnDom.elemGet();
                        ok(pinCn != null, OK.ERR_MSG_NULL);
                        ok(pinDom.getDOMMap().get(ModulePinDOM.NAME_CLASSNAME_IMP).isDOM(), OK.ERR_MSG_WRONG_CAST);
                        final IDOM pinCnDomImp = (IDOM)pinDom.getDOMMap().get(ModulePinDOM.NAME_CLASSNAME_IMP).getValue();
                        final String pinCnImp = pinCnDomImp.elemGet();
                        ok(pinCnImp != null, OK.ERR_MSG_NULL);
                        final IModulePin pin = ok(x -> loadPin(pinCn, pinCnImp, this), "");
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
        public void setInternalConfig(IDOM internalConfigDOM) {
            
        }
        
        @Override
        public boolean isInternalConfigDOMValid(IDOM internalConfigDOM) {
            return true;
        } 

        @Override
        public GraphicalRepresentation getGraphicalRepresentation() {
            return this.gr;
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
    
    /**
     * This class represents a graphical representation for the {@link MockModule}.
     * 
     * @author Jonathan Schenkenberger
     * @version 1.0
     */
    public static class MockModuleGR extends ModuleGR {
        
        /**
         * Creates a new mock module GR.
         * 
         * @param position - the position
         * @param height - the height
         * @param width - the width
         * @param iconText - the icon text
         */
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
