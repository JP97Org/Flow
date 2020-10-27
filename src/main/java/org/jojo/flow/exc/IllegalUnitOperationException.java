package org.jojo.flow.exc;

/**
 * This exception represents an exception which may occur during unit operations.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public class IllegalUnitOperationException extends FlowException {
    /**
     * 
     */
    private static final long serialVersionUID = 9209302819618647710L;

    public IllegalUnitOperationException(final String message) {
        super(new Warning(null, message, true));
    }
}
