package org.jojo.flow.exc;

public class TimeoutException extends FlowException {
    /**
     * 
     */
    private static final long serialVersionUID = -6109476998462646513L;

    public TimeoutException(final Warning errorWarning) {
        super(errorWarning);
    }
}
