package org.jojo.flow.model.flowChart;

import java.awt.Point;

import javax.swing.Icon;

import org.jojo.flow.model.Subject;

public abstract class GraphicalRepresentation extends Subject {
    private Point position;
    private Icon defaultIcon;
    private Icon selectedIcon;
    private FlowChartGR flowChartGR;
    
    public GraphicalRepresentation(final Point position, final FlowChartGR flowChartGR) {
        this.setPosition(position);
        this.setFlowChartGR(flowChartGR);
    }

    public Point getPosition() {
        return this.position;
    }

    public void setPosition(final Point position) {
        this.position = position;
    }
    
    public abstract int getHeigth();
    public abstract int getWidth();

    public Icon getDefaultIcon() {
        return this.defaultIcon;
    }

    public void setDefaultIcon(final Icon defaultIcon) {
        this.defaultIcon = defaultIcon;
    }

    public Icon getSelectedIcon() {
        return this.selectedIcon;
    }

    public void setSelectedIcon(final Icon selectedIcon) {
        this.selectedIcon = selectedIcon;
    }

    public final FlowChartGR getFlowChartGR() {
        return this.flowChartGR;
    }

    public final void setFlowChartGR(final FlowChartGR flowChartGR) {
        this.flowChartGR = flowChartGR;
    }
}
