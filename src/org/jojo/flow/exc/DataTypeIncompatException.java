package org.jojo.flow.exc;

public class DataTypeIncompatException extends FlowException {

    /**
     * 
     */
    private static final long serialVersionUID = 5313682517234787403L;

    public DataTypeIncompatException(final String message) {
        super(new Warning(null, message, true));
    }
}
