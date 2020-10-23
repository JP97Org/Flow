package org.jojo.flow.model.api;

import java.util.List;

/**
 * This interface represents a a scheduler which can schedule a given list of flow modules.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public interface IScheduler extends IAPI {
    
    /**
     * Gets the default implementation which is a {@link org.jojo.flow.model.simulation.PriorityScheduler}.
     * @return the default implementation
     */
    public static IScheduler getDefaultImplementation() {
        return (IScheduler) IAPI.defaultImplementationOfThisApi(new Class<?>[] {});
    }

    /**
     * Gets the schedule for the given list of flow modules, i.e. the modules in the correct order, 
     * the modules which are to be run first are also earlier in the returned list. 
     * Note that the returned list is usually of the same size and contains the same
     * flow modules as the given list but it is also allowed that an implementation returns a list
     * with another size or even containing other flow modules.
     * However, if this is the case the implementation should inform the user about it.
     * 
     * @param modulesToStep - the given list of flow modules (must not be {@code null})
     * @return the schedule for the given list of flow modules
     */
    List<IFlowModule> getSchedule(List<IFlowModule> modulesToStep);
}