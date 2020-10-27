package org.jojo.flow.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.jojo.flow.exc.ConnectionException;
import org.jojo.flow.exc.ValidationException;
import org.jojo.flow.exc.Warning;
import org.jojo.flow.model.api.IConnection;
import org.jojo.flow.model.api.IDefaultArrow;
import org.jojo.flow.model.api.IDefaultPin;
import org.jojo.flow.model.api.IDynamicClassLoader;
import org.jojo.flow.model.api.IFlowChart;
import org.jojo.flow.model.api.IFlowChartElement;
import org.jojo.flow.model.api.IFlowModule;
import org.jojo.flow.model.api.IInputPin;
import org.jojo.flow.model.api.IModelFacade;
import org.jojo.flow.model.api.IOutputPin;
import org.jojo.flow.model.api.ISimulation;
import org.jojo.flow.model.api.ISimulationConfiguration;
import org.jojo.flow.model.api.IStoreLoadFacade;
import org.jojo.flow.model.data.units.Time;
import org.jojo.flow.model.flowChart.FlowChart;
import org.jojo.flow.model.flowChart.FlowChartElement;
import org.jojo.flow.model.flowChart.connections.Connection;
import org.jojo.flow.model.flowChart.connections.DefaultArrow;
import org.jojo.flow.model.flowChart.connections.RigidConnection;
import org.jojo.flow.model.flowChart.modules.FlowModule;
import org.jojo.flow.model.simulation.Simulation;
import org.jojo.flow.model.simulation.SimulationConfiguration;
import org.jojo.flow.model.storeLoad.StoreLoadFacade;
import org.jojo.flow.model.util.DynamicObjectLoader;

public class ModelFacade implements IModelFacade {
    private static int idCounter = 0;
    
    private static IFlowChart mainFlowChart = (IFlowChart) IFlowChartElement.GENERIC_ERROR_ELEMENT; // ussually set to one with id 0
    
    private final boolean isUsingOwnDynamicIdNameSpace;
    private int dynamicIdCounter;
    
    private IFlowChart dynamicMainFlowChart;
    private static final List<IFlowChart> otherFlowCharts = new ArrayList<>();

    private final List<IFlowChart> dynamicOtherFlowCharts;
    
    private static final Time<Double> DEFAULT_TIMEOUT = Time.getDoubleConstant(60.);
    
    public ModelFacade() {
        this(false);
    }
    
    public ModelFacade(final boolean isUsingOwnDynamicIdNameSpace) {
        this.isUsingOwnDynamicIdNameSpace = isUsingOwnDynamicIdNameSpace;
        this.dynamicIdCounter = isUsingOwnDynamicIdNameSpace ? 0 : idCounter;
        this.dynamicMainFlowChart = isUsingOwnDynamicIdNameSpace? (FlowChart) FlowChartElement.GENERIC_ERROR_ELEMENT : mainFlowChart;
        this.dynamicOtherFlowCharts = isUsingOwnDynamicIdNameSpace ? new ArrayList<>() : otherFlowCharts;
    }
    
    @Override
    public synchronized IFlowChart getFlowChartById(final int id) {
        if (id == getMainFlowChart().getId()) {
            return getMainFlowChart();
        }
        return getOtherFlowCharts().stream().filter(f -> id == f.getId()).findFirst().orElse(null);
    }
    
    @Override
    public synchronized IFlowChartElement getElementById(final int id) {
        return getElementById(getMainFlowChart(), id);
    }
    
    @Override
    public synchronized IFlowChartElement getElementById(final IFlowChart flowChart, final int id) {
        Objects.requireNonNull(flowChart);
        if (flowChart.getConnections().stream().anyMatch(x -> x.getId() == id)) {
            return flowChart.getConnections()
                    .stream()
                    .filter(x -> x.getId() == id)
                    .findFirst().orElse(null);
        } else if (flowChart.getModules().stream().anyMatch(x -> x.getId() == id)) {
            return flowChart.getModules()
                    .stream()
                    .filter(x -> x.getId() == id)
                    .findFirst().orElse(null);
        }
        
        return flowChart.getId() == id ? flowChart : null;
    }

    @Override
    public synchronized IFlowChart getMainFlowChart() {
        if (!this.isUsingOwnDynamicIdNameSpace) {
            this.dynamicMainFlowChart = mainFlowChart;
        }
        return this.dynamicMainFlowChart;
    }
    
    @Override
    public synchronized List<IFlowChart> getOtherFlowCharts() {
        return this.dynamicOtherFlowCharts;
    }
    
    @Override
    public synchronized boolean addFlowChart(final IFlowChart flowChart) {
        Objects.requireNonNull(flowChart);
        if (this.dynamicOtherFlowCharts.contains(flowChart)) {
            return false;
        }
        this.dynamicOtherFlowCharts.add(flowChart);
        return true;
    }
    
    @Override
    public synchronized void setMainFlowChart(final IFlowChart flowChart) {
        if (this.isUsingOwnDynamicIdNameSpace) {
            this.dynamicMainFlowChart = Objects.requireNonNull(flowChart);
        } else {
            mainFlowChart = Objects.requireNonNull(flowChart);
            this.dynamicMainFlowChart = mainFlowChart;
        }
    }
    
    @Override
    public synchronized int nextFreeId() {
        return getAndIncCounter();
    }

    private int getAndIncCounter() {
        if (this.isUsingOwnDynamicIdNameSpace) {
            this.dynamicIdCounter++;
        } else {
            idCounter++;
            this.dynamicIdCounter = idCounter;
        }
        return this.dynamicIdCounter;
    }
    
    @Override
    public synchronized IStoreLoadFacade getStoreLoad() {
        return new StoreLoadFacade();
    }
    
    @Override
    public synchronized ISimulation getSimulation() {
        return getSimulation(getMainFlowChart());
    }
    
    @Override
    public synchronized ISimulation getSimulation(final IFlowChart flowChart) {
        return getSimulation(flowChart, new SimulationConfiguration(DEFAULT_TIMEOUT));
    }
    
    @Override
    public synchronized ISimulation getSimulation(final IFlowChart flowChart, final ISimulationConfiguration config) {
        return new Simulation(flowChart, config);
    }
    
    @Override
    public synchronized boolean addModule(final IDynamicClassLoader loader, final String className, final Point position) {
        return addModule(getMainFlowChart(), loader, className, position);
    }
    
    @Override
    public synchronized boolean addModule(final IFlowChart fc, final IDynamicClassLoader loader, final String className, final Point position) {
        Objects.requireNonNull(fc);
        Objects.requireNonNull(loader);
        Objects.requireNonNull(className);
        Objects.requireNonNull(position);
        final int id = nextFreeId();
        final IFlowModule toAdd = DynamicObjectLoader.loadModule(loader.getClassLoader(), className, id);
        final boolean checked = checkModuleCorrectness(fc, toAdd, position);
        if (checked) {
            toAdd.getGraphicalRepresentation().setPosition(position);
            fc.addModule(toAdd);
        }
        return checked; 
    }
    
    private boolean checkModuleCorrectness(final IFlowChart fc, final IFlowModule toAdd, final Point position) {
        if (toAdd == null) {
            return false;
        }
        return fc.getModules().stream().allMatch(m -> !m.getGraphicalRepresentation().getPosition().equals(position));
    }

    @Override
    public synchronized boolean removeModule(final int id) {
        return removeModule(getMainFlowChart(), id);
    }
    
    @Override
    public synchronized boolean removeModule(final IFlowChart fc, final int id) {
        Objects.requireNonNull(fc);
        final IFlowChartElement elem = getElementById(fc, id);
        final FlowModule mod = elem instanceof FlowModule ? (FlowModule)elem : null;
        return fc.removeModule(mod);
    }
    
    @Override
    public synchronized boolean connect(final IOutputPin from, final IInputPin to) {
        return connect(getMainFlowChart(), from, to);
    }
    
    @Override
    public synchronized boolean connect(final IFlowChart fc, final IOutputPin from, final IInputPin to) {
        Objects.requireNonNull(fc);
        Objects.requireNonNull(from);
        Objects.requireNonNull(to);
        final IConnection con = from.getModulePinImp() instanceof IDefaultPin 
                ? DynamicObjectLoader.loadConnection(DefaultArrow.class.getName()) 
                        : DynamicObjectLoader.loadConnection(RigidConnection.class.getName()) ;
        con.removeToPin(con.getToPins().get(0));
        try {
            con.setFromPin(from);
            final boolean ok = con.addToPin(to);
            return ok && fc.addConnection(con);
        } catch (ConnectionException e) {
            new Warning(null, e.toString(), true);
            return false;
        }
    }
    
    @Override
    public synchronized boolean removeConnection(final int id) {
        return removeConnection(getMainFlowChart(), id);
    }
    
    @Override
    public synchronized boolean removeConnection(final IFlowChart fc, final int id) {
        Objects.requireNonNull(fc);
        final IFlowChartElement elem = getElementById(fc, id);
        final Connection con = elem instanceof Connection ? (Connection)elem : null;
        return fc.removeConnection(con);
    }
    
    @Override
    public synchronized IDefaultArrow validateFlowChart() throws ValidationException {
        return validateFlowChart(getMainFlowChart());
    }
    
    @Override
    public synchronized IDefaultArrow validateFlowChart(final IFlowChart fc) throws ValidationException {
        Objects.requireNonNull(fc);
        return fc.validate();
    }
}
