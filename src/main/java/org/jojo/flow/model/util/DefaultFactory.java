package org.jojo.flow.model.util;

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

public class DefaultFactory extends AbstractFactory {
    public DefaultFactory() {
        super();
    }
    
    public void initialize() {
        clear();
        // Basic Types
        putImplementationMapping(IFraction.class, Fraction.class);
        // Data Signatures
        putImplementationMapping(IDataSignature.class, DataSignature.DontCareDataSignature.class);
        // Basic Checkables
        putImplementationMapping(IScalar.class, ScalarDataSet.class);
        putImplementationMapping(IStringData.class, StringDataSet.class);
        putImplementationMapping(IRaw.class, RawDataSet.class);
        putImplementationMapping(IMatrix.class, Matrix.class);
        putImplementationMapping(IMathMatrix.class, MathMatrix.class);
        putImplementationMapping(ITensor.class, Tensor.class);
        putImplementationMapping(IMultiMatrix.class, MultiMatrix.class);
        // Recursive Checkables
        putImplementationMapping(IDataArray.class, DataArray.class);
        putImplementationMapping(IDataVector.class, DataVector.class);
        putImplementationMapping(IDataBundle.class, DataBundle.class);
        // Facades
        putImplementationMapping(IModelFacade.class, ModelFacade.class);
        putImplementationMapping(IStoreLoadFacade.class, StoreLoadFacade.class);
        putImplementationMapping(ISimulation.class, Simulation.class);
        // FlowChart: FlowChart, MockModule, ExternalConfig (Modules, ModulePins, GRs, Connections done via DynamicObjectLoader)
        putImplementationMapping(IFlowChart.class, FlowChart.class);
        putImplementationMapping(IFlowModule.class, DynamicObjectLoader.MockModule.class);
        putImplementationMapping(IExternalConfig.class, ExternalConfig.class);
        // StoreLoad: DynamicClassLoader, ModuleClassesList, Document String
        putImplementationMapping(IDynamicClassLoader.class, DynamicClassLoader.class);
        putImplementationMapping(IModuleClassesList.class, ModuleClassesList.class);
        putImplementationMapping(IDocumentString.class, DocumentString.class);
        // Simulation: Config, Stepper, Scheduler
        putImplementationMapping(ISimulationConfiguration.class, SimulationConfiguration.class);
        putImplementationMapping(IMinimalStepper.class, SchedulingStepper.class);
        putImplementationMapping(IStepper.class, SchedulingStepper.class);
        putImplementationMapping(IScheduler.class, Scheduler.class);
    }
}
