package org.jojo.flow.model.api;

import org.jojo.flow.model.data.Fraction;
import org.jojo.flow.model.data.units.Frequency;
import org.jojo.flow.model.data.units.Time;

public interface ISimulationConfiguration extends IAPI {
    public static ISimulationConfiguration getDefaultImplementation(final Frequency<Fraction> stepperFrequency, 
            final Time<Double> timeout, final boolean isRealtime) {
        return (ISimulationConfiguration) IAPI.defaultImplementationOfThisApi(
                new Class<?>[] {Frequency.class, Time.class, boolean.class}, stepperFrequency, timeout, isRealtime);
    }
    
    public static ISimulationConfiguration getDefaultImplementation(final Frequency<Fraction> stepperFrequency,
            final Time<Double> timeout) {
        return (ISimulationConfiguration) IAPI.defaultImplementationOfThisApi(
                new Class<?>[] {Frequency.class, Time.class}, stepperFrequency, timeout);
    }
    
    public static ISimulationConfiguration getDefaultImplementation(final Time<Double> timeout, 
            final boolean isRealtime) {
        return (ISimulationConfiguration) IAPI.defaultImplementationOfThisApi(
                new Class<?>[] {Time.class, boolean.class}, timeout, isRealtime);
    }
    
    public static ISimulationConfiguration getDefaultImplementation(final Time<Double> timeout) {
        return (ISimulationConfiguration) IAPI.defaultImplementationOfThisApi(
                new Class<?>[] {Time.class}, timeout);
    }

    Frequency<Fraction> getStepperFrequency();

    void setStepperFrequency(Frequency<Fraction> stepperFrequency);

    Time<Double> getTimeout();

    void setTimeout(Time<Double> timeout);

    boolean isRealtime();

    void setRealtime(boolean isRealtime);
}