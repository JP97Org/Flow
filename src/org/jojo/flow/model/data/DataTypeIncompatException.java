package org.jojo.flow.model.data;

import org.jojo.flow.model.FlowException;
import org.jojo.flow.model.Warning;

public class DataTypeIncompatException extends FlowException {

    /**
     * 
     */
    private static final long serialVersionUID = 5313682517234787403L;

    public DataTypeIncompatException(final String message) {
        super(new Warning(null, message, true));
    }
}
