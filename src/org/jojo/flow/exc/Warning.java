package org.jojo.flow.exc;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.jojo.flow.model.api.IFlowChartElement;
import org.jojo.flow.model.flowChart.FlowChartElement;

public class Warning {
    private static final List<Warning> warningLog = new ArrayList<>();
    
    private static int idCounter = 0;
    private final int id;
    private IFlowChartElement affectedElement;
    private final String description;
    private boolean isError;
    
    public Warning(final IFlowChartElement affectedElement, final String description) {
        this.id = idCounter++;
        this.affectedElement = affectedElement;
        this.description = Objects.requireNonNull(description);
        this.isError = false;
    }
    
    public Warning(final IFlowChartElement affectedElement, final String description, final boolean isError) {
        this(affectedElement, description);
        this.isError = isError;
    }
    
    public Warning(final Warning toCopy) {
        this(toCopy.affectedElement, toCopy.description);
    }
    
    public static List<Warning> getWarningLog() {
        return new ArrayList<Warning>(warningLog);
    }
    
    public static Warning getLastWarningOfWarningLog() {
        return warningLog.isEmpty() ? null : warningLog.get(warningLog.size() - 1);
    }
    
    public static void clearWarningLog() {
        warningLog.clear();
    }
    
    public Warning setToError() {
        this.isError = true;
        return this;
    }
    
    public Warning setAffectedElement(final IFlowChartElement affectedElement) {
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
    
    public IFlowChartElement getAffectedElement() {
        return this.affectedElement;
    }
    
    public boolean isError() {
        return this.isError;
    }
    
    public synchronized void reportWarning() {
        warningLog.add(this);
        if (hasAffectedElement()) {
            getAffectedElement().reportWarning(this);
        } else {
            FlowChartElement.GENERIC_ERROR_ELEMENT.reportWarning(this);
        }
    }
    
    @Override
    public String toString() {
        return (isError() ? "ERROR: " : "WARNING: ") + getDescription() + " was encountered in element with ID " + (hasAffectedElement() ? "" + getAffectedElement().getId() : "unknown element");
    }
    
    @Override
    public int hashCode() {
        return this.id;
    }
    
    public boolean equals(final Object other) {
        if (other instanceof Warning) {
            return hashCode() == other.hashCode();
        }
        return false;
    }
}
