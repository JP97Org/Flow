package org.jojo.flow.model.flowChart.connections;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.jojo.flow.model.Warning;
import org.jojo.flow.model.flowChart.FlowChartGR;
import org.jojo.flow.model.flowChart.GraphicalRepresentation;
import org.jojo.flow.model.flowChart.modules.ModulePinGR;
import org.jojo.flow.model.storeLoad.DOM;

import static org.jojo.flow.model.flowChart.connections.ConnectionLineGR.isLine;

public class OneConnectionGR extends GraphicalRepresentation {
    private final ModulePinGR fromPin; // output pin
    private ModulePinGR toPin; // input pin
    
    private final List<ConnectionLineGR> lines;
    private final List<Point> diversionPoints;
    private Color color;
    
    public OneConnectionGR(final ModulePinGR fromPin, final ModulePinGR toPin, final FlowChartGR flowChart) { 
        super(fromPin.getPosition(), flowChart);
        this.fromPin = Objects.requireNonNull(fromPin);
        Objects.requireNonNull(toPin);
        this.lines = new ArrayList<>();
        this.diversionPoints = new ArrayList<>();
        setColor(Color.BLACK);
        setToPin(new Point(fromPin.getPosition().x, toPin.getPosition().y), Objects.requireNonNull(toPin));
    }

    public void setToPin(final Point diversionPoint, final ModulePinGR toPin) {
        Objects.requireNonNull(diversionPoint);
        Objects.requireNonNull(toPin);
        setColor(Color.BLACK);
        this.toPin = toPin;
        setPath(Arrays.asList(diversionPoint), true);
        notifyObservers();
    }
    
    public List<ConnectionLineGR> getLines() {
        return new ArrayList<>(this.lines);
    }
 
    public List<Point> getDiversionPoints() {
        return new ArrayList<>(this.diversionPoints);
    }
    
    public boolean setPath(final List<Point> diversionPoints) {
        Objects.requireNonNull(diversionPoints);
        boolean ret = (!diversionPoints.isEmpty()) 
                        || (diversionPoints.isEmpty() 
                                && isLine(this.fromPin.getLinePoint(), this.toPin.getLinePoint()));
        
        for (int i = 0; ret && i < diversionPoints.size(); i++) {
            final Point divPointBefore = i == 0 ? this.fromPin.getLinePoint() : diversionPoints.get(i - 1);
            final Point divPoint = diversionPoints.get(i);

            ret &= isLine(divPointBefore, divPoint);
            if (i == diversionPoints.size() - 1) {
                ret &= isLine(divPoint, this.toPin.getLinePoint());
            }
        }
        
        setPath(diversionPoints, ret);
        return ret;
    }

    private void setPath(final List<Point> diversionPoints, final boolean ok) {
        if (ok) {
            this.lines.clear();
            this.diversionPoints.clear();
            Point divPointBefore = this.fromPin.getPosition();
            for (final Point diversionPoint : diversionPoints) {
                this.lines.add(new ConnectionLineGR(divPointBefore, diversionPoint, getFlowChartGR()));
                divPointBefore = diversionPoint;
            }
            this.lines.add(new ConnectionLineGR(divPointBefore, this.toPin.getPosition(), getFlowChartGR()));
            this.diversionPoints.addAll(diversionPoints);
            notifyObservers(getLines());
        } else {
            new Warning(getFlowChartGR().getFlowChart(), "path of connection could not be set", false).reportWarning();
        }
    }

    @Override
    public int getHeigth() { //TODO hier ist heigth der maximale x punkt der verbindung
        return this.diversionPoints.stream().mapToInt(p -> p.x).max().orElse(0);
    }

    @Override
    public int getWidth() { //TODO hier ist width der maximale y punkt der verbindung
        return this.diversionPoints.stream().mapToInt(p -> p.y).max().orElse(0);
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(final Color color) {
        this.color = Objects.requireNonNull(color);
        notifyObservers(color);
    }
    
    public ModulePinGR getFromPin() {
        return this.fromPin;
    }

    public ModulePinGR getToPin() {
        return this.toPin;
    }

    @Override
    public DOM getDOM() {
        // TODO Auto-generated method stub
        return null;
    }
}
