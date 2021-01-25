package org.jojo.flow.model.simulation;

import java.util.Objects;

import org.jojo.flow.model.api.ISimulationConfiguration;
import org.jojo.flow.model.data.Fraction;
import org.jojo.flow.model.data.units.Frequency;
import org.jojo.flow.model.data.units.Time;

public class SimulationConfiguration implements ISimulationConfiguration {
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
    
    @Override
    public Frequency<Fraction> getStepperFrequency() {
        return this.stepperFrequency;
    }
    
    @Override
    public void setStepperFrequency(final Frequency<Fraction> stepperFrequency) {
        this.stepperFrequency = stepperFrequency;
    }
    
    @Override
    public Time<Double> getTimeout() {
        return this.timeout;
    }
    
    @Override
    public void setTimeout(final Time<Double> timeout) {
        this.timeout = Objects.requireNonNull(timeout);
    }

    @Override
    public boolean isRealtime() {
        return this.isRealtime;
    }

    @Override
    public void setRealtime(final boolean isRealtime) {
        this.isRealtime = isRealtime;
    }
}
