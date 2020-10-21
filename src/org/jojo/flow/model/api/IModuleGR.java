package org.jojo.flow.model.api;

import java.awt.Point;
import java.awt.Window;

import org.jojo.flow.model.util.DynamicObjectLoader;
import org.jojo.flow.model.util.DynamicObjectLoader.MockModule;

/**
 * This interface represents a graphical representation for a flow module.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public interface IModuleGR extends IFlowChartElementGR {
    
    /**
     * Gets the default implementation (a "mock module" GR). However, this is not the usual way
     * to get a IModuleGR implementation, flow module designers should rather provide their own
     * implementation of this interface for the flow modules they develop.
     * 
     * @return the default implementation (a "mock module" GR)
     */
    public static IModuleGR getDefaultImplementation() {
        return (IModuleGR) DynamicObjectLoader.loadGR(DynamicObjectLoader.MockModuleGR.class.getName());
    }

    /**
     * Sets a "mock module". Note, that this method is rather for debugging purposes.
     * 
     * @param mock - the "mock module"
     */
    void setModuleMock(MockModule mock);

    /**
     * Gets the corners of the flow module in the following ordering: 
     * {&lt;^,^&gt;,v&gt;,&lt;v} , i.e. clockwise, starting upper-left.
     * 
     * @return the corners of the flow module
     * @see #rotateLeft()
     * @see #rotateRight()
     */
    Point[] getCorners();

    /**
     * Gets the info text of the flow module.
     * 
     * @return the info text of the flow module (must not be {@code null})
     */
    String getInfoText();

    /**
     * Gets the internal configuration window or {@code null} if no internal configuration exists.
     * 
     * @return the internal configuration window or {@code null} if no internal configuration exists
     */
    Window getInternalConfigWindow();

    /**
     * Gets the priority of the flow module.
     * 
     * @return the priority of the flow module
     * @see IExternalConfig#getPriority()
     */
    int getPriority();

    /**
     * Gets the scale of the flow module. Usually the default value for the scale is {@code 1.0}.
     * 
     * @return the scale of the flow module
     */
    double getScale();

    /**
     * Sets the scale to the given scale.
     * 
     * @param scale - the given scale
     * @throws IllegalArgumentException if scale is {@code <= 0}
     */
    void setScale(double scale);

    /**
     * Rotates this flow module's GR corner-based to the left.
     * 
     * @see #getCorners()
     * @see #rotateRight()
     */
    void rotateLeft();

    /**
     * Rotates this flow module's GR corner-based to the right.
     * 
     * @see #getCorners()
     * @see #rotateLeft()
     */
    void rotateRight();

    /**
     * Determines whether an icon text is allowed.
     * 
     * @return whether an icon text is allowed
     */
    boolean isIconTextAllowed();

    /**
     * Sets whether an icon text is allowed.
     * 
     * @param isIconTextAllowed - whether an icon text should be allowed
     */
    void setIsIconTextAllowed(boolean isIconTextAllowed);

    /**
     * Gets the icon text if existent.
     * 
     * @return the icon text if existent, otherwise {@code null}
     */
    String getIconText();
    
    /**
     * Sets the icon text to the given icon text.
     * 
     * @param iconText - the given icon text (may be {@code null} iff icon text is not allowed)
     * @throws IllegalArgumentException if icon text is tried to set to {@code null} but icon text is allowed
     * @see #isIconTextAllowed()
     */
    void setIconText(String iconText);

    /**
     * Gets the flow module to which this GR is attached.
     * 
     * @return the flow module to which this GR is attached (must usually not be {@code null})
     */
    IFlowModule getModule();
}