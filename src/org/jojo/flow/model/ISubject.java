package org.jojo.flow.model;

import org.jojo.flow.view.IObserver;

public interface ISubject {
    void registerObserver(final IObserver observer);
    boolean unregisterObserver(final IObserver observer);
    void notifyObservers(final Object argument);
    void notifyObservers();
}
