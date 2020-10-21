package org.jojo.flow.model.flowChart.connections;

import static org.jojo.flow.model.storeLoad.OK.ok;

import java.awt.Point;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.jojo.flow.exc.ParsingException;
import org.jojo.flow.model.ModelFacade;
import org.jojo.flow.model.api.IModulePinGR;
import org.jojo.flow.model.api.IOneConnectionGR;
import org.jojo.flow.model.api.IRigidConnectionGR;
import org.jojo.flow.model.flowChart.modules.RigidPinGR;
import org.jojo.flow.model.api.IDOM;
import org.jojo.flow.model.storeLoad.GraphicalRepresentationDOM;
import org.jojo.flow.model.storeLoad.OK;
import org.jojo.flow.model.util.DynamicObjectLoader;

public class RigidConnectionGR extends ConnectionGR implements IRigidConnectionGR {
    public RigidConnectionGR(final RigidPinGR asFromPin, final RigidPinGR asToPin) {
        super(asFromPin, asToPin);
    }

    @Override
    public void addToPin(final Point diversionPoint, final IModulePinGR toPin) {
        Objects.requireNonNull(diversionPoint);
        Objects.requireNonNull(toPin);
        if (!(toPin instanceof RigidPinGR)) {
            throw new IllegalArgumentException("to pin GR must be rigid pin GR");
        }
        addConnection(new OneConnectionGR(getFromPin(), toPin));
    }

    @Override
    public IDOM getDOM() {
        final GraphicalRepresentationDOM dom = (GraphicalRepresentationDOM) super.getDOM();
        dom.appendCustomDOM("fromPin", getFromPin());
        dom.appendList("connections", getSingleConnections());
        return dom;
    }

    @Override
    public void restoreFromDOM(final IDOM dom) {
        if (isDOMValid(dom)) {
            super.restoreFromDOM(dom);
            deleteAllConnections();
            final Map<String, Object> domMap = dom.getDOMMap();
            final IDOM fromPinDom = (IDOM)domMap.get("fromPin");
            final IDOM fromPinDomGr = (IDOM) fromPinDom.getDOMMap().get(GraphicalRepresentationDOM.NAME);
            final IDOM cnDom = (IDOM) fromPinDomGr.getDOMMap().get(GraphicalRepresentationDOM.NAME_CLASSNAME);
            final String cn = cnDom.elemGet();
            final RigidPinGR fromPin = (RigidPinGR)DynamicObjectLoader.loadGR(cn);
            fromPin.restoreFromDOM(fromPinDom);
            setFromPin(fromPin);
            final IDOM connectionsDom = (IDOM)domMap.get("connections");
            final Map<String, Object> connectionsMap = connectionsDom.getDOMMap();
            for (final var conObj : connectionsMap.values()) {
                if (conObj instanceof IDOM) {
                    final IDOM connnectionDom = (IDOM)conObj;
                    final OneConnectionGR con = (OneConnectionGR) DynamicObjectLoader.loadGR(OneConnectionGR.class.getName(), false);
                    con.restoreFromDOM(connnectionDom);
                    addConnection(con);
                }
            }
            notifyObservers();
        }
    }
    
    @Override
    public boolean isDOMValid(final IDOM dom) {
        Objects.requireNonNull(dom);
        final List<IOneConnectionGR> before = this.getSingleConnections();
        final var beforePin = this.getFromPin();
        try {
            ok(super.isDOMValid(dom), "FCE_GR " + OK.ERR_MSG_DOM_NOT_VALID, (new ModelFacade()).getMainFlowChart());
            deleteAllConnections();
            final Map<String, Object> domMap = dom.getDOMMap();
            ok(domMap.get("fromPin") instanceof IDOM, OK.ERR_MSG_WRONG_CAST);
            final IDOM fromPinDom = (IDOM)domMap.get("fromPin");
            final IDOM fromPinDomGr = (IDOM) fromPinDom.getDOMMap().get(GraphicalRepresentationDOM.NAME);           
            ok(fromPinDomGr.getDOMMap().get(GraphicalRepresentationDOM.NAME_CLASSNAME) instanceof IDOM, OK.ERR_MSG_WRONG_CAST);
            final IDOM cnDomFrom = (IDOM) fromPinDomGr.getDOMMap().get(GraphicalRepresentationDOM.NAME_CLASSNAME);
            final String cnFrom = cnDomFrom.elemGet();
            ok(cnFrom != null, OK.ERR_MSG_NULL);
            final RigidPinGR fromPin = ok(c -> (RigidPinGR) DynamicObjectLoader.loadGR(c), cnFrom);
            ok(fromPin.isDOMValid(fromPinDom), "FromPin " + OK.ERR_MSG_DOM_NOT_VALID);
            fromPin.restoreFromDOM(fromPinDom);
            setFromPin(fromPin);
            ok(domMap.get("connections") instanceof IDOM, OK.ERR_MSG_WRONG_CAST);
            final IDOM connectionsDom = (IDOM)domMap.get("connections");
            final Map<String, Object> connectionsMap = connectionsDom.getDOMMap();
            for (final var conObj : connectionsMap.values()) {
                if (conObj instanceof IDOM) {
                    final IDOM connectionDom = (IDOM)conObj;
                    final OneConnectionGR con = ok(d -> (OneConnectionGR) DynamicObjectLoader.loadGR(OneConnectionGR.class.getName(), false), "");
                    ok(con.isDOMValid(connectionDom), "OneConnectionGR " + OK.ERR_MSG_DOM_NOT_VALID);
                    con.restoreFromDOM(connectionDom);
                    ok(con.getToPin() instanceof RigidPinGR, OK.ERR_MSG_WRONG_CAST);
                    ok(isAddable(con), "OneConnectionGR " + con + " not addable");
                }
            }
            setFromPin(beforePin);
            before.forEach(c -> addConnection(c));
            return true;
        } catch (ParsingException e) {
            setFromPin(beforePin);
            before.forEach(c -> addConnection(c));
            e.getWarning().setAffectedElement((new ModelFacade()).getMainFlowChart()).reportWarning();
            return false;
        }
    }
}
