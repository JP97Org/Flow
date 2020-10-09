package org.jojo.flow.model.flowChart;

import static org.jojo.flow.model.storeLoad.OK.ok;

import java.awt.Point;
import java.util.Map;
import java.util.Objects;

import org.jojo.flow.model.storeLoad.DOM;
import org.jojo.flow.model.storeLoad.DynamicClassLoader;
import org.jojo.flow.model.storeLoad.GraphicalRepresentationDOM;
import org.jojo.flow.model.storeLoad.OK;
import org.jojo.flow.model.storeLoad.ParsingException;

public abstract class FlowChartElementGR extends GraphicalRepresentation {
    private LabelGR label;
    
    public FlowChartElementGR(final Point position) {
        super(position);
    }

    public final LabelGR getLabel() {
        return this.label;
    }

    public final void setLabel(final LabelGR label) {
        this.label = label;
        notifyObservers(label);
    }

    public final void removeLabel() {
        setLabel(null);
    }
    
    @Override
    public DOM getDOM() {
        final DOM dom = super.getDOM();
        if (this.label != null) {
            dom.appendCustomDOM("label", this.label);
        }
        return dom;
    }
    
    @Override
    public void restoreFromDOM(final DOM dom) {
        if (isDOMValid(dom)) {
            super.restoreFromDOM(dom);
            final Map<String, Object> domMap = dom.getDOMMap();
            if (domMap.containsKey("label")) {
                final DOM labelDom = (DOM)domMap.get("label");
                this.label = (LabelGR) DynamicClassLoader.loadGR(LabelGR.class.getName());
                final DOM grDom = (DOM)labelDom.getDOMMap().get(GraphicalRepresentationDOM.NAME);
                this.label.restoreFromDOM(grDom);
            }
        }
    }
    
    @Override
    public boolean isDOMValid(final DOM dom) {
        Objects.requireNonNull(dom);
        try {
            ok(super.isDOMValid(dom), "GR " + OK.ERR_MSG_DOM_NOT_VALID);
            final Map<String, Object> domMap = dom.getDOMMap();
            if (domMap.containsKey("label")) {
                ok(domMap.get("label") instanceof DOM, OK.ERR_MSG_WRONG_CAST);
                final DOM labelDom = (DOM)domMap.get("label");
                ok(g -> (LabelGR) DynamicClassLoader.loadGR(g), LabelGR.class.getName());
                ok(labelDom.getDOMMap().get(GraphicalRepresentationDOM.NAME) instanceof DOM, OK.ERR_MSG_WRONG_CAST);
                final DOM grDom = (DOM)labelDom.getDOMMap().get(GraphicalRepresentationDOM.NAME);
                ok(this.label.isDOMValid(grDom), "Label " + OK.ERR_MSG_DOM_NOT_VALID);
            }
            
            return true;
        } catch (ParsingException e) {
            e.getWarning().reportWarning();
            return false;
        }
    }
}
