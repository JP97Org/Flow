package org.jojo.flow.model.flowChart;

import org.jojo.flow.model.FlowException;
import org.jojo.flow.model.Warning;

public class ValidationException extends FlowException {
    /**
     * 
     */
    private static final long serialVersionUID = 3919218411952699087L;

    public ValidationException(final Warning warning) {
        super(warning);
    }
}
