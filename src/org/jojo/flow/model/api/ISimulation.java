package org.jojo.flow.model.api;

import org.jojo.flow.exc.FlowException;
import org.jojo.flow.exc.ModuleRunException;
import org.jojo.flow.exc.TimeoutException;

/**
 * This interface represents the simulation facade.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public interface ISimulation extends IAPI {
    
    /**
     * Gets the default implementation for the given flow chart and ISimulationConfiguration.
     * 
     * @param flowChart - the given flow chart (must not be {@code null})
     * @param config - the given simulation configuration (must not be {@code null})
     * @return the default implementation
     */
    public static ISimulation getDefaultImplementation(final IFlowChart flowChart, final ISimulationConfiguration config) {
        return (ISimulation) IAPI.defaultImplementationOfThisApi(
                new Class<?>[] {IFlowChart.class, ISimulationConfiguration.class}, flowChart, config);
    }

    /**
     * Starts the simulation. If an exception occurs during simulation an error Warning is reported.
     * Moreover, the simulation and the simulation stepper run in a separate threads, so this method 
     * returns after it has started the simulation thread (which starts the stepper thread).
     * The simulation thread runs as long as the stepper is running. The stepper is running as long as
     * it is not paused or reset.
     * 
     * @see #stepOnce()
     * @see #pause()
     * @see #stop()
     */
    void start();

    /**
     * Steps exactly one step.
     * 
     * @throws ModuleRunException if an exception occurs during stepping or module execution
     * @throws TimeoutException if a timeout occurs
     * @throws FlowException if another kind of flow exception occurs
     * @see IStepper#stepOnce()
     */
    void stepOnce() throws ModuleRunException, TimeoutException, FlowException;

    /**
     * Stops this simulation by resetting the stepper. The step which is done at the moment is finished regularly
     * and the simulation is stopped afterwards. It may be that the last step is not counted to the old
     * ended simulation but to the new one, so call stop again after {@link #isRunning()} returns 
     * {@code false} to make sure the simulation is completely reset. If you do not want to stop the
     * simulation but want to only pause it, also e.g. for getting the passed time from the stepper, consider
     * using  {@link #pause()} instead.
     * 
     * @throws FlowException if time step cannot be reset
     * @see IStepper#reset()
     */
    void stop() throws FlowException;

    /**
     * Force-Stops the simulation by interrupting the stepper thread. Note that this method is an 
     * exceptional stopping of the simulation which does not necessarily provides a safe stopping
     * of the simulation, consider using {@link #stop()} for this purpose.
     */
    void forceStop();

    /**
     * Pauses the stepper.
     * 
     * @see IStepper#pause()
     */
    void pause();

    /**
     * Determines whether the simulation is running, i.e. whether the simulation thread is running.
     * 
     * @return whether the simulation is running
     */
    boolean isRunning();

    /**
     * Gets the simulation configuration.
     * 
     * @return the configuration
     * @throws IllegalStateException if simulation is running
     */
    ISimulationConfiguration getConfig() throws IllegalStateException;

    /**
     * Sets the given simulation configuration and reloads the stepper afterwards.
     * 
     * @param config - the configuration (must not be {@code null}
     * @throws IllegalStateException if simulation is running
     * @see #reloadStepper()
     */
    void setConfig(ISimulationConfiguration config) throws IllegalStateException;
    
    /**
     * Gets the stepper. However, the returned stepper should not be altered and no steps should
     * be directly performed on the returned stepper, consider using other methods in this facade
     * for this purpose.
     * 
     * @return the stepper (cannot be {@code null})
     */
    IStepper getStepper();

    /**
     * Re-initializes the stepper.
     */
    void reloadStepper();
}