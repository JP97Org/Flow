package org.jojo.flow.exc;

public class ValidationException extends FlowException {
    /**
     * 
     */
    private static final long serialVersionUID = 3919218411952699087L;

    public ValidationException(final Warning warning) {
        super(warning);
    }
}
