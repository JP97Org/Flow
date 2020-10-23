package org.jojo.flow.exc;

import org.jojo.flow.model.flowChart.FlowChartElement;

public class ConnectionException extends FlowException {
    /**
     * 
     */
    private static final long serialVersionUID = 3005776929136085943L;

    public ConnectionException(final String message, final FlowChartElement affectedElement) {
        super(new Warning(affectedElement, message, true));
    }
    
    public ConnectionException(final Warning warning) {
        super(warning);
    }
}
