package org.jojo.flow.model.api;

import java.awt.Color;
import java.awt.Point;
import java.util.List;

import org.jojo.flow.model.flowChart.modules.ModulePinGR;

public interface IConnectionGR extends IFlowChartElementGR {

    void addToPin(Point diversionPoint, ModulePinGR toPin);

    boolean removeToPin(ModulePinGR toPin);

    ModulePinGR getFromPin();

    List<ModulePinGR> getToPins();

    List<IOneConnectionGR> getSingleConnections();

    void setColor(Color color);
}