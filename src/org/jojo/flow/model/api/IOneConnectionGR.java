package org.jojo.flow.model.api;

import java.awt.Color;
import java.awt.Point;
import java.util.List;

import org.jojo.flow.model.flowChart.connections.OneConnectionGR;
import org.jojo.flow.model.util.DynamicObjectLoader;

public interface IOneConnectionGR extends IGraphicalRepresentation, IDOMable {
    public static IOneConnectionGR getDefaultImplementation(final boolean hasDefaultPin) {
        return (IOneConnectionGR) DynamicObjectLoader.loadGR(OneConnectionGR.class.getName(), hasDefaultPin);
    }

    void setToPin(Point diversionPoint, IModulePinGR toPin);

    List<IConnectionLineGR> getLines();

    List<Point> getDiversionPoints();

    boolean setPath(List<Point> diversionPoints);

    Color getColor();

    void setColor(Color color);

    IModulePinGR getFromPin();

    IModulePinGR getToPin();

}