package org.jojo.flow.model.api;

import java.awt.Point;
import java.util.List;

import org.jojo.flow.exc.ValidationException;
import org.jojo.flow.model.flowChart.FlowChart;
import org.jojo.flow.model.flowChart.FlowChartElement;
import org.jojo.flow.model.flowChart.connections.DefaultArrow;
import org.jojo.flow.model.flowChart.modules.InputPin;
import org.jojo.flow.model.flowChart.modules.OutputPin;
import org.jojo.flow.model.simulation.Simulation;
import org.jojo.flow.model.simulation.SimulationConfiguration;
import org.jojo.flow.model.storeLoad.DynamicClassLoader;
import org.jojo.flow.model.storeLoad.StoreLoadFacade;

public interface IModelFacade extends IAPI {

    FlowChartElement getFlowChartById(int id);

    FlowChartElement getElementById(int id);

    FlowChartElement getElementById(FlowChart flowChart, int id);

    FlowChart getMainFlowChart();

    List<FlowChart> getOtherFlowCharts();

    boolean addFlowChart(FlowChart flowChart);

    void setMainFlowChart(FlowChart flowChart);

    int nextFreeId();

    StoreLoadFacade getStoreLoad();

    Simulation getSimulation();

    Simulation getSimulation(FlowChart flowChart);

    Simulation getSimulation(FlowChart flowChart, SimulationConfiguration config);

    boolean addModule(DynamicClassLoader loader, String className, Point position);

    boolean addModule(FlowChart fc, DynamicClassLoader loader, String className, Point position);

    boolean removeModule(int id);

    boolean removeModule(FlowChart fc, int id);

    boolean connect(OutputPin from, InputPin to);

    boolean connect(FlowChart fc, OutputPin from, InputPin to);

    boolean removeConnection(int id);

    boolean removeConnection(FlowChart fc, int id);

    DefaultArrow validateFlowChart() throws ValidationException;

    DefaultArrow validateFlowChart(FlowChart fc) throws ValidationException;

}