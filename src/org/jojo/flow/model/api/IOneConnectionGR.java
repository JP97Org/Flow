package org.jojo.flow.model.api;

import java.awt.Color;
import java.awt.Point;
import java.util.List;

import org.jojo.flow.model.flowChart.modules.ModulePinGR;

public interface IOneConnectionGR extends IGraphicalRepresentation, IDOMable {

    void setToPin(Point diversionPoint, ModulePinGR toPin);

    List<IConnectionLineGR> getLines();

    List<Point> getDiversionPoints();

    boolean setPath(List<Point> diversionPoints);

    Color getColor();

    void setColor(Color color);

    ModulePinGR getFromPin();

    ModulePinGR getToPin();

}