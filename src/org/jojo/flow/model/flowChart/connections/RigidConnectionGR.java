package org.jojo.flow.model.flowChart.connections;

import java.awt.Point;
import java.util.Objects;

import org.jojo.flow.model.flowChart.modules.ModulePinGR;
import org.jojo.flow.model.flowChart.modules.RigidPinGR;
import org.jojo.flow.model.storeLoad.DOM;
import org.jojo.flow.model.storeLoad.GraphicalRepresentationDOM;

public class RigidConnectionGR extends ConnectionGR {
    public RigidConnectionGR(final RigidPinGR asFromPin, final RigidPinGR asToPin) {
        super(asFromPin, asToPin);
    }

    @Override
    public void addToPin(final Point diversionPoint, final ModulePinGR toPin) {
        Objects.requireNonNull(diversionPoint);
        Objects.requireNonNull(toPin);
        if (!(toPin instanceof RigidPinGR)) {
            throw new IllegalArgumentException("to pin GR must be rigid pin GR");
        }
        addConnection(new OneConnectionGR(getFromPin(), toPin));
    }

    @Override
    public DOM getDOM() {
        final GraphicalRepresentationDOM dom = new GraphicalRepresentationDOM();
        dom.setClassName(getClass().getName());
        dom.setPosition(getPosition());
        dom.setHeight(getHeight());
        dom.setWidth(getWidth());
        dom.appendCustomDOM("fromPin", getFromPin());
        dom.appendList("connections", getSingleConnections());
        if (getLabel() != null) {
            dom.appendCustomDOM("label", getLabel());
        }
        return dom;
    }

    @Override
    public void restoreFromDOM(DOM dom) {
        // TODO Auto-generated method stub
        
    }
}
