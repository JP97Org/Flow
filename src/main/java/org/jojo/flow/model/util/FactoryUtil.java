package org.jojo.flow.model.util;

import java.util.HashMap;
import java.util.Map;

import org.jojo.flow.exc.Warning;
import org.jojo.flow.model.ModelFacade;
import org.jojo.flow.model.api.IDataArray;
import org.jojo.flow.model.api.IDataBundle;
import org.jojo.flow.model.api.IDataSignature;
import org.jojo.flow.model.api.IDataVector;
import org.jojo.flow.model.api.IDocumentString;
import org.jojo.flow.model.api.IDynamicClassLoader;
import org.jojo.flow.model.api.IExternalConfig;
import org.jojo.flow.model.api.IFlowChart;
import org.jojo.flow.model.api.IFlowModule;
import org.jojo.flow.model.api.IFraction;
import org.jojo.flow.model.api.IMathMatrix;
import org.jojo.flow.model.api.IMatrix;
import org.jojo.flow.model.api.IMinimalStepper;
import org.jojo.flow.model.api.IModelFacade;
import org.jojo.flow.model.api.IModuleClassesList;
import org.jojo.flow.model.api.IMultiMatrix;
import org.jojo.flow.model.api.IRaw;
import org.jojo.flow.model.api.IScalar;
import org.jojo.flow.model.api.IScheduler;
import org.jojo.flow.model.api.ISimulation;
import org.jojo.flow.model.api.ISimulationConfiguration;
import org.jojo.flow.model.api.IStepper;
import org.jojo.flow.model.api.IStoreLoadFacade;
import org.jojo.flow.model.api.IStringData;
import org.jojo.flow.model.api.ITensor;
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

/**
 * This utility class can be used by factory implementations or default implementation getters
 * in order to find mappings from {@link org.jojo.flow.model.api.IAPI} interfaces to respective
 * default implementations.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public final class FactoryUtil {
    private FactoryUtil() {
        
    }
    
    /**
     * A Map which maps the API interfaces to respective default implementation classes.
     */
    private static final Map<Class<?>, Class<?>> apiToDefaultImplementationMap = new HashMap<>();
    
    /**
     * Sets the default mappings for the API interfaces to default implementations map.
     */
    public static void initialize() {
        apiToDefaultImplementationMap.clear();
        // Basic Types
        putDefaultImplementationMapping(IFraction.class, Fraction.class);
        // Data Signatures
        putDefaultImplementationMapping(IDataSignature.class, DataSignature.DontCareDataSignature.class);
        // Basic Checkables
        putDefaultImplementationMapping(IScalar.class, ScalarDataSet.class);
        putDefaultImplementationMapping(IStringData.class, StringDataSet.class);
        putDefaultImplementationMapping(IRaw.class, RawDataSet.class);
        putDefaultImplementationMapping(IMatrix.class, Matrix.class);
        putDefaultImplementationMapping(IMathMatrix.class, MathMatrix.class);
        putDefaultImplementationMapping(ITensor.class, Tensor.class);
        putDefaultImplementationMapping(IMultiMatrix.class, MultiMatrix.class);
        // Recursive Checkables
        putDefaultImplementationMapping(IDataArray.class, DataArray.class);
        putDefaultImplementationMapping(IDataVector.class, DataVector.class);
        putDefaultImplementationMapping(IDataBundle.class, DataBundle.class);
        // Facades
        putDefaultImplementationMapping(IModelFacade.class, ModelFacade.class);
        putDefaultImplementationMapping(IStoreLoadFacade.class, StoreLoadFacade.class);
        putDefaultImplementationMapping(ISimulation.class, Simulation.class);
        // FlowChart: FlowChart, MockModule, ExternalConfig (Modules, ModulePins, GRs, Connections done via DynamicObjectLoader)
        putDefaultImplementationMapping(IFlowChart.class, FlowChart.class);
        putDefaultImplementationMapping(IFlowModule.class, DynamicObjectLoader.MockModule.class);
        putDefaultImplementationMapping(IExternalConfig.class, ExternalConfig.class);
        // StoreLoad: DynamicClassLoader, ModuleClassesList, Document String
        putDefaultImplementationMapping(IDynamicClassLoader.class, DynamicClassLoader.class);
        putDefaultImplementationMapping(IModuleClassesList.class, ModuleClassesList.class);
        putDefaultImplementationMapping(IDocumentString.class, DocumentString.class);
        // Simulation: Config, Stepper, Scheduler
        putDefaultImplementationMapping(ISimulationConfiguration.class, SimulationConfiguration.class);
        putDefaultImplementationMapping(IMinimalStepper.class, SchedulingStepper.class);
        putDefaultImplementationMapping(IStepper.class, SchedulingStepper.class);
        putDefaultImplementationMapping(IScheduler.class, Scheduler.class);
    }
    
    /**
     * 
     * @return the api to default implementations map
     */
    public static Map<Class<?>, Class<?>> getApiToDefaultImplementationMap() {
        return new HashMap<>(apiToDefaultImplementationMap);
    }
    
    /**
     * Puts the given mapping. If it does not succeed an error {@link org.jojo.flow.exc.Warning} is 
     * reported.
     * 
     * @param key - the {@link org.jojo.flow.model.api.IAPI} interface
     * @param value - the respective default implementation to be put
     * @return whether the putting was successful, i.e. the key is assignable from the value
     */
    public static boolean putDefaultImplementationMapping(final Class<?> key, final Class<?> value) {
        if (key != null && value != null && key.isAssignableFrom(value)) {
            apiToDefaultImplementationMap.put(key, value);
            return true;
        }
        new Warning(null, "default implementation cannot be set: key is not assignable from value. key= " 
                            + key + " | value= " + value, true).reportWarning();
        return false;
    }
}
