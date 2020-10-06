package org.jojo.flow.view.flowChart;

import org.jojo.flow.model.Subject;

public abstract class Observer {
    private final Subject observed;
    
    public Observer(final Subject observed) {
        this.observed = observed;
    }
    
    public abstract void update(Subject subject, Object argument);
    
    public void update() {
        update(this.observed, this.observed);
    }
}
