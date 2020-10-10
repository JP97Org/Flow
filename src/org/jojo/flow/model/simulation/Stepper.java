package org.jojo.flow.model.simulation;

import java.util.Objects;

import org.jojo.flow.model.FlowException;
import org.jojo.flow.model.data.Fraction;
import org.jojo.flow.model.data.units.Frequency;
import org.jojo.flow.model.data.units.Time;

public abstract class Stepper implements IStepper, Runnable {
    private final Scheduler scheduler;
    
    public Stepper(final Scheduler scheduler) {
        this.scheduler = Objects.requireNonNull(scheduler);
    }
    
    public abstract void stepForward(Time<Fraction> time) throws ModuleRunException;
    
    public void stepReal(Time<Double> time) throws ModuleRunException {
        stepForward(Time.of(time.toFractionUnit()));
    }
    
    public abstract void stepForward() throws ModuleRunException;
    public abstract void stepOnce() throws ModuleRunException;
    
    public abstract void pause();
    public abstract boolean isPaused();
    public abstract void reset() throws FlowException;
    
    public abstract Frequency<Fraction> getFrequency();
    public abstract int getStepCount();
    public abstract Time<Fraction> getTimePassed();
    
    protected Scheduler getScheduler() {
        return this.scheduler;
    }
}
