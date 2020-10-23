package org.jojo.flow.model.api;

import java.awt.Point;

import javax.swing.Icon;

/**
 * This interface represents a super-interface for all graphical representations.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public interface IGraphicalRepresentation extends IDOMable {

    /**
     * Gets the position of this graphical representation. 
     * The position is usually defined as the Point of the most upper-left point of the respective
     * graphical representation, i.e. the point with the lowest x and the lowest y coordinate.
     * However, it may be that such a point does not exist or that for graphical representations for
     * certain elements this point is not meaningful and there may be other and better possibilities 
     * to define the position. In that case the position may be another more meaningful Point of the 
     * respective element. The respective view for this graphical representation must handle the 
     * position correctly as it is actually defined by the actual implementation of this method.
     * 
     * @return the position of this graphical representation which must not be {@code null}
     */
    Point getPosition();

    /**
     * Sets the position of this graphical representation.
     * 
     * @param position - the position to be set
     * @see #getPosition()
     */
    void setPosition(Point position);

    /**
     * Gets the height of this graphical representation. 
     * The height is usually defined as the difference between the highest and the lowest Point
     * of this graphical representation regarding the y coordinate. <br/><br/>
     * 
     * This is not the case is in IOneConnectionGR.
     * 
     * @return the height of this graphical representation
     * @see IOneConnectionGR#getHeight()
     */
    int getHeight();

    /**
     * Gets the width of this graphical representation. 
     * The width is usually defined as the difference between the highest and the lowest Point
     * of this graphical representation regarding the x coordinate. <br/><br/>
     * 
     * This is not the case is in IOneConnectionGR.
     * 
     * @return the width of this graphical representation
     * @see IOneConnectionGR#getWidth()
     */
    int getWidth();

    /**
     * Gets the default icon of this graphical representation. Once set it must not be {@code null} anymore.
     * 
     * @return the default icon of this graphical representation
     */
    Icon getDefaultIcon();

    /**
     * Sets the default icon of this graphical representation.
     * 
     * @param defaultIcon - the icon to be set (must not be {@code null})
     */
    void setDefaultIcon(Icon defaultIcon);

    /**
     * Gets the selected icon of this graphical representation. Once set it must not be {@code null} anymore.
     * 
     * @return the selected icon of this graphical representation
     */
    Icon getSelectedIcon();

    /**
     * Sets the selected icon of this graphical representation.
     * 
     * @param selectedIcon - the icon to be set (must not be {@code null})
     */
    void setSelectedIcon(Icon selectedIcon);
}