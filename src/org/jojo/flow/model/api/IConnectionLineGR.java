package org.jojo.flow.model.api;

import java.awt.Point;

import org.jojo.flow.model.flowChart.connections.ConnectionLineGR;
import org.jojo.flow.model.util.DynamicObjectLoader;

public interface IConnectionLineGR extends IGraphicalRepresentation {
    public static IConnectionLineGR getDefaultImplementation() {
        return (IConnectionLineGR) DynamicObjectLoader.loadGR(ConnectionLineGR.class.getName());
    }

    Point getPositionA();

    Point getPositionB();

    void setPositionA(Point positionA);

    void setPositionB(Point positionB);
}