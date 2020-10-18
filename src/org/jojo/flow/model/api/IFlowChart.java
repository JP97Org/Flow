package org.jojo.flow.model.api;

import java.util.List;

import org.jojo.flow.exc.ValidationException;
import org.jojo.flow.model.flowChart.FlowChartGR;
import org.jojo.flow.model.util.DynamicObjectLoader;

public interface IFlowChart extends IFlowChartElement {
    public static IFlowChart getDefaultImplementation() {
        return (IFlowChart) IAPI.defaultImplementationOfThisApi(
                new Class<?>[] {int.class, IFlowChartGR.class}, 
                IModelFacade.getDefaultImplementation().nextFreeId(), 
                DynamicObjectLoader.loadGR(FlowChartGR.class.getName()));
    }

    void addModule(IFlowModule module);

    boolean addConnection(IConnection connection);

    boolean removeModule(IFlowModule module);

    boolean removeModule(int index);

    boolean removeConnection(IConnection connection);

    boolean removeConnection(int index);

    List<IFlowModule> getModules();

    List<IConnection> getConnections();

    List<IDefaultArrow> getArrows();

    IDefaultArrow validate() throws ValidationException;
}