package org.jojo.flow.model.simulation;

import java.util.Objects;

import org.jojo.flow.model.data.Fraction;
import org.jojo.flow.model.data.units.Frequency;
import org.jojo.flow.model.data.units.Time;

public class SimulationConfiguration {
    private Frequency<Fraction> stepperFrequency;
    private Time<Double> timeout;
    private boolean isRealtime;
    
    public SimulationConfiguration(final Frequency<Fraction> stepperFrequency, 
            final Time<Double> timeout, final boolean isRealtime) {
        this(stepperFrequency, timeout);
        this.setRealtime(isRealtime);
    }
    
    public SimulationConfiguration(final Frequency<Fraction> stepperFrequency, final Time<Double> timeout) {
        this(timeout);
        setStepperFrequency(stepperFrequency);
    }
    
    public SimulationConfiguration(final Time<Double> timeout, final boolean isRealtime) {
        this(timeout);
        setRealtime(isRealtime);
    }
    
    public SimulationConfiguration(final Time<Double> timeout) {
        this.timeout = Objects.requireNonNull(timeout);
        setRealtime(true);
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

    public boolean isRealtime() {
        return isRealtime;
    }

    public void setRealtime(final boolean isRealtime) {
        this.isRealtime = isRealtime;
    }
}
