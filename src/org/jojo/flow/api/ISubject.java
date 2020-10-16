package org.jojo.flow.api;

public interface ISubject extends IAPI {
    void registerObserver(final IObserver observer);
    boolean unregisterObserver(final IObserver observer);
    void notifyObservers(final Object argument);
    void notifyObservers();
}
