package org.jojo.flow.model.api;

import java.awt.Point;

public interface IConnectionLineGR extends IGraphicalRepresentation {

    Point getPositionA();

    Point getPositionB();

    void setPositionA(Point positionA);

    void setPositionB(Point positionB);
}