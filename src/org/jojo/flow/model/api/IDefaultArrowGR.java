package org.jojo.flow.model.api;

import java.awt.Shape;

public interface IDefaultArrowGR extends IConnectionGR {

    Shape getDefaultArrow();

    void setDefaultArrow(Shape defaultArrow);

    Shape getSelectedArrow();

    void setSelectedArrow(Shape selectedArrow);
}