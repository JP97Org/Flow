package org.jojo.flow.model.api;

import java.awt.Point;

import org.jojo.flow.model.flowChart.FlowChartGR;
import org.jojo.flow.model.util.DynamicObjectLoader;

public interface IFlowChartGR extends IFlowChartElementGR {
    public static IFlowChartGR getDefaultImplementation() {
        return (IFlowChartGR) DynamicObjectLoader.loadGR(FlowChartGR.class.getName());
    }

    IFlowChart getFlowChart();
    
    void setFlowChart(IFlowChart fc);

    void addModule(IModuleGR moduleGR);

    void addConnection(IConnectionGR connectionGR);

    boolean removeModule(IModuleGR moduleGR);

    boolean removeModule(int index);

    boolean removeConnection(IConnectionGR connectionGR);

    boolean removeConnection(int index);

    Point getAbsOriginPoint();

    void setAbsOriginPoint(Point absOriginPoint);

    boolean isRasterEnabled();

    void setRasterEnabled(boolean isRasterEnabled);
}