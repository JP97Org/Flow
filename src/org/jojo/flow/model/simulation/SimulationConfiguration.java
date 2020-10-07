package org.jojo.flow.model.simulation;

import java.util.Objects;

import org.jojo.flow.model.data.Fraction;
import org.jojo.flow.model.data.Unit;

public class SimulationConfiguration {
    private Unit<Fraction> stepperFrequency;
    private Unit<Double> timeout;
    
    public SimulationConfiguration(final Unit<Fraction> stepperFrequency, final Unit<Double> timeout) {
        this(timeout);
        this.stepperFrequency = stepperFrequency;
    }
    
    public SimulationConfiguration(final Unit<Double> timeout) {
        this.timeout = Objects.requireNonNull(timeout);
    }
    
    public Unit<Fraction> getStepperFrequency() {
        return this.stepperFrequency;
    }
    
    public void setStepperFrequency(final Unit<Fraction> stepperFrequency) {
        this.stepperFrequency = stepperFrequency;
    }
    
    public Unit<Double> getTimeout() {
        return this.timeout;
    }
    
    public void setTimeout(final Unit<Double> timeout) {
        this.timeout = timeout;
    }
}
