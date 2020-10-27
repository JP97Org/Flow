package org.jojo.flow.exc;

import java.util.Objects;

import org.jojo.flow.model.api.IFlowChartElement;

/**
 * This class represents the superclass for all checked exceptions defined in the Flow program.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public class FlowException extends Exception {
    /**
     * 
     */
    private static final long serialVersionUID = -2729748712056272179L;
    private final Warning errorWarning;
    
    /**
     * Creates a new flow exception with the given error warning. Moreover, the error warning is reported.
     * 
     * @param errorWarning - the error warning of this exception
     * @see Warning#reportWarning()
     */
    public FlowException(final Warning errorWarning) {
        super((!Objects.requireNonNull(errorWarning).hasAffectedElement() ? "an unknown source" : errorWarning.getAffectedElement()) + " reports this error: " +  errorWarning.getDescription());
        this.errorWarning = errorWarning;
        errorWarning.reportWarning();
    }
    
    /**
     * Wraps another flow exception but sets the given affected element and 
     * reports the error warning of the given flow exception again.
     * 
     * @param toWrap - the given flow exception
     * @param affectedElement - the affected flow chart element
     * @see Warning#reportWarning()
     */
    public FlowException(final FlowException toWrap, final IFlowChartElement affectedElement) {
        this(toWrap.getWarning().setAffectedElement(affectedElement));
    }
    
    /**
     * Wraps another exception and sets the given affected element and 
     * reports the error warning of this exception.
     * 
     * @param toWrap - the given other exception
     * @param affectedElement - the affected flow chart element
     * @see Warning#reportWarning()
     */
    public FlowException(final Exception toWrap, final IFlowChartElement affectedElement) {
        this(new Warning(affectedElement, toWrap.getMessage(), true));
    }
    
    /**
     * Wraps another exception and reports the error warning of this exception.
     * 
     * @param toWrap - the given other exception
     * @see Warning#reportWarning()
     */
    public FlowException(final Exception toWrap) {
        this(new Warning(null, toWrap.getMessage(), true));
    }
    
    /**
     * 
     * @return the warning of this flow exception
     */
    public Warning getWarning() {
        return this.errorWarning;
    }
}
