package org.jojo.flow.model.flowChart;

import java.awt.Point;
import java.util.Objects;

import javax.swing.Icon;

import org.jojo.flow.model.Subject;
import org.jojo.flow.model.storeLoad.DOMable;

public abstract class GraphicalRepresentation extends Subject implements DOMable {
    private Point position;
    private Icon defaultIcon;
    private Icon selectedIcon;
    
    public GraphicalRepresentation(final Point position) {
        this.setPosition(position);
    }

    public Point getPosition() {
        return this.position;
    }

    public void setPosition(final Point position) {
        this.position = Objects.requireNonNull(position);
        notifyObservers(position);
    }
    
    public abstract int getHeight();
    public abstract int getWidth();

    public Icon getDefaultIcon() {
        return this.defaultIcon;
    }

    public void setDefaultIcon(final Icon defaultIcon) {
        this.defaultIcon = Objects.requireNonNull(defaultIcon);
        notifyObservers(defaultIcon);
    }

    public Icon getSelectedIcon() {
        return this.selectedIcon;
    }

    public void setSelectedIcon(final Icon selectedIcon) {
        this.selectedIcon = Objects.requireNonNull(selectedIcon);
        notifyObservers(selectedIcon);
    }
}
