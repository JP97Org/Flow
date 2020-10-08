package org.jojo.flow.model.flowChart.connections;

import java.awt.Point;
import java.util.Objects;

import org.jojo.flow.model.Warning;
import org.jojo.flow.model.flowChart.FlowChartElement;
import org.jojo.flow.model.flowChart.GraphicalRepresentation;
import org.jojo.flow.model.storeLoad.DOM;
import org.jojo.flow.model.storeLoad.GraphicalRepresentationDOM;

public class ConnectionLineGR extends GraphicalRepresentation {
    private Point positionB;
    
    public ConnectionLineGR(final Point positionA, final Point positionB) {
        super(positionA);
        this.setPositionB(positionB);
        if (!isLine(positionA, positionB)) {
            final Warning warning = FlowChartElement.GENERIC_ERROR_ELEMENT.getLastWarning();
            throw new IllegalArgumentException(warning.getDescription());
        }
    }
    
    public static boolean isLine(final Point positionA, final Point positionB) {
        if (positionA.x != positionB.x && positionA.y != positionB.y) {
            new Warning(null, "A and B are not on a line", false).reportWarning();
            return false;
        } else if (positionA.equals(positionB)) {
            new Warning(null, "A and B are the same point", false).reportWarning();
            return false;
        }
        return true;
    }
    
    public Point getPositionA() {
        return getPosition();
    }

    public Point getPositionB() {
        return this.positionB;
    }
    
    public void setPositionA(final Point positionA) {
        setPosition(positionA);
    }

    public void setPositionB(final Point positionB) {
        this.positionB = Objects.requireNonNull(positionB);
        notifyObservers(positionB);
    }

    @Override
    public int getHeight() {
        return Math.abs(getPositionA().x - getPositionB().x);
    }

    @Override
    public int getWidth() {
        return Math.abs(getPositionA().y - getPositionB().y);
    }

    @Override
    public DOM getDOM() {
        final GraphicalRepresentationDOM dom = (GraphicalRepresentationDOM) super.getDOM();
        dom.appendCustomPoint("positionB", getPositionB());
        return dom;
    }

    @Override
    public void restoreFromDOM(DOM dom) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean isDOMValid(DOM dom) {
        // TODO Auto-generated method stub
        return true;
    }
}
