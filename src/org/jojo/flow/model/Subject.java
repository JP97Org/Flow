package org.jojo.flow.model;

import java.util.ArrayList;
import java.util.List;

import org.jojo.flow.view.flowChart.IObserver;

public abstract class Subject implements ISubject {
    private final List<IObserver> observerList;
    
    public Subject() {
        this.observerList = new ArrayList<>();
    }
    
    public static Subject getSubject(final ISubject toWrap) {
        if (toWrap instanceof Subject) {
            return (Subject)toWrap;
        }
        return new Subject() {};
    }
    
    @Override
    public void registerObserver(final IObserver observer) {
        this.observerList.add(observer);
    }
    
    @Override
    public boolean unregisterObserver(final IObserver observer) {
        return this.observerList.remove(observer);
    }
    
    @Override
    public void notifyObservers(final Object argument) {
        for (final IObserver observer : this.observerList) {
            observer.update(this, argument);
        }
    }
    
    @Override
    public void notifyObservers() {
        notifyObservers(this);
    }
}
