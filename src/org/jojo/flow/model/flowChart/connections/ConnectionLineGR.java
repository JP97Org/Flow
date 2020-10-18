package org.jojo.flow.model.flowChart.connections;

import static org.jojo.flow.model.storeLoad.OK.ok;

import java.awt.Point;
import java.util.Map;
import java.util.Objects;

import org.jojo.flow.model.ModelFacade;
import org.jojo.flow.model.Warning;
import org.jojo.flow.model.api.IConnectionLineGR;
import org.jojo.flow.model.flowChart.FlowChartElement;
import org.jojo.flow.model.flowChart.GraphicalRepresentation;
import org.jojo.flow.model.storeLoad.DOM;
import org.jojo.flow.model.storeLoad.GraphicalRepresentationDOM;
import org.jojo.flow.model.storeLoad.OK;
import org.jojo.flow.model.storeLoad.ParsingException;
import org.jojo.flow.model.storeLoad.PointDOM;

public class ConnectionLineGR extends GraphicalRepresentation implements IConnectionLineGR {
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
    
    @Override
    public Point getPositionA() {
        return getPosition();
    }

    @Override
    public Point getPositionB() {
        return this.positionB;
    }
    
    @Override
    public void setPositionA(final Point positionA) {
        setPosition(positionA);
    }

    @Override
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
        if (isDOMValid(dom)) {
            super.restoreFromDOM(dom);
            final Map<String, Object> domMap = dom.getDOMMap();
            final DOM posBDom = (DOM)domMap.get("positionB");
            this.positionB = PointDOM.pointOf(posBDom);
            notifyObservers();
        }
    }

    @Override
    public boolean isDOMValid(DOM dom) {
        Objects.requireNonNull(dom);
        try {
            ok(super.isDOMValid(dom), "GR " + OK.ERR_MSG_DOM_NOT_VALID, (new ModelFacade()).getMainFlowChart());
            final Map<String, Object> domMap = dom.getDOMMap();
            ok(domMap.get("positionB") instanceof DOM, OK.ERR_MSG_WRONG_CAST);
            final DOM posBDom = (DOM)domMap.get("positionB");
            ok(d -> PointDOM.pointOf(d), posBDom);
            return true;
        } catch (ParsingException e) {
            e.getWarning().setAffectedElement((new ModelFacade()).getMainFlowChart()).reportWarning();
            return false;
        }
    }
}
