package org.jojo.flow.api;

public interface IObserver extends IAPI {
    void update(ISubject subject, Object argument);
    void update();
}
