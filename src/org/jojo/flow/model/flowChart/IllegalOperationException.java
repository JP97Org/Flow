package org.jojo.flow.model.flowChart;

import org.jojo.flow.model.FlowException;
import org.jojo.flow.model.Warning;

public class IllegalOperationException extends FlowException {
    /**
     * 
     */
    private static final long serialVersionUID = -6766593136587984981L;

    public IllegalOperationException(final String message, final FlowChartElement affectedElement) {
        super(new Warning(affectedElement, message, true));
    }
    
    public IllegalOperationException(final Warning warning) {
        super(warning);
    }
}
