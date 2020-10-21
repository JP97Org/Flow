package org.jojo.flow.model.api;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.jojo.flow.model.ModelFacade;
import org.jojo.flow.model.data.DataArray;
import org.jojo.flow.model.data.DataBundle;
import org.jojo.flow.model.data.DataSignature;
import org.jojo.flow.model.data.DataVector;
import org.jojo.flow.model.data.Fraction;
import org.jojo.flow.model.data.MathMatrix;
import org.jojo.flow.model.data.Matrix;
import org.jojo.flow.model.data.MultiMatrix;
import org.jojo.flow.model.data.RawDataSet;
import org.jojo.flow.model.data.ScalarDataSet;
import org.jojo.flow.model.data.StringDataSet;
import org.jojo.flow.model.data.Tensor;
import org.jojo.flow.model.flowChart.FlowChart;
import org.jojo.flow.model.flowChart.modules.ExternalConfig;
import org.jojo.flow.model.simulation.Scheduler;
import org.jojo.flow.model.simulation.SchedulingStepper;
import org.jojo.flow.model.simulation.Simulation;
import org.jojo.flow.model.simulation.SimulationConfiguration;
import org.jojo.flow.model.storeLoad.DocumentString;
import org.jojo.flow.model.storeLoad.DynamicClassLoader;
import org.jojo.flow.model.storeLoad.ModuleClassesList;
import org.jojo.flow.model.storeLoad.StoreLoadFacade;
import org.jojo.flow.model.util.DynamicObjectLoader;

/**
 * The main interface of the Flow Model API. All interfaces of the API should extend this interface.
 * It declares no methods but provides a static API initializer and a default implementation getter
 * which can be used from sub-interfaces to get respective default implementations.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public interface IAPI {
    /**
     * A Map which maps the API interfaces to respective default implementation classes.
     */
    static final Map<Class<?>, Class<?>> apiToDefaultImplementationMap = new HashMap<>();
    
    /**
     * Initializes the API, i.e. sets the mappings for the API interfaces to default implementations map.
     */
    static void initialize() { 
        // Basic Types
        apiToDefaultImplementationMap.put(IFraction.class, Fraction.class);
        // Data Signatures
        apiToDefaultImplementationMap.put(IDataSignature.class, DataSignature.DontCareDataSignature.class);
        // Basic Checkables
        apiToDefaultImplementationMap.put(IScalar.class, ScalarDataSet.class);
        apiToDefaultImplementationMap.put(IStringData.class, StringDataSet.class);
        apiToDefaultImplementationMap.put(IRaw.class, RawDataSet.class);
        apiToDefaultImplementationMap.put(IMatrix.class, Matrix.class);
        apiToDefaultImplementationMap.put(IMathMatrix.class, MathMatrix.class);
        apiToDefaultImplementationMap.put(ITensor.class, Tensor.class);
        apiToDefaultImplementationMap.put(IMultiMatrix.class, MultiMatrix.class);
        // Recursive Checkables
        apiToDefaultImplementationMap.put(IDataArray.class, DataArray.class);
        apiToDefaultImplementationMap.put(IDataVector.class, DataVector.class);
        apiToDefaultImplementationMap.put(IDataBundle.class, DataBundle.class);
        // Facades
        apiToDefaultImplementationMap.put(IModelFacade.class, ModelFacade.class);
        apiToDefaultImplementationMap.put(IStoreLoadFacade.class, StoreLoadFacade.class);
        apiToDefaultImplementationMap.put(ISimulation.class, Simulation.class);
        // FlowChart: FlowChart, MockModule, ExternalConfig (Modules, ModulePins, GRs, Connections done via DynamicObjectLoader)
        apiToDefaultImplementationMap.put(IFlowChart.class, FlowChart.class);
        apiToDefaultImplementationMap.put(IFlowModule.class, DynamicObjectLoader.MockModule.class);
        apiToDefaultImplementationMap.put(IExternalConfig.class, ExternalConfig.class);
        // StoreLoad: DynamicClassLoader, ModuleClassesList, Document String
        apiToDefaultImplementationMap.put(IDynamicClassLoader.class, DynamicClassLoader.class);
        apiToDefaultImplementationMap.put(IModuleClassesList.class, ModuleClassesList.class);
        apiToDefaultImplementationMap.put(IDocumentString.class, DocumentString.class);
        // Simulation: Config, Stepper, Scheduler
        apiToDefaultImplementationMap.put(ISimulationConfiguration.class, SimulationConfiguration.class);
        apiToDefaultImplementationMap.put(IMinimalStepper.class, SchedulingStepper.class);
        apiToDefaultImplementationMap.put(IStepper.class, SchedulingStepper.class);
        apiToDefaultImplementationMap.put(IScheduler.class, Scheduler.class);
    }
    
    /**
     * Gets the default implementation of the calling sub-interface of IAPI.
     * This method should only directly be called from a sub-interface of IAPI.
     * 
     * @param parameterTypes - the parameter types for the constructor to call
     * @param initArgs - the initial arguments for the constructor to call
     * @return a default implementation of the calling IAPI sub-interface created with the given initial arguments
     */
    static IAPI defaultImplementationOfThisApi(final Class<?>[] parameterTypes, final Object... initArgs) {
        final String iClassName;
        try { 
            throw new RuntimeException();
        } catch (RuntimeException e) {
            iClassName = e.getStackTrace()[1].getClassName();
        }
        try {
            final Class<?> iClass = Class.forName(iClassName);
            final Class<?> implClass = apiToDefaultImplementationMap.get(iClass);
            if (implClass != null) {
                final Object o = DynamicObjectLoader.load(IAPI.class.getClassLoader(), implClass.getName(), 
                            parameterTypes, initArgs);
                if (IAPI.class.isInstance(o) && implClass.isInstance(o)) {
                    return (IAPI) o;
                } else {
                    System.err.println("wrong API to default impl. mapping: " + iClass + " -> " + implClass);
                }
            }
        } catch (final ClassNotFoundException | NoSuchMethodException | SecurityException | 
                InstantiationException | IllegalAccessException | IllegalArgumentException | 
                InvocationTargetException e) {
            // should not happen
            e.printStackTrace();
        }
        return null;
    }
}
