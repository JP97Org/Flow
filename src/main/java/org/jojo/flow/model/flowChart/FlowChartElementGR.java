package org.jojo.flow.model.flowChart;

import static org.jojo.flow.model.util.OK.ok;

import java.awt.Point;
import java.util.Map;
import java.util.Objects;

import org.jojo.flow.exc.ParsingException;
import org.jojo.flow.model.api.DOMStringUnion;
import org.jojo.flow.model.api.IDOM;
import org.jojo.flow.model.api.IFlowChartElementGR;
import org.jojo.flow.model.api.ILabelGR;
import org.jojo.flow.model.storeLoad.GraphicalRepresentationDOM;
import org.jojo.flow.model.util.DynamicObjectLoader;
import org.jojo.flow.model.util.OK;

public abstract class FlowChartElementGR extends GraphicalRepresentation implements IFlowChartElementGR {
    private ILabelGR label;
    
    public FlowChartElementGR(final Point position) {
        super(position);
    }

    @Override
    public final ILabelGR getLabel() {
        return this.label;
    }

    @Override
    public final void setLabel(final ILabelGR label) {
        this.label = label;
        notifyObservers(label);
    }

    @Override
    public final void removeLabel() {
        setLabel(null);
    }
    
    @Override
    public IDOM getDOM() {
        final IDOM dom = super.getDOM();
        if (this.label != null) {
            dom.appendCustomDOM("label", this.label);
        }
        return dom;
    }
    
    @Override
    public void restoreFromDOM(final IDOM dom) {
        if (isDOMValid(dom)) {
            super.restoreFromDOM(dom);
            final Map<String, DOMStringUnion> domMap = dom.getDOMMap();
            if (domMap.containsKey("label")) {
                final IDOM labelDom = (IDOM)domMap.get("label").getValue();
                this.label = (LabelGR) DynamicObjectLoader.loadGR(LabelGR.class.getName());
                final IDOM grDom = (IDOM)labelDom.getDOMMap().get(GraphicalRepresentationDOM.NAME).getValue();
                this.label.restoreFromDOM(grDom);
            }
        }
    }
    
    @Override
    public boolean isDOMValid(final IDOM dom) {
        Objects.requireNonNull(dom);
        try {
            ok(super.isDOMValid(dom), "GR " + OK.ERR_MSG_DOM_NOT_VALID);
            final Map<String, DOMStringUnion> domMap = dom.getDOMMap();
            if (domMap.containsKey("label")) {
                ok(domMap.get("label").isDOM(), OK.ERR_MSG_WRONG_CAST);
                final IDOM labelDom = (IDOM)domMap.get("label").getValue();
                ok(g -> (LabelGR) DynamicObjectLoader.loadGR(g), LabelGR.class.getName());
                ok(labelDom.getDOMMap().get(GraphicalRepresentationDOM.NAME).isDOM(), OK.ERR_MSG_WRONG_CAST);
                final IDOM grDom = (IDOM)labelDom.getDOMMap().get(GraphicalRepresentationDOM.NAME).getValue();
                ok(this.label.isDOMValid(grDom), "Label " + OK.ERR_MSG_DOM_NOT_VALID);
            }
            
            return true;
        } catch (ParsingException e) {
            e.getWarning().reportWarning();
            return false;
        }
    }
}
