package org.jojo.flow.model.flowChart;

import static org.jojo.flow.model.storeLoad.OK.ok;

import java.awt.Point;
import java.util.Map;
import java.util.Objects;

import javax.swing.Icon;

import org.jojo.flow.model.Subject;
import org.jojo.flow.model.storeLoad.DOM;
import org.jojo.flow.model.storeLoad.DOMable;
import org.jojo.flow.model.storeLoad.GraphicalRepresentationDOM;
import org.jojo.flow.model.storeLoad.OK;
import org.jojo.flow.model.storeLoad.ParsingException;
import org.jojo.flow.model.storeLoad.PointDOM;

public abstract class GraphicalRepresentation extends Subject implements DOMable {
    private Point position;
    private Icon defaultIcon;
    private Icon selectedIcon;
    
    public GraphicalRepresentation(final Point position) {
        this.setPosition(position);
    }

    public Point getPosition() {
        return this.position;
    }

    public void setPosition(final Point position) {
        this.position = Objects.requireNonNull(position);
        notifyObservers(position);
    }
    
    public abstract int getHeight();
    public abstract int getWidth();

    public Icon getDefaultIcon() {
        return this.defaultIcon;
    }

    public void setDefaultIcon(final Icon defaultIcon) {
        this.defaultIcon = Objects.requireNonNull(defaultIcon);
        notifyObservers(defaultIcon);
    }

    public Icon getSelectedIcon() {
        return this.selectedIcon;
    }

    public void setSelectedIcon(final Icon selectedIcon) {
        this.selectedIcon = Objects.requireNonNull(selectedIcon);
        notifyObservers(selectedIcon);
    }
    
    @Override
    public DOM getDOM() {
        final GraphicalRepresentationDOM dom = new GraphicalRepresentationDOM();
        dom.setClassName(getClass().getName());
        dom.setPosition(getPosition());
        dom.setHeight(getHeight());
        dom.setWidth(getWidth());
        dom.appendString("defaultIcon", "TODO"); //TODO className of icon
        dom.appendString("selectedIcon", "TODO"); //TODO className of icon
        return dom;
    }
    
    @Override
    public void restoreFromDOM(final DOM dom) {
        if (isDOMValid(dom)) {
            Map<String, Object> domMap = dom.getDOMMap();
            if (domMap.containsKey(GraphicalRepresentationDOM.NAME)) {
                domMap = ((DOM)domMap.get(GraphicalRepresentationDOM.NAME)).getDOMMap();
            }
            final DOM posDom = (DOM)domMap.get(GraphicalRepresentationDOM.NAME_POSITION);
            this.position = PointDOM.pointOf(posDom);
            //TODO icons. Height and width can be done in subclasses
        }
    }
    
    @Override
    public boolean isDOMValid(final DOM dom) {
        Objects.requireNonNull(dom);
        try {
            Map<String, Object> domMap = dom.getDOMMap();
            if (domMap.containsKey(GraphicalRepresentationDOM.NAME)) {
                ok(domMap.get(GraphicalRepresentationDOM.NAME) instanceof DOM, OK.ERR_MSG_WRONG_CAST);
                domMap = ((DOM)domMap.get(GraphicalRepresentationDOM.NAME)).getDOMMap();
            }
            ok(domMap.get(GraphicalRepresentationDOM.NAME_CLASSNAME) instanceof DOM, OK.ERR_MSG_WRONG_CAST);
            final DOM cnDom = (DOM)domMap.get(GraphicalRepresentationDOM.NAME_CLASSNAME);
            final String cn = cnDom.elemGet();
            ok(cn != null, OK.ERR_MSG_NULL);
            ok(cn.equals(getClass().getName()), OK.ERR_MSG_WRONG_CAST);
            ok(domMap.get(GraphicalRepresentationDOM.NAME_POSITION) instanceof DOM, OK.ERR_MSG_WRONG_CAST);
            final DOM posDom = (DOM)domMap.get(GraphicalRepresentationDOM.NAME_POSITION);
            ok(p -> PointDOM.pointOf(p), posDom);
            //TODO icons. Height and width can be done in subclasses
            return true;
        } catch (ParsingException e) {
            e.getWarning().reportWarning();
            return false;
        }
    }
}
