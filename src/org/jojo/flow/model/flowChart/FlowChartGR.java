package org.jojo.flow.model.flowChart;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jojo.flow.model.flowChart.connections.ConnectionGR;
import org.jojo.flow.model.flowChart.modules.ModuleGR;

public class FlowChartGR extends FlowChartElementGR {
    private final FlowChart flowChart;
    
    private final List<ModuleGR> modules;
    private final List<ConnectionGR> connections;
    private Point absOriginPoint;
    private boolean isRasterEnabled;
    
    public FlowChartGR(final FlowChart flowChart) {
        super(new Point(0, 0), flowChart);
        this.flowChart = flowChart;
        this.modules = new ArrayList<>();
        this.connections = new ArrayList<>();
        this.absOriginPoint = new Point(0,0);
        this.isRasterEnabled = true;
    }

    public FlowChart getFlowChart() {
        return flowChart;
    }
    
    public void addModule(final ModuleGR moduleGR) {
        this.modules.add(moduleGR);
    }
    
    public void addConnection(final ConnectionGR connectionGR) {
        this.connections.add(connectionGR);
    }
    
    public boolean removeModule(final ModuleGR moduleGR) {
        return this.modules.remove(moduleGR);
    }
    
    public boolean removeModule(final int index) {
        if (index >= this.modules.size()) {
            return false;
        }
        
        this.modules.remove(index);
        return true;
    }
    
    public boolean removeConnection(final ConnectionGR connectionGR) {
        return this.connections.remove(connectionGR);
    }
    
    public boolean removeConnection(final int index) {
        if (index >= this.connections.size()) {
            return false;
        }
        
        this.connections.remove(index);
        return true;
    }
    
    @Override
    public int getHeigth() {
        final int modMax = Math.max(this.modules
                            .stream()
                            .map(m -> m.getCorners())
                            .flatMap(p -> Arrays.stream(p))
                            .mapToInt(p -> p.x)
                            .max().orElse(0),
                            this.modules
                            .stream()
                            .map(m -> m.getModule())
                            .map(m -> m.getAllModulePins())
                            .flatMap(p -> p.stream())
                            .map(p -> p.getGraphicalRepresentation())
                            .mapToInt(g -> g.getPosition().y + g.getHeigth())
                            .max().orElse(0));
        final int conMax = this.connections
                            .stream()
                            .mapToInt(c -> c.getHeigth())
                            .max().orElse(0);
        return Math.max(modMax, conMax);
    }
    
    @Override
    public int getWidth() {
        final int modMax = Math.max(this.modules
                .stream()
                .map(m -> m.getCorners())
                .flatMap(p -> Arrays.stream(p))
                .mapToInt(p -> p.y)
                .max().orElse(0),
                this.modules
                    .stream()
                    .map(m -> m.getModule())
                    .map(m -> m.getAllModulePins())
                    .flatMap(p -> p.stream())
                    .map(p -> p.getGraphicalRepresentation())
                    .mapToInt(g -> g.getPosition().x + g.getWidth())
                    .max().orElse(0));
        final int conMax = this.connections
                .stream()
                .mapToInt(c -> c.getWidth())
                .max().orElse(0);
        return Math.max(modMax, conMax);
    }

    public Point getAbsOriginPoint() {
        return absOriginPoint;
    }

    public void setAbsOriginPoint(Point absOriginPoint) {
        this.absOriginPoint = absOriginPoint;
    }

    public boolean isRasterEnabled() {
        return isRasterEnabled;
    }

    public void setRasterEnabled(boolean isRasterEnabled) {
        this.isRasterEnabled = isRasterEnabled;
    }
}
