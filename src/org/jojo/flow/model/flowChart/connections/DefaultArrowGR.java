package org.jojo.flow.model.flowChart.connections;

import static org.jojo.flow.model.storeLoad.OK.ok;

import java.awt.Point;
import java.awt.Shape;
import java.util.Map;
import java.util.Objects;

import org.jojo.flow.exc.ParsingException;
import org.jojo.flow.model.ModelFacade;
import org.jojo.flow.model.api.IDefaultArrowGR;
import org.jojo.flow.model.api.IModulePinGR;
import org.jojo.flow.model.flowChart.modules.DefaultInputPinGR;
import org.jojo.flow.model.flowChart.modules.DefaultOutputPinGR;
import org.jojo.flow.model.api.DOMStringUnion;
import org.jojo.flow.model.api.IDOM;
import org.jojo.flow.model.storeLoad.GraphicalRepresentationDOM;
import org.jojo.flow.model.storeLoad.OK;
import org.jojo.flow.model.util.DynamicObjectLoader;

public class DefaultArrowGR extends ConnectionGR implements IDefaultArrowGR {
    private Shape defaultArrow; //TODO evtl. anderer Typ jenachdem ob das so geht
    private Shape selectedArrow; //TODO evtl. anderer Typ jenachdem ob das so geht
    
    public DefaultArrowGR(final DefaultOutputPinGR fromPin, final DefaultInputPinGR toPin, final Shape defaultArrow) {
        super(fromPin, toPin);
        this.defaultArrow = defaultArrow; //TODO req. non-null sobald shape da ist
    }

    @Override
    public void addToPin(final Point diversionPoint, final IModulePinGR toPin) {
        Objects.requireNonNull(diversionPoint);
        Objects.requireNonNull(toPin);
        if (!(toPin instanceof DefaultInputPinGR)) {
            throw new IllegalArgumentException("to pin GR must be default input pin GR");
        }
        addConnection(new OneConnectionGR(getFromPin(), toPin));
    }

    @Override
    public Shape getDefaultArrow() {
        return this.defaultArrow;
    }

    @Override
    public void setDefaultArrow(final Shape defaultArrow) {
        this.defaultArrow = Objects.requireNonNull(defaultArrow);
        notifyObservers(defaultArrow);
    }

    @Override
    public Shape getSelectedArrow() {
        return this.selectedArrow;
    }

    @Override
    public void setSelectedArrow(final Shape selectedArrow) {
        this.selectedArrow = Objects.requireNonNull(selectedArrow);
        notifyObservers(selectedArrow);
    }

    @Override
    public IDOM getDOM() {
        final GraphicalRepresentationDOM dom = (GraphicalRepresentationDOM) super.getDOM();
        dom.appendCustomDOM("fromPin", getFromPin());
        dom.appendList("connections", getSingleConnections());
        dom.appendString("defaultArrow", "TODO"); //TODO class name of arrow for recreation of shape
        dom.appendString("selectedArrow", "TODO");//TODO class name of arrow for recreation of shape
        return dom;
    }

    @Override
    public void restoreFromDOM(final IDOM dom) {
        if (isDOMValid(dom)) {
            super.restoreFromDOM(dom);
            deleteAllConnections();
            final Map<String, DOMStringUnion> domMap = dom.getDOMMap();
            final IDOM fromPinDom = (IDOM)domMap.get("fromPin").getValue();
            final IDOM fromPinDomGr = (IDOM) fromPinDom.getDOMMap().get(GraphicalRepresentationDOM.NAME).getValue();
            final IDOM cnDom = (IDOM) fromPinDomGr.getDOMMap().get(GraphicalRepresentationDOM.NAME_CLASSNAME).getValue();
            final String cn = cnDom.elemGet();
            final DefaultOutputPinGR fromPin = (DefaultOutputPinGR)DynamicObjectLoader.loadGR(cn);
            fromPin.restoreFromDOM(fromPinDom);
            setFromPin(fromPin);
            final IDOM connectionsDom = (IDOM)domMap.get("connections").getValue();
            final Map<String, DOMStringUnion> connectionsMap = connectionsDom.getDOMMap();
            for (final var conObj : connectionsMap.values()) {
                if (conObj.isDOM()) {
                    final IDOM connectionDom = (IDOM)conObj.getValue();
                    final OneConnectionGR con = (OneConnectionGR) DynamicObjectLoader.loadGR(OneConnectionGR.class.getName());
                    con.restoreFromDOM(connectionDom);
                    addConnection(con);
                }
            }
            //TODO Arrow Shapes
            notifyObservers();
        }
    }
    
    @Override
    public boolean isDOMValid(final IDOM dom) {
        Objects.requireNonNull(dom);
        try {
            ok(super.isDOMValid(dom), "FCE_GR " + OK.ERR_MSG_DOM_NOT_VALID, (new ModelFacade()).getMainFlowChart());
            final Map<String, DOMStringUnion> domMap = dom.getDOMMap();
            ok(domMap.get("fromPin").isDOM(), OK.ERR_MSG_WRONG_CAST);
            final IDOM fromPinDom = (IDOM)domMap.get("fromPin").getValue();
            ok(fromPinDom.getDOMMap().get(GraphicalRepresentationDOM.NAME).isDOM(), OK.ERR_MSG_WRONG_CAST);
            final IDOM fromPinDomGr = (IDOM) fromPinDom.getDOMMap().get(GraphicalRepresentationDOM.NAME).getValue();
            ok(fromPinDomGr.getDOMMap().get(GraphicalRepresentationDOM.NAME_CLASSNAME).isDOM(), OK.ERR_MSG_WRONG_CAST);
            final IDOM cnDomFrom = (IDOM) fromPinDomGr.getDOMMap().get(GraphicalRepresentationDOM.NAME_CLASSNAME).getValue();
            final String cnFrom = cnDomFrom.elemGet();
            ok(cnFrom != null, OK.ERR_MSG_NULL);
            final DefaultOutputPinGR fromPin = ok(c -> (DefaultOutputPinGR) DynamicObjectLoader.loadGR(c), cnFrom);
            ok(fromPin.isDOMValid(fromPinDom), "FromPin " + OK.ERR_MSG_DOM_NOT_VALID);
            ok(domMap.get("connections").isDOM(), OK.ERR_MSG_WRONG_CAST);
            final IDOM connectionsDom = (IDOM)domMap.get("connections").getValue();
            final Map<String, DOMStringUnion> connectionsMap = connectionsDom.getDOMMap();
            for (final var conObj : connectionsMap.values()) {
                if (conObj.isDOM()) {
                    final IDOM connectionDom = (IDOM)conObj.getValue();
                    final OneConnectionGR con = ok(d -> (OneConnectionGR) DynamicObjectLoader.loadGR(OneConnectionGR.class.getName()), "");
                    ok(con.isDOMValid(connectionDom), "OneConnectionGR " + OK.ERR_MSG_DOM_NOT_VALID);
                    ok(con.getToPin() instanceof DefaultInputPinGR, OK.ERR_MSG_WRONG_CAST);
                    ok(isAddable(con), "OneConnectionGR " + con + " not addable");
                }
            }
            //TODO Arrow Shapes
            return true;
        } catch (ParsingException e) {
            e.getWarning().setAffectedElement((new ModelFacade()).getMainFlowChart()).reportWarning();
            return false;
        }
    }

}
