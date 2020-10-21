package org.jojo.flow.model.flowChart;

import static org.jojo.flow.model.storeLoad.OK.ok;

import java.awt.Point;
import java.util.Map;
import java.util.Objects;

import org.jojo.flow.exc.ParsingException;
import org.jojo.flow.model.ModelFacade;
import org.jojo.flow.model.Subject;
import org.jojo.flow.model.api.IFlowChartElement;
import org.jojo.flow.model.api.ILabelGR;
import org.jojo.flow.model.api.IObserver;
import org.jojo.flow.model.api.ISubject;
import org.jojo.flow.model.api.IDOM;
import org.jojo.flow.model.storeLoad.GraphicalRepresentationDOM;
import org.jojo.flow.model.storeLoad.OK;

public class LabelGR extends GraphicalRepresentation implements ISubject, ILabelGR {
    private final Subject subject;
    
    private String text;
    private IFlowChartElement element;
    
    private int height;
    private int width;
    
    public LabelGR(final IFlowChartElement element, final String text, 
            final Point position, final int height, final int width) {
        super(position);
        this.subject = Subject.getSubject(this);
        this.setText(text);
        this.element = element;
        this.setHeight(height);
        this.setWidth(width);
    }

    @Override
    public void registerObserver(IObserver observer) {
        this.subject.registerObserver(observer);
    }

    @Override
    public boolean unregisterObserver(IObserver observer) {
        return this.subject.unregisterObserver(observer);
    }

    @Override
    public void notifyObservers(Object argument) {
        this.subject.notifyObservers(argument);
    }

    @Override
    public void notifyObservers() {
        this.subject.notifyObservers();
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public void setText(final String text) {
        this.text = Objects.requireNonNull(text);
        notifyObservers(text);
    }

    @Override
    public IFlowChartElement getElement() {
        return element;
    }
    
    @Override
    public void setElement(final IFlowChartElement element) {
        this.element = element;
    }

    public int getHeight() {
        return this.height;
    }

    @Override
    public void setHeight(final int height) {
        this.height = height;
        notifyObservers(this.height);
    }

    public int getWidth() {
        return this.width;
    }

    @Override
    public void setWidth(final int width) {
        this.width = width;
        notifyObservers(this.width);
    }

    @Override
    public IDOM getDOM() {
        final GraphicalRepresentationDOM dom = (GraphicalRepresentationDOM) super.getDOM();
        dom.appendInt("element", getElement().getId());
        dom.appendString("text", getText());
        return dom;
    }

    @Override
    public void restoreFromDOM(final IDOM dom) {
        if (isDOMValid(dom)) {
            super.restoreFromDOM(dom);
            final Map<String, Object> domMap = dom.getDOMMap();
            final IDOM hDom = (IDOM) domMap.get(GraphicalRepresentationDOM.NAME_HEIGHT);
            final String hStr = hDom.elemGet();
            this.height = Integer.parseInt(hStr);
            final IDOM wDom = (IDOM) domMap.get(GraphicalRepresentationDOM.NAME_WIDTH);
            final String wStr = wDom.elemGet();
            this.width = Integer.parseInt(wStr);
            final IDOM elemDom = (IDOM)domMap.get("element");
            final String elemIdStr = elemDom.elemGet();
            final int elemId = Integer.parseInt(elemIdStr);
            this.element = (new ModelFacade()).getElementById(elemId);
            final IDOM strDom = (IDOM)domMap.get("text");
            this.text = strDom.elemGet();
            notifyObservers();
        }
    }

    @Override
    public boolean isDOMValid(final IDOM dom) {
        Objects.requireNonNull(dom);
        try {
            ok(super.isDOMValid(dom), "GR " + OK.ERR_MSG_DOM_NOT_VALID, (new ModelFacade()).getMainFlowChart());
            final Map<String, Object> domMap = dom.getDOMMap();
            ok(domMap.get(GraphicalRepresentationDOM.NAME_HEIGHT) instanceof IDOM, OK.ERR_MSG_WRONG_CAST);
            final IDOM hDom = (IDOM) domMap.get(GraphicalRepresentationDOM.NAME_HEIGHT);
            final String hStr = hDom.elemGet();
            ok(hStr != null, OK.ERR_MSG_NULL);
            ok(s -> Integer.parseInt(s), hStr);
            ok(domMap.get(GraphicalRepresentationDOM.NAME_WIDTH) instanceof IDOM, OK.ERR_MSG_WRONG_CAST);
            final IDOM wDom = (IDOM) domMap.get(GraphicalRepresentationDOM.NAME_WIDTH);
            final String wStr = wDom.elemGet();
            ok(wStr != null, OK.ERR_MSG_NULL);
            ok(s -> Integer.parseInt(s), wStr);
            ok(domMap.get("element") instanceof IDOM, OK.ERR_MSG_WRONG_CAST);
            final IDOM elemDom = (IDOM)domMap.get("element");
            final String elemIdStr = elemDom.elemGet();
            ok(elemIdStr != null, OK.ERR_MSG_NULL);
            final int elemId = ok(s -> Integer.parseInt(s), elemIdStr);
            IFlowChartElement element = (new ModelFacade()).getElementById(elemId);
            ok(element != null, OK.ERR_MSG_NULL);
            ok(domMap.get("text") instanceof IDOM, OK.ERR_MSG_WRONG_CAST);
            final IDOM strDom = (IDOM)domMap.get("text");
            String text = strDom.elemGet();
            ok(text != null, OK.ERR_MSG_NULL);
            return true;
        } catch (ParsingException e) {
            e.getWarning().setAffectedElement((new ModelFacade()).getMainFlowChart()).reportWarning();
            return false;
        }
    }
}
