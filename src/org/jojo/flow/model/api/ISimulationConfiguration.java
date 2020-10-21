package org.jojo.flow.model.api;

import org.jojo.flow.model.data.Fraction;
import org.jojo.flow.model.data.units.Frequency;
import org.jojo.flow.model.data.units.Time;

/**
 * This interface represents a configuration for a simulation.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public interface ISimulationConfiguration extends IAPI {
    
    /**
     * Gets the default implementation with an explicit stepper frequency.
     * 
     * @param stepperFrequency - explicit stepper frequency
     * @param timeout - timeout per step
     * @param isRealtime - determines whether the stepper steps in real time 
     * (do not set isRealtime if time step < 100ms (or max{frequencies} > 10Hz or if modules
     * take too much time for calculations). It is not ensured that the real time execution
     * is real time correct as modules may take longer to calculate than the defined real time step
     * @return the default implementation
     * @see #getDefaultImplementation(Time, boolean)
     */
    public static ISimulationConfiguration getDefaultImplementation(final Frequency<Fraction> stepperFrequency, 
            final Time<Double> timeout, final boolean isRealtime) {
        return (ISimulationConfiguration) IAPI.defaultImplementationOfThisApi(
                new Class<?>[] {Frequency.class, Time.class, boolean.class}, stepperFrequency, timeout, isRealtime);
    }
    
    /**
     * Gets the default implementation with an explicit stepper frequency.
     * 
     * @param stepperFrequency
     * @param timeout - the timeout per step
     * @return the default implementation
     * @see #getDefaultImplementation(Time)
     */
    public static ISimulationConfiguration getDefaultImplementation(final Frequency<Fraction> stepperFrequency,
            final Time<Double> timeout) {
        return (ISimulationConfiguration) IAPI.defaultImplementationOfThisApi(
                new Class<?>[] {Frequency.class, Time.class}, stepperFrequency, timeout);
    }
    
    /**
     * Gets the default implementation.
     * 
     * @param timeout - the timeout per step
     * @param isRealtime - determines whether the stepper steps in real time 
     * (do not set isRealtime if time step < 100ms (or max{frequencies} > 10Hz or if modules
     * take too much time for calculations). It is not ensured that the real time execution
     * is real time correct as modules may take longer to calculate than the defined real time step
     * @return the default implementation
     */
    public static ISimulationConfiguration getDefaultImplementation(final Time<Double> timeout, 
            final boolean isRealtime) {
        return (ISimulationConfiguration) IAPI.defaultImplementationOfThisApi(
                new Class<?>[] {Time.class, boolean.class}, timeout, isRealtime);
    }
    
    /**
     * Gets the default implementation.
     * 
     * @param timeout - the timeout per step
     * @return the default implementation
     */
    public static ISimulationConfiguration getDefaultImplementation(final Time<Double> timeout) {
        return (ISimulationConfiguration) IAPI.defaultImplementationOfThisApi(
                new Class<?>[] {Time.class}, timeout);
    }

    /**
     * Gets the stepper frequency.
     * 
     * @return the stepper frequency
     * @see IStepper#getFrequency()
     */
    Frequency<Fraction> getStepperFrequency();

    /**
     * Sets the stepper frequency.
     * 
     * @param stepperFrequency - the stepper frequency (must not be {@code null})
     * @see #getStepperFrequency()
     */
    void setStepperFrequency(Frequency<Fraction> stepperFrequency);

    /**
     * Gets the timeout per step.
     * 
     * @return the timeout per step (cannot be {@code null})
     */
    Time<Double> getTimeout();

    /**
     * Sets the timeout per step.
     * 
     * @param timeout - the timeout per step (must not be {@code null})
     */
    void setTimeout(Time<Double> timeout);

    /**
     * Determines whether the stepper tries to step in real time.
     * 
     * @return whether the stepper tries to step in real time
     */
    boolean isRealtime();

    /**
     * Sets whether the stepper should try to step in real time.
     * 
     * @param isRealtime - whether the stepper should try to step in real time
     */
    void setRealtime(boolean isRealtime);
}