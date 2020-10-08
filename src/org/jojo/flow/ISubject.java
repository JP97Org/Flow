package org.jojo.flow;

public interface ISubject {
    void registerObserver(final IObserver observer);
    boolean unregisterObserver(final IObserver observer);
    void notifyObservers(final Object argument);
    void notifyObservers();
}
