package org.jojo.flow.model.api;

import org.jojo.flow.model.util.DynamicObjectLoader;

/**
 * This interface represents an external configuration of a flow module.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public interface IExternalConfig extends ISubject, IDOMable, Comparable<IExternalConfig> {
    
    /**
     * Gets the default implementation with mock values.
     * 
     * @return the default implementation
     * @see #getDefaultImplementation(String, int)
     */
    public static IExternalConfig getDefaultImplementation() {
        return DynamicObjectLoader.getNewMockExternalConfig();
    }
    
    /**
     * Gets the default implementation.
     * 
     * @param name - the name of this flow module (usually it is the the same for all instances of one
     * flow module class. However, individual naming is possible) (it must not be {@code null})
     * @param priority - the priority of the flow module of this config (higher value means higher priority, i.e. earlier
     * execution in simulation with an {@link org.jojo.flow.model.simulation.PriorityScheduler})
     * @return the default implementation
     */
    public static IExternalConfig getDefaultImplementation(final String name, final int priority) {
        return (IExternalConfig) IAPI.defaultImplementationOfThisApi(new Class<?>[] {String.class, int.class}, name, priority);
    }

    /**
     * Sets the flow module to which this external config is attached.
     * 
     * @param module - the flow module
     */
    void setModule(IFlowModule module);

    /**
     * Gets the name. It cannot be null.
     * 
     * @return the name 
     */
    String getName();
    
    /**
     * Sets the given name.
     * 
     * @param name - the given name (must not be {@code null})
     */
    void setName(String name);

    /**
     * Gets the priority of the flow module of this config (higher value means higher priority, i.e. earlier
     * execution in simulation with an {@link org.jojo.flow.model.simulation.PriorityScheduler}).
     * 
     * @return the priority of the flow module of this config
     */
    int getPriority();

    /**
     * Sets a new priority.
     * 
     * @param newPriority - the new priority
     * @see #getPriority()
     */
    void setPriority(int newPriority);

    /**
     * Gets the config as a pair of name and priority.
     * 
     * @return the config as a pair of name and priority
     * @see #getName()
     * @see #getPriority()
     */
    Pair<String, Integer> getConfig();
}