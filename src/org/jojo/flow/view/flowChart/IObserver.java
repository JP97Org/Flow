package org.jojo.flow.view.flowChart;

import org.jojo.flow.model.ISubject;

public interface IObserver {
    void update(ISubject subject, Object argument);
    void update();
}
