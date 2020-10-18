package org.jojo.flow.model.api;

import java.awt.Point;
import java.util.List;

import org.jojo.flow.exc.ValidationException;

public interface IModelFacade extends IAPI {
    public static IModelFacade getDefaultImplementation() {
        return (IModelFacade) IAPI.defaultImplementationOfThisApi(new Class<?>[] {});
    }
    
    public static IModelFacade getDefaultImplementation(final boolean isUsingOwnDynamicIdNameSpace) {
        return (IModelFacade) IAPI.defaultImplementationOfThisApi(new Class<?>[] {boolean.class}, isUsingOwnDynamicIdNameSpace);
    }

    IFlowChartElement getFlowChartById(int id);

    IFlowChartElement getElementById(int id);

    IFlowChartElement getElementById(IFlowChart flowChart, int id);

    IFlowChart getMainFlowChart();

    List<IFlowChart> getOtherFlowCharts();

    boolean addFlowChart(IFlowChart flowChart);

    void setMainFlowChart(IFlowChart flowChart);

    int nextFreeId();

    IStoreLoadFacade getStoreLoad();

    ISimulation getSimulation();

    ISimulation getSimulation(IFlowChart flowChart);

    ISimulation getSimulation(IFlowChart flowChart, ISimulationConfiguration config);

    boolean addModule(IDynamicClassLoader loader, String className, Point position);

    boolean addModule(IFlowChart fc, IDynamicClassLoader loader, String className, Point position);

    boolean removeModule(int id);

    boolean removeModule(IFlowChart fc, int id);

    boolean connect(IOutputPin from, IInputPin to);

    boolean connect(IFlowChart fc, IOutputPin from, IInputPin to);

    boolean removeConnection(int id);

    boolean removeConnection(IFlowChart fc, int id);

    IDefaultArrow validateFlowChart() throws ValidationException;

    IDefaultArrow validateFlowChart(IFlowChart fc) throws ValidationException;

}