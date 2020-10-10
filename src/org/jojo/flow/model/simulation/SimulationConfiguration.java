package org.jojo.flow.model.simulation;

import java.util.Objects;

import org.jojo.flow.model.data.Fraction;
import org.jojo.flow.model.data.units.Frequency;
import org.jojo.flow.model.data.units.Time;

public class SimulationConfiguration {
    private Frequency<Fraction> stepperFrequency;
    private Time<Double> timeout;
    
    public SimulationConfiguration(final Frequency<Fraction> stepperFrequency, final Time<Double> timeout) {
        this(timeout);
        this.stepperFrequency = stepperFrequency;
    }
    
    public SimulationConfiguration(final Time<Double> timeout) {
        this.timeout = Objects.requireNonNull(timeout);
    }
    
    public Frequency<Fraction> getStepperFrequency() {
        return this.stepperFrequency;
    }
    
    public void setStepperFrequency(final Frequency<Fraction> stepperFrequency) {
        this.stepperFrequency = stepperFrequency;
    }
    
    public Time<Double> getTimeout() {
        return this.timeout;
    }
    
    public void setTimeout(final Time<Double> timeout) {
        this.timeout = timeout;
    }
}
