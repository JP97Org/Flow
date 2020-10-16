package org.jojo.flow.model.api;

public interface IObserver extends IAPI {
    void update(ISubject subject, Object argument);
    void update();
}
