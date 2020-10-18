package org.jojo.flow.model.api;

import java.util.List;

import org.jojo.flow.exc.ValidationException;
import org.jojo.flow.model.data.Fraction;
import org.jojo.flow.model.data.units.Frequency;
import org.jojo.flow.model.storeLoad.DOM;
import org.jojo.flow.model.util.DynamicObjectLoader;

public interface IFlowModule extends IFlowChartElement, Comparable<IFlowModule> {
    public static IFlowModule getDefaultImplementation(){
        return (IFlowModule) IAPI.defaultImplementationOfThisApi(new Class<?>[] {int.class, IExternalConfig.class},
                IModelFacade.getDefaultImplementation().nextFreeId(), IExternalConfig.getDefaultImplementation());
    }
    
    public static IFlowModule getDefaultImplementation(final ClassLoader classLoader, final String className,
            final String name, final int priority) {
        return DynamicObjectLoader.loadModule(classLoader, className, 
                IModelFacade.getDefaultImplementation().nextFreeId(), name, priority);
    }
    
    List<IModulePin> getAllModulePins();

    Frequency<Fraction> getFrequency();

    void run() throws Exception;

    IDefaultArrow validate() throws ValidationException;

    void setInternalConfig(DOM internalConfigDOM);

    boolean isInternalConfigDOMValid(DOM internalConfigDOM);

    IInternalConfig getInternalConfig();

    boolean hasInternalConfig();

    IExternalConfig getExternalConfig();

    List<IInputPin> getDefaultInputs();

    List<IOutputPin> getDefaultOutputs();

    List<IInputPin> getAllInputs();

    List<IOutputPin> getAllOutputs();

    List<IFlowModule> getDefaultDependencyList();

    List<IFlowModule> getDefaultAdjacencyList();
}