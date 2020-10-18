package org.jojo.flow.exc;

import org.jojo.flow.model.flowChart.FlowChartElement;

public class ParsingException extends FlowException {
    /**
     * 
     */
    private static final long serialVersionUID = 4015360889858586201L;

    public ParsingException(Warning errorWarning) {
        super(errorWarning);
    }

    public ParsingException(final Exception toWrap) {
        super(toWrap);
    }
    
    public ParsingException(final Exception toWrap, final FlowChartElement affectedElement) {
        super(toWrap, affectedElement);
    }
}
