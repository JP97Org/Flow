package org.jojo.flow;

public interface IObserver {
    void update(ISubject subject, Object argument);
    void update();
}
