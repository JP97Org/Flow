package org.jojo.flow.model.flowChart;

import java.awt.Point;
import java.util.Objects;

import org.jojo.flow.IObserver;
import org.jojo.flow.ISubject;
import org.jojo.flow.model.Subject;
import org.jojo.flow.model.storeLoad.DOM;
import org.jojo.flow.model.storeLoad.GraphicalRepresentationDOM;

public class LabelGR extends GraphicalRepresentation implements ISubject {
    private final Subject subject;
    
    private String text;
    private final FlowChartElement element;
    
    private int height;
    private int width;
    
    public LabelGR(final FlowChartElement element, final String text, 
            final Point position, final FlowChartGR flowChartGR, final int height, final int width) {
        super(position);
        this.subject = Subject.getSubject(this);
        this.setText(text);
        this.element = Objects.requireNonNull(element);
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
        final GraphicalRepresentationDOM dom = new GraphicalRepresentationDOM();
        dom.setClassName(getClass().getName());
        dom.setPosition(getPosition());
        dom.setHeight(getHeight());
        dom.setWidth(getWidth());
        dom.appendInt("element", getElement().getId());
        dom.appendString("text", getText());
        return dom;
    }

    @Override
    public void restoreFromDOM(DOM dom) {
        // TODO Auto-generated method stub
        
    }

}
