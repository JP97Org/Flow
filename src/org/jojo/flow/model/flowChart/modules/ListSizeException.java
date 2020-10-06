package org.jojo.flow.model.flowChart.modules;

import org.jojo.flow.model.FlowException;
import org.jojo.flow.model.Warning;

public class ListSizeException extends FlowException {
    /**
     * 
     */
    private static final long serialVersionUID = -5367914606404141211L;

    public ListSizeException(final Warning warning) {
        super(warning);
    }
}
