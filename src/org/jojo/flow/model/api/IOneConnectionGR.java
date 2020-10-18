package org.jojo.flow.model.api;

import java.awt.Color;
import java.awt.Point;
import java.util.List;

public interface IOneConnectionGR extends IGraphicalRepresentation, IDOMable {

    void setToPin(Point diversionPoint, IModulePinGR toPin);

    List<IConnectionLineGR> getLines();

    List<Point> getDiversionPoints();

    boolean setPath(List<Point> diversionPoints);

    Color getColor();

    void setColor(Color color);

    IModulePinGR getFromPin();

    IModulePinGR getToPin();

}