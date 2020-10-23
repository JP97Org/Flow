package org.jojo.flow.exc;

public class ListSizeException extends FlowException {
    /**
     * 
     */
    private static final long serialVersionUID = -5367914606404141211L;

    public ListSizeException(final Warning warning) {
        super(warning);
    }
}
