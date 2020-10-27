package org.jojo.flow.exc;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.jojo.flow.model.api.IFlowChartElement;
import org.jojo.flow.model.flowChart.FlowChartElement;

/**
 * This class represents a warning which can either be a normal warning or an error warning. It can be
 * used to indicate errors occurring in the program flow.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public class Warning {
    private static final List<Warning> warningLog = new ArrayList<>();
    
    private static int idCounter = 0;
    private final int id;
    private IFlowChartElement affectedElement;
    private final String description;
    private boolean isError;
    
    /**
     * Creates a new normal warning with the given affected element and the given description.
     * 
     * @param affectedElement - the affected flow chart element
     * @param description - the given description (must not be {@code null})
     */
    public Warning(final IFlowChartElement affectedElement, final String description) {
        this.id = idCounter++;
        this.affectedElement = affectedElement;
        this.description = Objects.requireNonNull(description);
        this.isError = false;
    }
    
    /**
     * Creates a new warning with the given affected element, the given description and the given error
     * level, i.e. whether it is an error warning or not.
     * 
     * @param affectedElement - the affected flow chart element
     * @param description - the given description (must not be {@code null})
     * @param isError - whether this warning is an error warning
     */
    public Warning(final IFlowChartElement affectedElement, final String description, final boolean isError) {
        this(affectedElement, description);
        this.isError = isError;
    }
    
    /**
     * Copy-constructor. (id is not copied)
     * 
     * @param toCopy
     */
    public Warning(final Warning toCopy) {
        this(toCopy.affectedElement, toCopy.description, toCopy.isError);
    }
    
    /**
     * Gets the warning log.
     * 
     * @return the warning log
     */
    public static List<Warning> getWarningLog() {
        return new ArrayList<Warning>(warningLog);
    }
    
    /**
     * Gets the last warning of the warning log or {@code null} if none exists.
     * @return
     */
    public static Warning getLastWarningOfWarningLog() {
        return warningLog.isEmpty() ? null : warningLog.get(warningLog.size() - 1);
    }
    
    /**
     * Clears the warning log.
     */
    public static void clearWarningLog() {
        warningLog.clear();
    }
    
    /**
     * Sets this warning to an error warning.
     * 
     * @return this warning as an error warning
     */
    public Warning setToError() {
        this.isError = true;
        return this;
    }
    
    /**
     * Sets the given affected element.
     * 
     * @param affectedElement - the affected flow chart element
     * @return this warning
     */
    public Warning setAffectedElement(final IFlowChartElement affectedElement) {
        this.affectedElement = affectedElement;
        return this;
    }
    
    /**
     * 
     * @return the id
     */
    public int getId() {
        return this.id;
    }
    
    /**
     * 
     * @return the description
     */
    public String getDescription() {
        return this.description;
    }
    
    /**
     * Determines whether this warning has an affected element.
     * 
     * @return whether this warning has an affected element
     */
    public boolean hasAffectedElement() {
        return this.affectedElement != null;
    }
    
    /**
     * 
     * @return the affected element or {@code null} if none exists
     */
    public IFlowChartElement getAffectedElement() {
        return this.affectedElement;
    }
    
    /**
     * Determines whether this warning is an error warning.
     * 
     * @return whether this warning is an error warning
     */
    public boolean isError() {
        return this.isError;
    }
    
    /**
     * Reports this warning to 
     * <ul>
     * <li> the warning log and </li>
     * <li> the affected element or the {@link IFlowChartElement#GENERIC_ERROR_ELEMENT} 
     * iff no affected element exists. </li>
     * </ul>
     */
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
    
    @Override
    public boolean equals(final Object other) {
        if (other instanceof Warning) {
            return hashCode() == other.hashCode();
        }
        return false;
    }
}
