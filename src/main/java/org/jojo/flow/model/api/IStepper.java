package org.jojo.flow.model.api;

import org.jojo.flow.exc.FlowException;
import org.jojo.flow.exc.ModuleRunException;
import org.jojo.flow.model.data.Fraction;
import org.jojo.flow.model.data.units.Frequency;
import org.jojo.flow.model.data.units.Time;

/**
 * This interface represents an interface for a simulation stepper.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 * @see IMinimalStepper
 */
public interface IStepper extends IMinimalStepper, Runnable {
    
    /**
     * Gets the default implementation (a scheduling stepper).
     * 
     * @param flowChart - the flow chart for the simulation (must not be {@code null})
     * @param scheduler - the scheduler
     * @param explicitTimeStep - defines the minimal time step duration explicitly 
     * (should be {@code null}) for automatic minimal time step
     * @param isRealtime - determines whether the stepper steps in real time 
     * (do not set isRealtime if time step < 100ms (or max{frequencies} > 10Hz or if modules
     * take too much time for calculations). It is not ensured that the real time execution
     * is real time correct as modules may take longer to calculate than the defined real time step
     * @return the default implementation (a scheduling stepper)
     */
    public static IStepper getDefaultImplementation(final IFlowChart flowChart, 
            final IScheduler scheduler, final Time<Fraction> explicitTimeStep, final boolean isRealtime) {
        return (IStepper) IAPI.defaultImplementationOfThisApi(
                new Class<?>[] {IFlowChart.class, IScheduler.class, Time.class, boolean.class}, 
                flowChart, scheduler, explicitTimeStep, isRealtime);
    }

    /**
     * Performs simulation partial steps until the time has passed.
     * 
     * @param time - the given time
     * @throws ModuleRunException if an exception occurs during stepping
     * @see IMinimalStepper#performSimulationStep(Time)
     * @see #stepOnce()
     */
    void stepForward(Time<Fraction> time) throws ModuleRunException;

    /**
     * Performs simulation partial steps until the time has passed.
     * 
     * @param time - the given time
     * @throws ModuleRunException if an exception occurs during stepping
     * @see #stepForward(Time)
     * @see #stepOnce()
     */
    void stepReal(Time<Double> time) throws ModuleRunException;

    /**
     * Performs simulation partial steps until this stepper is paused or reset.
     * 
     * @throws ModuleRunException if an exception occurs during stepping
     * @see #pause()
     * @see #reset()
     * @see #stepOnce()
     */
    void stepForward() throws ModuleRunException;

    /**
     * Steps one minimal partial step, i.e. an explicit or implicit minimal time step.
     * Note that all warnings of the warnings lists of modules are set resolved in order to be able
     * to add possibly occurring new warnings.
     * 
     * @throws ModuleRunException if an exception occurs during stepping
     */
    void stepOnce() throws ModuleRunException;

    /**
     * Unpauses this stepper.
     */
    void unpause();

    /**
     * Pauses this stepper.
     * 
     * @see #reset()
     */
    void pause();

    /**
     * Determines whether this stepper is paused.
     * 
     * @return whether this stepper is paused
     */
    boolean isPaused();

    /**
     * Resets this stepper, i.e. pauses it and then resets the time step, the step count and the time passed.
     * @throws FlowException if time step cannot be reset
     * @see #pause()
     */
    void reset() throws FlowException;

    /**
     * Gets the frequency of this stepper, i.e. the reciprocal of the minimal time step.
     * 
     * @return the frequency of this stepper
     */
    Frequency<Fraction> getFrequency();

    /**
     * Gets the step count, i.e. the count of performed {@link #stepOnce()} steps since last reset.
     * 
     * @return the step count
     */
    int getStepCount();

    /**
     * Gets the time passed, i.e. the time passed since last reset.
     * 
     * @return the time passed
     */
    Time<Fraction> getTimePassed();
}