package org.jojo.flow.model.simulation;

import org.jojo.flow.model.FlowException;
import org.jojo.flow.model.Warning;

public class TimeoutException extends FlowException {
    /**
     * 
     */
    private static final long serialVersionUID = -6109476998462646513L;

    public TimeoutException(final Warning errorWarning) {
        super(errorWarning);
    }
}
