package org.jojo.flow.model.api;

import java.util.List;

/**
 * This interface represents an observer for observable {@link ISubject} instances.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public interface IObserver extends IAPI {
    
    /**
     * Gets the main observed subject.
     * 
     * @return the main observed subject
     */
    ISubject getObserved();
    
    /**
     * Gets a copy of the list of other observed subjects.
     * 
     * @return a copy of the list of other observed subjects
     */
    List<ISubject> getOtherObservedSubjects();
    
    /**
     * Updates the observer, i.e. informs it about a subject's state change.
     * 
     * @param subject - the subject whose state has changed (may but probably should not be {@code null})
     * @param argument - the argument trying to tell what has changed as exactly as possible (may be {@code null})
     */
    void update(ISubject subject, Object argument);
    
    /**
     * Updates the observer, i.e. informs it about a subject's state change.
     * Note that this method informs the observer as if the main observed subject with itself as
     * argument had informed this observer. Consider using {@link #update(ISubject, Object)} instead of 
     * this method.
     */
    void update();
}
