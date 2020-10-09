package org.jojo.flow.model.flowChart;

import static org.jojo.flow.model.storeLoad.OK.ok;

import java.awt.Point;
import java.util.Map;
import java.util.Objects;

import org.jojo.flow.IObserver;
import org.jojo.flow.ISubject;
import org.jojo.flow.model.ModelFacade;
import org.jojo.flow.model.Subject;
import org.jojo.flow.model.storeLoad.DOM;
import org.jojo.flow.model.storeLoad.GraphicalRepresentationDOM;
import org.jojo.flow.model.storeLoad.OK;
import org.jojo.flow.model.storeLoad.ParsingException;

public class LabelGR extends GraphicalRepresentation implements ISubject {
    private final Subject subject;
    
    private String text;
    private FlowChartElement element;
    
    private int height;
    private int width;
    
    public LabelGR(final FlowChartElement element, final String text, 
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

    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = Objects.requireNonNull(text);
        notifyObservers(text);
    }

    public FlowChartElement getElement() {
        return element;
    }
    
    public void setElement(final FlowChartElement element) {
        this.element = element;
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(final int height) {
        this.height = height;
        notifyObservers(this.height);
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(final int width) {
        this.width = width;
        notifyObservers(this.width);
    }

    @Override
    public DOM getDOM() {
        final GraphicalRepresentationDOM dom = (GraphicalRepresentationDOM) super.getDOM();
        dom.appendInt("element", getElement().getId());
        dom.appendString("text", getText());
        return dom;
    }

    @Override
    public void restoreFromDOM(final DOM dom) {
        if (isDOMValid(dom)) {
            super.restoreFromDOM(dom);
            final Map<String, Object> domMap = dom.getDOMMap();
            final DOM elemDom = (DOM)domMap.get("element");
            final String elemIdStr = elemDom.elemGet();
            final int elemId = Integer.parseInt(elemIdStr);
            this.element = (new ModelFacade()).getElementById(elemId);
            final DOM strDom = (DOM)domMap.get("text");
            this.text = strDom.elemGet();
            notifyObservers();
        }
    }

    @Override
    public boolean isDOMValid(final DOM dom) {
        Objects.requireNonNull(dom);
        try {
            ok(super.isDOMValid(dom), "GR " + OK.ERR_MSG_DOM_NOT_VALID, (new ModelFacade()).getFlowChart());
            final Map<String, Object> domMap = dom.getDOMMap();
            ok(domMap.get("element") instanceof DOM, OK.ERR_MSG_WRONG_CAST);
            final DOM elemDom = (DOM)domMap.get("element");
            final String elemIdStr = elemDom.elemGet();
            ok(elemIdStr != null, OK.ERR_MSG_NULL);
            final int elemId = ok(s -> Integer.parseInt(s), elemIdStr);
            FlowChartElement element = (new ModelFacade()).getElementById(elemId);
            ok(element != null, OK.ERR_MSG_NULL);
            ok(domMap.get("text") instanceof DOM, OK.ERR_MSG_WRONG_CAST);
            final DOM strDom = (DOM)domMap.get("text");
            String text = strDom.elemGet();
            ok(text != null, OK.ERR_MSG_NULL);
            return true;
        } catch (ParsingException e) {
            e.getWarning().setAffectedElement((new ModelFacade()).getFlowChart()).reportWarning();
            return false;
        }
    }
}
