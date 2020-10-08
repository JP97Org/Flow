package org.jojo.flow.model.flowChart.modules;

import java.awt.Point;
import java.util.Objects;

import org.jojo.flow.model.flowChart.GraphicalRepresentation;

public abstract class ModulePinGR extends GraphicalRepresentation {
    private final int height;
    private final int width;
    
    private boolean isIconTextAllowed;
    private String iconText;
    
    private Point linePoint;
    private PinOrientation pinOrientation;
    
    public ModulePinGR(final Point position, final String iconText,
            final int height, final int width) {
        super(position);
        this.height = height;
        this.width = width;
        setIconTextAllowed(iconText != null);
        setIconText(iconText);
        setLinePoint(position);
    }
    
    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    public boolean isIconTextAllowed() {
        return this.isIconTextAllowed;
    }

    public void setIconTextAllowed(final boolean isIconTextAllowed) {
        this.isIconTextAllowed = isIconTextAllowed;
        notifyObservers(isIconTextAllowed);
    }

    public String getIconText() {
        return this.iconText;
    }

    public void setIconText(final String iconText) {
        this.iconText = iconText;
        notifyObservers(iconText);
    }

    public Point getLinePoint() {
        return this.linePoint;
    }

    public void setLinePoint(final Point linePoint) {
        this.linePoint = Objects.requireNonNull(linePoint);
        notifyObservers(linePoint);
    }

    public PinOrientation getPinOrientation() {
        return this.pinOrientation;
    }

    public void setPinOrientation(PinOrientation pinOrientation) {
        this.pinOrientation = Objects.requireNonNull(pinOrientation);
        notifyObservers(pinOrientation);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(getClass(), this.linePoint);
    }
    
    @Override
    public boolean equals(final Object other) {
        if (getClass().equals(other.getClass())) {
            return this.linePoint.equals(((ModulePinGR)other).linePoint);
        }
        return false;
    }
}
