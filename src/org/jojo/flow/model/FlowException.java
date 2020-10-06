package org.jojo.flow.model;

import java.util.Objects;

import org.jojo.flow.model.flowChart.FlowChartElement;

public class FlowException extends Exception {
    /**
     * 
     */
    private static final long serialVersionUID = -2729748712056272179L;
    private final Warning errorWarning;
    
    public FlowException(final Warning errorWarning) {
        super((!Objects.requireNonNull(errorWarning).hasAffectedElement() ? "an unknown source" : errorWarning.getAffectedElement()) + " reports this error: " +  errorWarning.getDescription());
        this.errorWarning = errorWarning;
    }
    
    public FlowException(final FlowException toWrap, final FlowChartElement affectedElement) {
        this(toWrap.getWarning().setAffectedElement(affectedElement));
    }
    
    public FlowException(final Exception toWrap, final FlowChartElement affectedElement) {
        this(new FlowException(toWrap), affectedElement);
    }
    
    public FlowException(final Exception toWrap) {
        this(new Warning(null, toWrap.getMessage(), true));
    }
    
    public Warning getWarning() {
        return this.errorWarning;
    }
}
