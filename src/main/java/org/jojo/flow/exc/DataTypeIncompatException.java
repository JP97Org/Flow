package org.jojo.flow.exc;

/**
 * This exception represents an exception which may occur during data type operations.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public class DataTypeIncompatException extends FlowException {

    /**
     * 
     */
    private static final long serialVersionUID = 5313682517234787403L;

    public DataTypeIncompatException(final String message) {
        super(new Warning(null, message, true));
    }
}
