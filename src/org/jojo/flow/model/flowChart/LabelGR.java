package org.jojo.flow.model.flowChart;

import java.awt.Point;
import java.util.Objects;

import org.jojo.flow.IObserver;
import org.jojo.flow.ISubject;
import org.jojo.flow.model.Subject;
import org.jojo.flow.model.storeLoad.DOM;

public class LabelGR extends GraphicalRepresentation implements ISubject {
    private final Subject subject;
    
    private String text;
    private final FlowChartElement element;
    
    private int heigth;
    private int width;
    
    public LabelGR(final FlowChartElement element, final String text, 
            final Point position, final FlowChartGR flowChartGR, final int heigth, final int width) {
        super(position, flowChartGR);
        this.subject = Subject.getSubject(this);
        this.setText(text);
        this.element = Objects.requireNonNull(element);
        this.setHeigth(heigth);
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

    public int getHeigth() {
        return this.heigth;
    }

    public void setHeigth(final int heigth) {
        this.heigth = heigth;
        notifyObservers(this.heigth);
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
        // TODO Auto-generated method stub
        return null;
    }

}
