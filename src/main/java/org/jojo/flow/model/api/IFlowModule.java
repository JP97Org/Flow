package org.jojo.flow.model.api;

import java.util.List;

import org.jojo.flow.exc.ValidationException;
import org.jojo.flow.model.data.Fraction;
import org.jojo.flow.model.data.units.Frequency;
import org.jojo.flow.model.flowChart.modules.FlowModule;
import org.jojo.flow.model.util.DynamicObjectLoader;

/**
 * This interface represents a flow module, i.e. a module on the flow chart which can receive input,
 * calculate and then put output on possibly connected outgoing connections. Note that designers of new 
 * flow module classes should not directly implement this interface but extend a default or other abstract
 * implementation of this interface because there are some important implementations already done.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public interface IFlowModule extends IFlowChartElement, Comparable<IFlowModule> {
    
    /**
     * Gets the default abstract class.
     * 
     * @return the default abstract class
     */
    public static Class<? extends IFlowModule> getDefaultAbstractClass() {
        return FlowModule.class;
    }
    
    /**
     * Gets a default mock implementation ("mock-module").
     * 
     * @return a default mock implementation ("mock-module")
     * @see #getDefaultImplementation(ClassLoader, String, String, int)
     */
    public static IFlowModule getDefaultImplementation(){
        return (IFlowModule) IAPI.defaultImplementationOfThisApi(new Class<?>[] {int.class, IExternalConfig.class},
                IModelFacade.getDefaultImplementation().nextFreeId(), IExternalConfig.getDefaultImplementation());
    }
    
    /**
     * Gets a default implementation of a flow module specified by the parameters. Note that all parameters
     * should be correct, otherwise an {@link java.lang.reflect.InvocationTargetException} or another exception
     * may be thrown.
     * 
     * @param classLoader - the class loader to load the class(es) related to the flow module's class with
     * @param className - the name of the flow module's class
     * @param name - the desired name of the flow module
     * @param priority - the desired priority of the flow module
     * @return a default implementation of a flow module
     */
    public static IFlowModule getDefaultImplementation(final ClassLoader classLoader, final String className,
            final String name, final int priority) {
        return DynamicObjectLoader.loadModule(classLoader, className, 
                IModelFacade.getDefaultImplementation().nextFreeId(), name, priority);
    }
    
    /**
     * Gets all module pins of this flow module.
     * 
     * @return all module pins of this flow module
     */
    List<IModulePin> getAllModulePins();

    /**
     * Gets the frequency of this flow module. 
     * The flow module caller should use this frequency to call the module's {@link #run()} method.
     * 
     * @return the frequency of this flow module
     */
    Frequency<Fraction> getFrequency();

    /**
     * Performs the flow modules calculations, including input processing and output putting.
     * 
     * @throws Exception if an exception occurs during calculation
     */
    void run() throws Exception;

    /**
     * Validates this flow module’s default input pins which must have completely checking data signatures
     * for using the default implementation of the validate method.
     * Thereafter, it puts correct complete-does-care default data (data signature completely checking)
     * on all its default output pins. Returns a faulty {@link IDefaultArrow} if any exists, otherwise returns
     * {@code null}.
     * 
     * @return {@code null} if this flow module is valid, otherwise the first found invalid arrow
     * @throws ValidationException if the flow module is not programmed correctly
     * @see IDataSignature#isCheckingRecursive()
     * @see IFlowChart#validate()
     */
    IDefaultArrow validate() throws ValidationException;

    /**
     * Sets the internal config via a IDOM.
     * 
     * @param internalConfigDOM - the internal config IDOM
     * @see #getInternalConfig()
     */
    void setInternalConfig(IDOM internalConfigDOM);

    /**
     * Determines whether the given internal config IDOM is valid.
     * 
     * @param internalConfigDOM - the given internal config IDOM
     * @return whether the given internal config IDOM is valid
     */
    boolean isInternalConfigDOMValid(IDOM internalConfigDOM);

    /**
     * Gets the internal config if existent.
     * 
     * @return the internal config if existent, otherwise {@code null}
     * @see #hasInternalConfig()
     */
    IInternalConfig getInternalConfig();

    /**
     * Determines whether this flow module has an internal config.
     * 
     * @return whether this flow module has an internal config
     */
    boolean hasInternalConfig();

    /**
     * Gets the the external config of the flow module.
     * 
     * @return the external config of the flow module (must not be {@code null})
     */
    IExternalConfig getExternalConfig();

    /**
     * Gets all default input pins.
     * 
     * @return all default input pins
     */
    List<IInputPin> getDefaultInputs();

    /**
     * Gets all default output pins.
     * 
     * @return all default output pins
     */
    List<IOutputPin> getDefaultOutputs();

    /**
     * Gets all input pins.
     * 
     * @return all input pins
     */
    List<IInputPin> getAllInputs();

    /**
     * Gets all output pins.
     * 
     * @return all output pins
     */
    List<IOutputPin> getAllOutputs();

    /**
     * Gets all modules which are directly connected with an {@link IDefaultArrow}
     * to one of this module’s default input pins.
     * 
     * @return all modules which are directly connected with an {@link IDefaultArrow}
     * to one of this module’s default input pins
     */
    List<IFlowModule> getDefaultDependencyList();

    /**
     * Gets all modules which are directly connected with an {@link IDefaultArrow}
     * to one of this module’s default output pins.
     * 
     * @return all modules which are directly connected with an {@link IDefaultArrow}
     * to one of this module’s default output pins
     */
    List<IFlowModule> getDefaultAdjacencyList();
}