package org.jojo.flow.model.data;

import org.jojo.flow.model.FlowException;
import org.jojo.flow.model.Warning;

public class IllegalUnitOperationException extends FlowException {
    /**
     * 
     */
    private static final long serialVersionUID = 9209302819618647710L;

    public IllegalUnitOperationException(final String message) {
        super(new Warning(null, message, true));
    }
}
