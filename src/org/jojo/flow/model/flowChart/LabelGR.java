package org.jojo.flow.model.flowChart;

import java.awt.Point;

import org.jojo.flow.model.ISubject;
import org.jojo.flow.model.Subject;
import org.jojo.flow.view.IObserver;

public class LabelGR extends GraphicalRepresentation implements ISubject {
    private final Subject subject;
    
    private String text;
    private final FlowChartElement element;
    
    public LabelGR(final FlowChartElement element, final String text, 
            final Point position, final FlowChartGR flowChartGR) {
        super(position, flowChartGR);
        this.subject = Subject.getSubject(this);
        this.setText(text);
        this.element = element;
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
        this.text = text;
    }

    @Override
    public int getHeigth() {
        // TODO Auto-generated method stub
        return 0; //TODO
    }

    @Override
    public int getWidth() {
        // TODO Auto-generated method stub
        return 0; //TODO
    }

}
