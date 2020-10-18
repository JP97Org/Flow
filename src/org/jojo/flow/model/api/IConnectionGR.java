package org.jojo.flow.model.api;

import java.awt.Color;
import java.awt.Point;
import java.util.List;

public interface IConnectionGR extends IFlowChartElementGR {

    void addToPin(Point diversionPoint, IModulePinGR toPin);

    boolean removeToPin(IModulePinGR toPin);

    IModulePinGR getFromPin();

    List<IModulePinGR> getToPins();

    List<IOneConnectionGR> getSingleConnections();

    void setColor(Color color);
}