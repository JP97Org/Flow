package org.jojo.flow.model.api;

import java.awt.Point;

import javax.swing.Icon;

public interface IGraphicalRepresentation extends IDOMable {

    Point getPosition();

    void setPosition(Point position);

    int getHeight();

    int getWidth();

    Icon getDefaultIcon();

    void setDefaultIcon(Icon defaultIcon);

    Icon getSelectedIcon();

    void setSelectedIcon(Icon selectedIcon);
}