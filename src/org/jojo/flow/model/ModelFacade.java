package org.jojo.flow.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.jojo.flow.exc.ConnectionException;
import org.jojo.flow.exc.ValidationException;
import org.jojo.flow.exc.Warning;
import org.jojo.flow.model.api.IModelFacade;
import org.jojo.flow.model.data.units.Time;
import org.jojo.flow.model.flowChart.FlowChart;
import org.jojo.flow.model.flowChart.FlowChartElement;
import org.jojo.flow.model.flowChart.connections.Connection;
import org.jojo.flow.model.flowChart.connections.DefaultArrow;
import org.jojo.flow.model.flowChart.connections.RigidConnection;
import org.jojo.flow.model.flowChart.modules.DefaultPin;
import org.jojo.flow.model.flowChart.modules.FlowModule;
import org.jojo.flow.model.flowChart.modules.InputPin;
import org.jojo.flow.model.flowChart.modules.OutputPin;
import org.jojo.flow.model.simulation.Simulation;
import org.jojo.flow.model.simulation.SimulationConfiguration;
import org.jojo.flow.model.storeLoad.DynamicClassLoader;
import org.jojo.flow.model.storeLoad.DynamicObjectLoader;
import org.jojo.flow.model.storeLoad.StoreLoadFacade;

public class ModelFacade implements IModelFacade {
    private static int idCounter = 0;
    
    private static FlowChart mainFlowChart = (FlowChart) FlowChartElement.GENERIC_ERROR_ELEMENT; // ussually set to one with id 0
    
    private final boolean isUsingOwnDynamicIdNameSpace;
    private int dynamicIdCounter;
    
    private FlowChart dynamicMainFlowChart;
    private static final List<FlowChart> otherFlowCharts = new ArrayList<>();

    private final List<FlowChart> dynamicOtherFlowCharts;
    
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
    public synchronized FlowChartElement getFlowChartById(final int id) {
        if (id == getMainFlowChart().getId()) {
            return getMainFlowChart();
        }
        return getOtherFlowCharts().stream().filter(f -> id == f.getId()).findFirst().orElse(null);
    }
    
    @Override
    public synchronized FlowChartElement getElementById(final int id) {
        return getElementById(getMainFlowChart(), id);
    }
    
    @Override
    public synchronized FlowChartElement getElementById(final FlowChart flowChart, final int id) {
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
    public synchronized FlowChart getMainFlowChart() {
        if (!this.isUsingOwnDynamicIdNameSpace) {
            this.dynamicMainFlowChart = mainFlowChart;
        }
        return this.dynamicMainFlowChart;
    }
    
    @Override
    public synchronized List<FlowChart> getOtherFlowCharts() {
        return this.dynamicOtherFlowCharts;
    }
    
    @Override
    public synchronized boolean addFlowChart(final FlowChart flowChart) {
        Objects.requireNonNull(flowChart);
        if (this.dynamicOtherFlowCharts.contains(flowChart)) {
            return false;
        }
        this.dynamicOtherFlowCharts.add(flowChart);
        return true;
    }
    
    @Override
    public synchronized void setMainFlowChart(final FlowChart flowChart) {
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
    public synchronized StoreLoadFacade getStoreLoad() {
        return new StoreLoadFacade();
    }
    
    @Override
    public synchronized Simulation getSimulation() {
        return getSimulation(getMainFlowChart());
    }
    
    @Override
    public synchronized Simulation getSimulation(final FlowChart flowChart) {
        return getSimulation(flowChart, new SimulationConfiguration(DEFAULT_TIMEOUT));
    }
    
    @Override
    public synchronized Simulation getSimulation(final FlowChart flowChart, final SimulationConfiguration config) {
        return new Simulation(flowChart, config);
    }
    
    @Override
    public synchronized boolean addModule(final DynamicClassLoader loader, final String className, final Point position) {
        return addModule(getMainFlowChart(), loader, className, position);
    }
    
    @Override
    public synchronized boolean addModule(final FlowChart fc, final DynamicClassLoader loader, final String className, final Point position) {
        Objects.requireNonNull(fc);
        Objects.requireNonNull(loader);
        Objects.requireNonNull(className);
        Objects.requireNonNull(position);
        final int id = nextFreeId();
        final FlowModule toAdd = DynamicObjectLoader.loadModule(loader, className, id);
        final boolean checked = checkModuleCorrectness(fc, toAdd, position);
        if (checked) {
            toAdd.getGraphicalRepresentation().setPosition(position);
            fc.addModule(toAdd);
        }
        return checked; 
    }
    
    private boolean checkModuleCorrectness(final FlowChart fc, final FlowModule toAdd, final Point position) {
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
    public synchronized boolean removeModule(final FlowChart fc, final int id) {
        Objects.requireNonNull(fc);
        final FlowChartElement elem = getElementById(fc, id);
        final FlowModule mod = elem instanceof FlowModule ? (FlowModule)elem : null;
        return fc.removeModule(mod);
    }
    
    @Override
    public synchronized boolean connect(final OutputPin from, final InputPin to) {
        return connect(getMainFlowChart(), from, to);
    }
    
    @Override
    public synchronized boolean connect(final FlowChart fc, final OutputPin from, final InputPin to) {
        Objects.requireNonNull(fc);
        Objects.requireNonNull(from);
        Objects.requireNonNull(to);
        final Connection con = from.getModulePinImp() instanceof DefaultPin 
                ? DynamicObjectLoader.loadConnection(DefaultArrow.class.getName()) 
                        : DynamicObjectLoader.loadConnection(RigidConnection.class.getName()) ;
        con.removeToPin(0);
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
    public synchronized boolean removeConnection(final FlowChart fc, final int id) {
        Objects.requireNonNull(fc);
        final FlowChartElement elem = getElementById(fc, id);
        final Connection con = elem instanceof Connection ? (Connection)elem : null;
        return fc.removeConnection(con);
    }
    
    @Override
    public synchronized DefaultArrow validateFlowChart() throws ValidationException {
        return validateFlowChart(getMainFlowChart());
    }
    
    @Override
    public synchronized DefaultArrow validateFlowChart(final FlowChart fc) throws ValidationException {
        Objects.requireNonNull(fc);
        return fc.validate();
    }
}
