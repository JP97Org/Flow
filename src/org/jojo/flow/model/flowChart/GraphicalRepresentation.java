package org.jojo.flow.model.flowChart;

import java.awt.Point;
import java.util.Objects;

import javax.swing.Icon;

import org.jojo.flow.model.Subject;
import org.jojo.flow.model.storeLoad.DOM;
import org.jojo.flow.model.storeLoad.DOMable;

public abstract class GraphicalRepresentation extends Subject implements DOMable {
    private Point position;
    private Icon defaultIcon;
    private Icon selectedIcon;
    private FlowChartGR flowChartGR;
    
    public GraphicalRepresentation(final Point position, final FlowChartGR flowChartGR) {
        this.setPosition(position);
        this.setFlowChartGR(flowChartGR);
    }
    
    @Override
    public abstract DOM getDOM();

    public Point getPosition() {
        return this.position;
    }

    public void setPosition(final Point position) {
        this.position = Objects.requireNonNull(position);
        notifyObservers(position);
    }
    
    public abstract int getHeigth();
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

    public final FlowChartGR getFlowChartGR() {
        return this.flowChartGR;
    }

    public final void setFlowChartGR(final FlowChartGR flowChartGR) {
        this.flowChartGR = Objects.requireNonNull(flowChartGR);
        notifyObservers(flowChartGR);
    }
}
