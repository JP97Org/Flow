package org.jojo.flow.model.api;

import org.jojo.flow.exc.ModuleRunException;
import org.jojo.flow.model.data.Fraction;
import org.jojo.flow.model.data.units.Time;

/**
 * This interface represents a minimal interface for a simulation stepper.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 * @see IStepper
 */
public interface IMinimalStepper extends IAPI {
    
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
    public static IMinimalStepper getDefaultImplementation(final IFlowChart flowChart, 
            final IScheduler scheduler, final Time<Fraction> explicitTimeStep, final boolean isRealtime) {
        return (IMinimalStepper) IAPI.defaultImplementationOfThisApi(
                new Class<?>[] {IFlowChart.class, IScheduler.class, Time.class, boolean.class}, 
                flowChart, scheduler, explicitTimeStep, isRealtime);
    }
    
    /**
     * Performs simulation partial steps until the time has passed.
     * 
     * @param time - the given time
     * @throws ModuleRunException if an exception occurs during stepping
     */
    void performSimulationStep(Time<Fraction> time) throws ModuleRunException;
}
