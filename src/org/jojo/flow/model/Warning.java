package org.jojo.flow.model;

import java.util.Objects;

import org.jojo.flow.model.flowChart.FlowChartElement;

public class Warning {
    private static int idCounter = 0;
    private final int id;
    private FlowChartElement affectedElement;
    private final String description;
    private boolean isError;
    
    public Warning(final FlowChartElement affectedElement, final String description) {
        this.id = idCounter++;
        this.affectedElement = affectedElement;
        this.description = Objects.requireNonNull(description);
        this.isError = false;
    }
    
    public Warning(final FlowChartElement affectedElement, final String description, final boolean isError) {
        this(affectedElement, description);
        this.isError = isError;
    }
    
    public Warning setToError() {
        this.isError = true;
        return this;
    }
    
    public Warning setAffectedElement(final FlowChartElement affectedElement) {
        this.affectedElement = affectedElement;
        return this;
    }
    
    public int getId() {
        return this.id;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public boolean hasAffectedElement() {
        return this.affectedElement != null;
    }
    
    public FlowChartElement getAffectedElement() {
        return this.affectedElement;
    }
    
    public boolean isError() {
        return this.isError;
    }
}
