package org.jojo.flow.model.api;

import java.awt.Point;

import org.jojo.flow.model.flowChart.FlowChart;
import org.jojo.flow.model.flowChart.connections.ConnectionGR;
import org.jojo.flow.model.flowChart.modules.ModuleGR;

public interface IFlowChartGR extends IFlowChartElementGR {

    FlowChart getFlowChart();

    void addModule(ModuleGR moduleGR);

    void addConnection(ConnectionGR connectionGR);

    boolean removeModule(ModuleGR moduleGR);

    boolean removeModule(int index);

    boolean removeConnection(ConnectionGR connectionGR);

    boolean removeConnection(int index);

    Point getAbsOriginPoint();

    void setAbsOriginPoint(Point absOriginPoint);

    boolean isRasterEnabled();

    void setRasterEnabled(boolean isRasterEnabled);
}