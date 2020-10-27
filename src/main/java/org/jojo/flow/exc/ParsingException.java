package org.jojo.flow.exc;

import org.jojo.flow.model.flowChart.FlowChartElement;

/**
 * This exception represents an exception which may occur during parsing.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
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
