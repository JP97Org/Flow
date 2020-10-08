package org.jojo.flow.view;

import java.util.ArrayList;
import java.util.List;

import org.jojo.flow.IObserver;
import org.jojo.flow.ISubject;

public abstract class Observer implements IObserver {
    private final ISubject observed;
    private final List<ISubject> otherObservedSubjects;
    
    public Observer(final ISubject observed) {
        this.observed = observed;
        this.otherObservedSubjects = new ArrayList<>();
    }
    
    public Observer(final ISubject observed, final List<ISubject> otherObservedSubjects) {
        this(observed);
        this.otherObservedSubjects.addAll(otherObservedSubjects);
    }
    
    public ISubject getObserved() {
        return this.observed;
    }
    
    public List<ISubject> getOtherObservedSubjects() {
        return new ArrayList<>(this.otherObservedSubjects);
    }
    
    protected void startObservingMainSubject() {
        this.observed.registerObserver(this);
    }
    
    protected void stopObservingMainSubject() {
        this.observed.unregisterObserver(this);
    }
    
    protected void startObservingOtherObservedSubject(final ISubject otherObservedSubject) {
        otherObservedSubject.registerObserver(this);
        this.otherObservedSubjects.add(otherObservedSubject);
    }
    
    protected boolean stopObserving(final ISubject otherObservedSubject) {
        if (this.otherObservedSubjects.contains(otherObservedSubject)) {
            this.otherObservedSubjects.removeIf(s -> s.equals(otherObservedSubject));
            otherObservedSubject.unregisterObserver(this);
            return true;
        }
        return false;
    }
    
    @Override
    public abstract void update(ISubject subject, Object argument);
    
    @Override
    public void update() {
        update(this.observed, this.observed);
    }
}
