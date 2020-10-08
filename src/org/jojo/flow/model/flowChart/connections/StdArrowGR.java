package org.jojo.flow.model.flowChart.connections;

import java.awt.Point;
import java.awt.Shape;
import java.util.Objects;

import org.jojo.flow.model.flowChart.modules.ModulePinGR;
import org.jojo.flow.model.flowChart.modules.StdInputPinGR;
import org.jojo.flow.model.flowChart.modules.StdOutputPinGR;
import org.jojo.flow.model.storeLoad.DOM;
import org.jojo.flow.model.storeLoad.GraphicalRepresentationDOM;

public class StdArrowGR extends ConnectionGR {
    private Shape defaultArrow; //TODO evtl. anderer Typ jenachdem ob das so geht
    private Shape selectedArrow; //TODO evtl. anderer Typ jenachdem ob das so geht
    
    public StdArrowGR(final StdOutputPinGR fromPin, final StdInputPinGR toPin, final Shape defaultArrow) {
        super(fromPin, toPin);
        this.defaultArrow = defaultArrow;
    }

    @Override
    public void addToPin(final Point diversionPoint, final ModulePinGR toPin) {
        Objects.requireNonNull(diversionPoint);
        Objects.requireNonNull(toPin);
        if (!(toPin instanceof StdInputPinGR)) {
            throw new IllegalArgumentException("to pin GR must be std input pin GR");
        }
        addConnection(new OneConnectionGR(getFromPin(), toPin));
    }

    public Shape getDefaultArrow() {
        return this.defaultArrow;
    }

    public void setDefaultArrow(final Shape defaultArrow) {
        this.defaultArrow = Objects.requireNonNull(defaultArrow);
        notifyObservers(defaultArrow);
    }

    public Shape getSelectedArrow() {
        return this.selectedArrow;
    }

    public void setSelectedArrow(final Shape selectedArrow) {
        this.selectedArrow = Objects.requireNonNull(selectedArrow);
        notifyObservers(selectedArrow);
    }

    @Override
    public DOM getDOM() {
        final GraphicalRepresentationDOM dom = (GraphicalRepresentationDOM) super.getDOM();
        dom.appendCustomDOM("fromPin", getFromPin());
        dom.appendList("connections", getSingleConnections());
        dom.appendString("defaultArrow", "TODO"); //TODO class name of arrow for recreation of shape
        dom.appendString("selectedArrow", "TODO");//TODO class name of arrow for recreation of shape
        if (getLabel() != null) {
            dom.appendCustomDOM("label", getLabel());
        }
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
