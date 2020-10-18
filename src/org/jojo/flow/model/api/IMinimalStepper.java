package org.jojo.flow.model.api;

import org.jojo.flow.exc.ModuleRunException;
import org.jojo.flow.model.data.Fraction;
import org.jojo.flow.model.data.units.Time;

public interface IMinimalStepper extends IAPI {
    public static IMinimalStepper getDefaultImplementation(final IFlowChart flowChart, 
            final IScheduler scheduler, final Time<Fraction> explicitTimeStep, final boolean isRealtime) {
        return (IMinimalStepper) IAPI.defaultImplementationOfThisApi(
                new Class<?>[] {IFlowChart.class, IScheduler.class, Time.class, boolean.class}, 
                flowChart, scheduler, explicitTimeStep, isRealtime);
    }
    
    void performSimulationStep(Time<Fraction> time) throws ModuleRunException;
}
