package org.jojo.flow.model.api;

import java.awt.Shape;

import org.jojo.flow.model.flowChart.connections.DefaultArrowGR;
import org.jojo.flow.model.util.DynamicObjectLoader;

public interface IDefaultArrowGR extends IConnectionGR {
    public static IDefaultArrowGR getDefaultImplementation() {
        return (IDefaultArrowGR) DynamicObjectLoader.loadGR(DefaultArrowGR.class.getName());
    }

    Shape getDefaultArrow();

    void setDefaultArrow(Shape defaultArrow);

    Shape getSelectedArrow();

    void setSelectedArrow(Shape selectedArrow);
}