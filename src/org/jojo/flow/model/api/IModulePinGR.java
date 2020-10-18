package org.jojo.flow.model.api;

import java.awt.Point;

public interface IModulePinGR extends IGraphicalRepresentation {

    boolean isIconTextAllowed();

    void setIconTextAllowed(boolean isIconTextAllowed);

    String getIconText();

    void setIconText(String iconText);

    Point getLinePoint();

    void setLinePoint(Point linePoint);

    PinOrientation getPinOrientation();

    void setPinOrientation(PinOrientation pinOrientation);

}