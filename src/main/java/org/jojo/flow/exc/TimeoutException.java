package org.jojo.flow.exc;

/**
 * This exception represents an exception which may occur if a timeout occurs.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public class TimeoutException extends FlowException {
    /**
     * 
     */
    private static final long serialVersionUID = -6109476998462646513L;

    public TimeoutException(final Warning errorWarning) {
        super(errorWarning);
    }
}
