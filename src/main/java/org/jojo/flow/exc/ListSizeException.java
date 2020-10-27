package org.jojo.flow.exc;

/**
 * This exception represents an exception which may occur during connecting to many connections to a
 * pin, especially to an input pin (which may be connected to maximum 1 connection).
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public class ListSizeException extends FlowException {
    /**
     * 
     */
    private static final long serialVersionUID = -5367914606404141211L;

    public ListSizeException(final Warning warning) {
        super(warning);
    }
}
