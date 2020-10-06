package org.jojo.flow.model;

import java.util.ArrayList;
import java.util.List;

import org.jojo.flow.view.flowChart.Observer;

public abstract class Subject {
    private final List<Observer> observerList;
    
    public Subject() {
        this.observerList = new ArrayList<>();
    }
    
    public void registerObserver(final Observer observer) {
        this.observerList.add(observer);
    }
    
    public boolean unregisterObserver(final Observer observer) {
        return this.observerList.remove(observer);
    }
    
    public void notifyObservers(final Object argument) {
        for (final Observer observer : this.observerList) {
            observer.update(this, argument);
        }
    }
    
    public void notifyObservers() {
        notifyObservers(this);
    }
}
