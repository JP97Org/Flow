package org.jojo.flow.model.api;

import java.awt.Point;
import java.awt.Window;

import org.jojo.flow.model.util.DynamicObjectLoader;
import org.jojo.flow.model.util.DynamicObjectLoader.MockModule;

public interface IModuleGR extends IFlowChartElementGR {
    public static IModuleGR getDefaultImplementation() {
        return (IModuleGR) DynamicObjectLoader.loadGR(DynamicObjectLoader.MockModuleGR.class.getName());
    }

    void setModuleMock(MockModule mock);

    Point[] getCorners();

    String getInfoText();

    Window getInternalConfigWindow();

    int getPriority();

    double getScale();

    void setScale(double scale);

    void rotateLeft();

    void rotateRight();

    boolean isIconTextAllowed();

    void setIsIconTextAllowed(boolean isIconTextAllowed);

    void setIconText(String iconText);

    IFlowModule getModule();
}