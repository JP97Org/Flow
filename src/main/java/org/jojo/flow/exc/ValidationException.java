package org.jojo.flow.exc;

/**
 * This exception represents an exception which may occur if validation fails due to flow module
 * programming errors.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public class ValidationException extends FlowException {
    /**
     * 
     */
    private static final long serialVersionUID = 3919218411952699087L;

    public ValidationException(final Warning warning) {
        super(warning);
    }
}
