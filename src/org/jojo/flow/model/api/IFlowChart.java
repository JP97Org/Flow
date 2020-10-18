package org.jojo.flow.model.api;

import java.util.List;

import org.jojo.flow.exc.ValidationException;

public interface IFlowChart extends IFlowChartElement {

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