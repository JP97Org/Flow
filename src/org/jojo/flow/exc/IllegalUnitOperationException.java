package org.jojo.flow.exc;

public class IllegalUnitOperationException extends FlowException {
    /**
     * 
     */
    private static final long serialVersionUID = 9209302819618647710L;

    public IllegalUnitOperationException(final String message) {
        super(new Warning(null, message, true));
    }
}
