package org.jojo.flow.model.api;

import java.awt.Point;

/**
 * This interface represents a super-interface for all graphical representations of module pins.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public interface IModulePinGR extends IGraphicalRepresentation {

    /**
     * Determines whether an icon text is allowed instead of an actual icon.
     * 
     * @return whether an icon text is allowed instead of an actual icon
     */
    boolean isIconTextAllowed();

    /**
     * Sets whether an icon text is allowed instead of an actual icon.
     * 
     * @param isIconTextAllowed - whether an icon text should be allowed instead of an actual icon
     */
    void setIconTextAllowed(boolean isIconTextAllowed);

    /**
     * Gets the icon text.
     * 
     * @return the icon text (may be {@code null})
     */
    String getIconText();

    /**
     * Sets the given icon text.
     * 
     * @param iconText - the icon text (may be {@code null})
     */
    void setIconText(String iconText);

    /**
     * Gets the line point of this pin, i.e. the point where a connection may begin or end.
     * 
     * @return the line point of this pin
     */
    Point getLinePoint();
 
    /**
     * Sets the line point of this pin.
     * 
     * @param linePoint - the given line point (must not be {@code null})
     * @see #getLinePoint()
     */
    void setLinePoint(Point linePoint);

    /**
     * Gets the pin orientation of this pin.
     * 
     * @return the pin orientation of this pin
     * @see PinOrientation
     */
    PinOrientation getPinOrientation();

    /**
     * Sets the pin orientation of this pin to the given orientation.
     * 
     * @param pinOrientation - the given orientation
     * @see PinOrientation
     */
    void setPinOrientation(PinOrientation pinOrientation);
}