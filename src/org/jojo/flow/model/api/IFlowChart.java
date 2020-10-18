package org.jojo.flow.model.api;

import java.util.List;

import org.jojo.flow.exc.ValidationException;
import org.jojo.flow.model.flowChart.connections.Connection;
import org.jojo.flow.model.flowChart.connections.DefaultArrow;
import org.jojo.flow.model.flowChart.modules.FlowModule;

public interface IFlowChart extends IFlowChartElement {

    void addModule(FlowModule module);

    boolean addConnection(Connection connection);

    boolean removeModule(FlowModule module);

    boolean removeModule(int index);

    boolean removeConnection(Connection connection);

    boolean removeConnection(int index);

    List<FlowModule> getModules();

    List<Connection> getConnections();

    List<DefaultArrow> getArrows();

    DefaultArrow validate() throws ValidationException;
}