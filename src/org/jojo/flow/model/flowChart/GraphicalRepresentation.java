package org.jojo.flow.model.flowChart;

import static org.jojo.flow.model.storeLoad.OK.ok;

import java.awt.Point;
import java.util.Map;
import java.util.Objects;

import javax.swing.Icon;

import org.jojo.flow.exc.ParsingException;
import org.jojo.flow.model.Subject;
import org.jojo.flow.model.api.IDOM;
import org.jojo.flow.model.api.IDOMable;
import org.jojo.flow.model.api.IGraphicalRepresentation;
import org.jojo.flow.model.storeLoad.GraphicalRepresentationDOM;
import org.jojo.flow.model.storeLoad.OK;
import org.jojo.flow.model.storeLoad.PointDOM;

public abstract class GraphicalRepresentation extends Subject implements IDOMable, IGraphicalRepresentation {
    private Point position;
    private Icon defaultIcon;
    private Icon selectedIcon;
    
    public GraphicalRepresentation(final Point position) {
        this.setPosition(position);
    }

    @Override
    public Point getPosition() {
        return this.position;
    }

    @Override
    public void setPosition(final Point position) {
        this.position = Objects.requireNonNull(position);
        notifyObservers(position);
    }
    
    @Override
    public abstract int getHeight();
    @Override
    public abstract int getWidth();

    @Override
    public Icon getDefaultIcon() {
        return this.defaultIcon;
    }

    @Override
    public void setDefaultIcon(final Icon defaultIcon) {
        this.defaultIcon = Objects.requireNonNull(defaultIcon);
        notifyObservers(defaultIcon);
    }

    @Override
    public Icon getSelectedIcon() {
        return this.selectedIcon;
    }

    @Override
    public void setSelectedIcon(final Icon selectedIcon) {
        this.selectedIcon = Objects.requireNonNull(selectedIcon);
        notifyObservers(selectedIcon);
    }
    
    @Override
    public IDOM getDOM() {
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
    public void restoreFromDOM(final IDOM dom) {
        if (isDOMValid(dom)) {
            Map<String, Object> domMap = dom.getDOMMap();
            if (domMap.containsKey(GraphicalRepresentationDOM.NAME)) {
                domMap = ((IDOM)domMap.get(GraphicalRepresentationDOM.NAME)).getDOMMap();
            }
            final IDOM posDom = (IDOM)domMap.get(GraphicalRepresentationDOM.NAME_POSITION);
            this.position = PointDOM.pointOf(posDom);
            //TODO icons. Height and width can be done in subclasses
        }
    }
    
    @Override
    public boolean isDOMValid(final IDOM dom) {
        Objects.requireNonNull(dom);
        try {
            Map<String, Object> domMap = dom.getDOMMap();
            if (domMap.containsKey(GraphicalRepresentationDOM.NAME)) {
                ok(domMap.get(GraphicalRepresentationDOM.NAME) instanceof IDOM, OK.ERR_MSG_WRONG_CAST);
                domMap = ((IDOM)domMap.get(GraphicalRepresentationDOM.NAME)).getDOMMap();
            }
            ok(domMap.get(GraphicalRepresentationDOM.NAME_CLASSNAME) instanceof IDOM, OK.ERR_MSG_WRONG_CAST);
            final IDOM cnDom = (IDOM)domMap.get(GraphicalRepresentationDOM.NAME_CLASSNAME);
            final String cn = cnDom.elemGet();
            ok(cn != null, OK.ERR_MSG_NULL);
            ok(cn.equals(getClass().getName()), OK.ERR_MSG_WRONG_CAST);
            ok(domMap.get(GraphicalRepresentationDOM.NAME_POSITION) instanceof IDOM, OK.ERR_MSG_WRONG_CAST);
            final IDOM posDom = (IDOM)domMap.get(GraphicalRepresentationDOM.NAME_POSITION);
            ok(p -> PointDOM.pointOf(p), posDom);
            //TODO icons. Height and width can be done in subclasses
            return true;
        } catch (ParsingException e) {
            e.getWarning().reportWarning();
            return false;
        }
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " with position= " + getPosition();
    }
}
