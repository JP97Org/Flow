package org.jojo.flow.model.api;

/**
 * This interface represents a subject which can be observed by an {@link IObserver}.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public interface ISubject extends IAPI {
    
    /**
     * Registers the given observer.
     * 
     * @param observer - the given observer (must not be {@code null})
     */
    void registerObserver(final IObserver observer);
    
    /**
     * Unregisters the given observer.
     * 
     * @param observer - the given observer (must not be {@code null})
     * @return whether the observer was unregistered
     */
    boolean unregisterObserver(final IObserver observer);
    
    /**
     * Notifies all registered observers with the given argument as an argument trying to tell what has changed.
     * 
     * @param argument - an argument trying to tell what has changed
     */
    void notifyObservers(final Object argument);
    
    /**
     * Notifies all registered observers with this instance as the argument.
     * 
     * @see #notifyObservers(Object)
     */
    void notifyObservers();
}
