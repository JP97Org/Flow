package org.jojo.flow.view.flowChart;

import org.jojo.flow.model.ISubject;

public abstract class Observer implements IObserver {
    private final ISubject observed;
    
    public Observer(final ISubject observed) {
        this.observed = observed;
    }
    
    @Override
    public abstract void update(ISubject subject, Object argument);
    
    @Override
    public void update() {
        update(this.observed, this.observed);
    }
}
